from concurrent import futures
import grpc
import os
import sys
import logging
import time
from datetime import datetime

# Path configuration
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'generated'))

# Import modules
import importlib
gen_pb2 = importlib.import_module('app.generated.ai_service_pb2')
sys.modules['ai_service_pb2'] = gen_pb2
from app.generated import ai_service_pb2_grpc, ai_service_pb2
from app.llm import prompt_templates
from app.llm.client_factory import LLMClientFactory

# Constants
class ServerConstants:
    # Server configuration
    SERVER_PORT = '[::]:50051'
    MAX_WORKERS = 10
    LOG_FILE = 'ai_service.log'
    
    # Default values
    DEFAULT_MODEL = "ollama"
    DEFAULT_METHOD = "GET"
    DEFAULT_CONFIDENCE_SCORE = 1.0
    DEFAULT_TENANT_ID = "unknown"
    DEFAULT_OLLAMA_HOST = "http://localhost:11434"
    SELENIUM_SPEED = 0.1
    
    # Model identifiers
    GROK_MODEL_IDENTIFIER = "grok"
    
    # Template names
    TEMPLATE_API_TESTING = "API_TESTING_SCENARIOS"
    TEMPLATE_JIRA_STORIES = "JIRA_STORIES"
    TEMPLATE_PROMPT_AUTOMATION = "PROMPT_BASED_AUTOMATION"
    
    # Error messages
    ERROR_TEXT_REQUIRED = "Text content is required for test case generation. Provide 'prompt' or 'parameters' with 'text'."
    ERROR_PROMPT_REQUIRED = "Prompt is required for general chat."
    ERROR_SRS_REQUIRED = "SRS text is required"
    ERROR_EMPTY_LLM_RESPONSE = "Empty response received from LLM"
    ERROR_NO_LLM_RESPONSE = "No response from LLM."
    
    # Logging messages
    LOG_INITIALIZING = "Initializing AiServiceServicer"
    LOG_SERVER_STARTING = "Starting gRPC server..."
    LOG_THREAD_POOL_INIT = "gRPC server thread pool initialized"
    
    # Response modes
    MODE_TEXT_GENERATION = "text_based_test_generation"
    MODE_GENERAL_CHAT = "general_chat"
    
    # Log truncation
    LOG_TRUNCATE_LENGTH = 200


class AiServiceServicer(ai_service_pb2_grpc.AiServiceServicer):
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.logger.info(ServerConstants.LOG_INITIALIZING)

    def _get_llm_client(self, model: str):
        """Get the appropriate LLM client based on model name."""
        try:
            if model and ServerConstants.GROK_MODEL_IDENTIFIER in model.lower():
                self.logger.info(f"Using Grok client for model: {model}")
                return LLMClientFactory.get_client(ServerConstants.GROK_MODEL_IDENTIFIER)
            
            self.logger.info(f"Using Ollama client for model: {model}")
            return LLMClientFactory.get_client(ServerConstants.DEFAULT_MODEL)
        except Exception as e:
            self.logger.error(f"Error getting LLM client: {str(e)}")
            raise

    def GenerateResponseForCodeGeneratorWithText(self, request, context):
        logging.info(f"Received gRPC test case with text request: {request}")
        
        # Try to get text from either prompt field or parameters
        user_text = getattr(request, "prompt", "")
        
        # Check parameters for text if prompt is empty
        if not user_text and hasattr(request, 'parameters') and 'text' in request.parameters:
            user_text = request.parameters['text']
        
        # Default metadata
        metadata = {
            "mode": ServerConstants.MODE_TEXT_GENERATION,
            "ollama_host": os.getenv("OLLAMA_HOST", ServerConstants.DEFAULT_OLLAMA_HOST)
        }
        
        # Get model from parameters if specified
        model = ServerConstants.DEFAULT_MODEL
        if hasattr(request, 'parameters') and request.parameters.get("model"):
            model = request.parameters.get("model")
        
        # Validate input
        if not user_text:
            logging.error(ServerConstants.ERROR_TEXT_REQUIRED)
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(ServerConstants.ERROR_TEXT_REQUIRED)
            return ai_service_pb2.AiResponse(metadata=metadata)
        
        # Get tenant ID if available
        tenant_id = getattr(request, "tenant_id", ServerConstants.DEFAULT_TENANT_ID)
        logging.info(f"Processing request for tenant: {tenant_id}, model: {model}")
        
        try:
            # Import prompt template
            from app.llm.prompt import TEST_CASE_GENERATOR_WITH_TEXT
            
            # Format the prompt with the user's text
            formatted_prompt = TEST_CASE_GENERATOR_WITH_TEXT.format(user_text=user_text)
            logging.debug(f"Formatted prompt: {formatted_prompt[:ServerConstants.LOG_TRUNCATE_LENGTH]}...")
            
            # Get LLM client and generate response
            client = self._get_llm_client(model)
            messages = [{"role": "user", "content": formatted_prompt}]
            ai_text = client.chat(messages)
            
            if not ai_text:
                raise ValueError(ServerConstants.ERROR_EMPTY_LLM_RESPONSE)
                
            logging.info(f"Successfully generated response of length: {len(ai_text)}")
            
            # Return successful response
            return ai_service_pb2.AiResponse(
                response_text=ai_text,
                confidence_score=ServerConstants.DEFAULT_CONFIDENCE_SCORE,
                metadata=metadata
            )
            
        except Exception as e:
            error_msg = f"Error generating response: {str(e)}"
            logging.error(error_msg, exc_info=True)
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(error_msg)
            return ai_service_pb2.AiResponse(
                response_text="",
                confidence_score=0.0,
                metadata={"error": error_msg}
            )

    def GenerateResponseForGeneralChat(self, request, context):
        logging.info(f"Received gRPC general chat request: {request}")
        
        prompt = getattr(request, "prompt", None)
        metadata = {"mode": ServerConstants.MODE_GENERAL_CHAT}
        
        # Get model from parameters if specified
        model = ServerConstants.DEFAULT_MODEL
        if hasattr(request, 'parameters') and request.parameters.get("model"):
            model = request.parameters.get("model")
            
        if not prompt:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(ServerConstants.ERROR_PROMPT_REQUIRED)
            return ai_service_pb2.AiResponse(metadata=metadata)
            
        try:
            client = self._get_llm_client(model)
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
            
        return ai_service_pb2.AiResponse(response_text=ai_text)

    def GenerateResponse(self, request, context):
        self.logger.info(f"GenerateResponse called with model: {request.model}")
        
        prompt = request.prompt
        metadata = {
            "model": request.model,
            "timestamp": str(datetime.utcnow()),
            "request_id": context.invocation_metadata().get("request-id", ""),
            "llm_provider": ServerConstants.DEFAULT_MODEL  # Default to ollama
        }
        
        # Simple response generation using LLM
        try:
            metadata["llm_provider"] = (ServerConstants.GROK_MODEL_IDENTIFIER 
                                      if ServerConstants.GROK_MODEL_IDENTIFIER in request.model.lower() 
                                      else ServerConstants.DEFAULT_MODEL)
            self.logger.info(f"Generating response using {metadata['llm_provider']} with model: {request.model}")
            
            client = self._get_llm_client(request.model)
            messages = [{"role": "user", "content": prompt}]
            self.logger.debug(f"Sending messages to LLM: {messages}")
            
            start_time = time.time()
            ai_text = client.chat(messages)
            elapsed = time.time() - start_time
            
            self.logger.info(f"LLM response received in {elapsed:.2f}s")
            log_text = (f"{ai_text[:ServerConstants.LOG_TRUNCATE_LENGTH]}..." 
                       if len(str(ai_text)) > ServerConstants.LOG_TRUNCATE_LENGTH 
                       else ai_text)
            self.logger.debug(f"LLM response: {log_text}")
            
        except Exception as e:
            logging.error(f"LLM error: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse()
        
        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=ServerConstants.DEFAULT_CONFIDENCE_SCORE,
            metadata=metadata
        )

    def GenerateApiTestingScenarios(self, request, context):
        logging.info(f"Received API testing gRPC request: {request}")
        
        # Extract parameters with defaults
        url = request.parameters.get("url", "")
        method = request.parameters.get("method", ServerConstants.DEFAULT_METHOD)
        params = request.parameters.get("params", "[]")
        headers = request.parameters.get("headers", "[]")
        body = request.parameters.get("body", "None")
        
        metadata = {"template_name": ServerConstants.TEMPLATE_API_TESTING}
        
        # Format parameters and headers
        try:
            import json
            params_list = json.loads(params)
            headers_list = json.loads(headers)
            
            param_details = self._format_parameter_details(params_list)
            header_details = self._format_parameter_details(headers_list)
            
        except Exception as e:
            logging.error(f"Error parsing params or headers: {e}")
            param_details = "None"
            header_details = "None"
        
        prompt = self._build_api_testing_prompt(url, method, param_details, header_details, body)
        
        logging.info(f"Using API testing prompt")
        model = request.parameters.get("model", ServerConstants.DEFAULT_MODEL)
        
        try:
            client = self._get_llm_client(model)
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        
        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=ServerConstants.DEFAULT_CONFIDENCE_SCORE,
            metadata=metadata
        )

    def _format_parameter_details(self, param_list):
        """Format parameter or header details for display."""
        if not param_list:
            return "None"
        return "\n".join([
            f"- {param.get('key')}: {param.get('value')} ({param.get('description')})" 
            for param in param_list
        ])

    def _build_api_testing_prompt(self, url, method, param_details, header_details, body):
        """Build the API testing prompt."""
        return f"""
I have to test the API with the following details:

- URL: {url}
- HTTP Method: {method}

Please share all the possible scenarios to test this endpoint, considering:
1. **Data Testing**: Validate input boundary conditions, data types, required fields, empty or null values, special characters, and maximum field lengths across all parameters, headers, and body fields.
   - **Query Parameters**: {param_details}
   - **Headers**: {header_details}
2. **Security Testing**:
    - **Authentication and Authorization**: Include cases for missing, invalid, and expired tokens or credentials (if applicable).
    - **Injection Attacks**: SQL Injection, XSS, and other potential vulnerabilities.
    - **Data Sensitivity**: Ensure sensitive data is not exposed in error messages or response bodies.
3. **Functional Testing**:
    - **Positive Cases**: Scenarios that cover expected inputs and responses, ensuring successful requests.
    - **Negative Cases**: Include missing fields, invalid field values, and edge cases for optional fields.
4. **Load and Performance Testing**: Consider scenarios to test the API under load, such as large payloads, high request frequency, and any other performance-sensitive factors.

Return these test scenarios in a format compatible with Postman, including expected input values, response status codes (e.g., 200, 400, 401), and validation checks on the response body fields.

Example Inputs:
- **Query Parameters**: {param_details}
- **Headers**: {header_details}
- **Body**: {body if body != "None" else "None"}
"""
    
    def GenerateJiraStories(self, request, context):
        logging.info(f"Received Jira stories generation gRPC request: {request}")
        
        srs_text = request.parameters.get("srs_text", "")
        if not srs_text:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(ServerConstants.ERROR_SRS_REQUIRED)
            return ai_service_pb2.AiResponse(metadata={"template_name": ServerConstants.TEMPLATE_JIRA_STORIES})
        
        metadata = {"template_name": ServerConstants.TEMPLATE_JIRA_STORIES}
        prompt = self._build_jira_stories_prompt(srs_text)
        
        logging.info(f"Using Jira stories generation prompt")
        model = request.parameters.get("model", ServerConstants.DEFAULT_MODEL)
        
        try:
            client = self._get_llm_client(model)
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        
        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=ServerConstants.DEFAULT_CONFIDENCE_SCORE,
            metadata=metadata
        )

    def _build_jira_stories_prompt(self, srs_text):
        """Build the Jira stories generation prompt."""
        return f"""Generate Jira stories in ordered list from the following extracted SRS (Software Requirements Specification) text:

{srs_text}

For each requirement, please create a well-structured Jira story with:

1. **Epic**: Categorize the story under an appropriate epic
2. **Story Title**: Clear, concise title starting with "As a..."
3. **Description**: Detailed explanation of the requirement
4. **Acceptance Criteria**: Specific, testable conditions that must be met
5. **Priority**: High/Medium/Low based on business impact and technical complexity
6. **Story Points**: Estimate of effort (1, 2, 3, 5, 8, 13)

Format each story as follows:

---
## Epic: [Epic Name]
### Story: [Story Title]
**Description:**
[Detailed description]

**Acceptance Criteria:**
- [Criterion 1]
- [Criterion 2]
- [Criterion n]

**Priority:** [Priority Level]
**Story Points:** [Estimate]
---

Ensure that:
- Each story is independent and testable
- The acceptance criteria are specific and measurable
- Technical implementation details are minimized in favor of business requirements
- Non-functional requirements (performance, security, etc.) are captured as separate stories
"""
    
    def PromptBasedAutomation(self, request, context):
        logging.info(f"Received user prompt for automation: {request}")
        
        user_prompt = request.parameters.get("user_prompt", "")
        formatted_locators = request.parameters.get("formatted_locators", "")
        metadata = {"template_name": ServerConstants.TEMPLATE_PROMPT_AUTOMATION}

        prompt = self._build_automation_prompt(user_prompt, formatted_locators)

        try:
            client = self._get_llm_client(ServerConstants.DEFAULT_MODEL)
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM error: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse()
        
        if not ai_text:
            ai_text = ServerConstants.ERROR_NO_LLM_RESPONSE

        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=ServerConstants.DEFAULT_CONFIDENCE_SCORE,
            metadata=metadata
        )

    def _build_automation_prompt(self, user_prompt, formatted_locators):
        """Build the automation prompt for Robot Framework."""
        return f"""
User Instructions:
{user_prompt}

Locators:
{formatted_locators}

Generate a complete Robot Framework script that strictly follows the exact sequence of actions provided in the user_instructions. Do not reorder, omit, or infer any steps. Use the given locators exactly as specified, with 'xpath=' included where applicable.
Before performing any action on an element, add a 'Wait Until Element Is Visible' step using the same locator.
Locators must be used exactly as retrieved from the source URL, preserving original casing and formatting, without any alterations such as capitalization or lowercasing.
This requirement applies to both the full XPath and all attribute values within it. Do not modify attribute values (e.g., do not change 'male' to 'Male') under any circumstances.
Set the Selenium execution speed to {ServerConstants.SELENIUM_SPEED} seconds using the appropriate Robot Framework keyword at the beginning of the test case.
The script must be minimal and contain only:
*** Settings ***
Library           SeleniumLibrary

*** Test Cases ***
[Test Case Name]
[Sequence of Robot Framework keywords with inputs as specified]
No explanations, markdown, or comments. Output must be plain Robot Framework code only, directly executable.
"""

    def ProcessDocument(self, request, context):
        return ai_service_pb2.DocumentResponse(
            processed_content=f"Processed: {request.document_content}",
            extractions=[],
            status="OK"
        )


def configure_logging():
    """Configure logging for the application."""
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler(),
            logging.FileHandler(ServerConstants.LOG_FILE)
        ]
    )


def serve():
    configure_logging()
    logger = logging.getLogger(__name__)
    logger.info(ServerConstants.LOG_SERVER_STARTING)
    
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=ServerConstants.MAX_WORKERS))
    logger.info(ServerConstants.LOG_THREAD_POOL_INIT)
    
    ai_service_pb2_grpc.add_AiServiceServicer_to_server(AiServiceServicer(), server)
    server.add_insecure_port(ServerConstants.SERVER_PORT)
    server.start()
    server.wait_for_termination()