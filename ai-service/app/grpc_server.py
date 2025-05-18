from concurrent import futures
import grpc
import os, sys
import logging
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'generated'))
import importlib
gen_pb2 = importlib.import_module('app.generated.ai_service_pb2')
sys.modules['ai_service_pb2'] = gen_pb2
from app.generated import ai_service_pb2_grpc, ai_service_pb2
from app.llm import prompt_templates
from app.llm.client_factory import LLMClientFactory

class AiServiceServicer(ai_service_pb2_grpc.AiServiceServicer):
    def GenerateResponseForGeneralChat(self, request, context):
        logging.info(f"Received gRPC general chat request: {request}")
        prompt = getattr(request, "prompt", None)
        model = "grok"
        metadata = {"mode": "general_chat"}
        if hasattr(request, 'parameters') and request.parameters.get("model"):
            model = request.parameters.get("model")
        if not prompt:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("Prompt is required for general chat.")
            return ai_service_pb2.AiResponse(metadata=metadata)
        client = LLMClientFactory.get_client(model)
        try:
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        return ai_service_pb2.AiResponse(
            response_text    = ai_text
        )

    def GenerateResponse(self, request, context):
        logging.info(f"Received gRPC request: {request}")
        tpl_name    = request.parameters.get("template_name")
        issue_type  = request.parameters.get("issue_type", "")
        keys        = request.parameters.get("keys", "")
        desc        = request.parameters.get("description", "")
        metadata = {"template_name": tpl_name}
        try:
            tpl = getattr(prompt_templates, tpl_name)
        except Exception:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(f"Unknown template: {tpl_name}")
            return ai_service_pb2.AiResponse(metadata=metadata)
        prompt = tpl.format(
            issue_type       = issue_type,
            keys             = keys,
            description_text = desc
        )
        logging.info(f"Using template {tpl_name}: {prompt}")
        model = request.parameters.get("model", "grok")
        client = LLMClientFactory.get_client(model)
        try:
            ai_text = client.chat([{"role":"user","content":prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        return ai_service_pb2.AiResponse(
            response_text    = ai_text,
            confidence_score = 1.0,
            metadata         = metadata
        )

    def GenerateApiTestingScenarios(self, request, context):
        logging.info(f"Received API testing gRPC request: {request}")
        
        url = request.parameters.get("url", "")
        method = request.parameters.get("method", "GET")
        params = request.parameters.get("params", "[]")
        headers = request.parameters.get("headers", "[]")
        body = request.parameters.get("body", "None")
        
        metadata = {"template_name": "API_TESTING_SCENARIOS"}
        
        # Format parameters and headers similar to the reference implementation
        try:
            import json
            params_list = json.loads(params)
            headers_list = json.loads(headers)
            
            param_details = "\n".join([f"- {param.get('key')}: {param.get('value')} ({param.get('description')})" 
                                       for param in params_list]) if params_list else "None"
            header_details = "\n".join([f"- {header.get('key')}: {header.get('value')} ({header.get('description')})" 
                                        for header in headers_list]) if headers_list else "None"
        except Exception as e:
            logging.error(f"Error parsing params or headers: {e}")
            param_details = "None"
            header_details = "None"
        
        prompt = f"""
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
        
        logging.info(f"Using API testing prompt: {prompt}")
        model = request.parameters.get("model", "grok")
        client = LLMClientFactory.get_client(model)
        try:
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        
        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=1.0,
            metadata=metadata
        )
    
    def GenerateJiraStories(self, request, context):
        logging.info(f"Received Jira stories generation gRPC request: {request}")
        
        srs_text = request.parameters.get("srs_text", "")
        if not srs_text:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("SRS text is required")
            return ai_service_pb2.AiResponse(metadata={"template_name": "JIRA_STORIES"})
        
        metadata = {"template_name": "JIRA_STORIES"}
        
        prompt = f"""Generate Jira stories in ordered list from the following extracted SRS (Software Requirements Specification) text:

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
        
        logging.info(f"Using Jira stories generation prompt")
        model = request.parameters.get("model", "ollama")
        client = LLMClientFactory.get_client(model)
        try:
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM call error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse(metadata=metadata)
        
        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=1.0,
            metadata=metadata
        )
    
    def PromptBasedAutomation(self, request, context):

        logging.info(f"Received user prompt for automation: {request}")
        user_prompt = request.parameters.get("user_prompt", "")
        formatted_locators = request.parameters.get("formatted_locators", "")

        metadata = {"template_name": "PROMPT_BASED_AUTOMATION"}

        # Final Prompt
        prompt = f"""
    User Instructions:
    {user_prompt}

    Locators:
    {formatted_locators}

    Generate a complete Robot Framework script that strictly follows the exact sequence of actions provided in the user_instructions. Do not reorder, omit, or infer any steps. Use the given locators exactly as specified, with 'xpath=' included where applicable.
    Before performing any action on an element, add a 'Wait Until Element Is Visible' step using the same locator.
    Locators must be used exactly as retrieved from the source URL, preserving original casing and formatting, without any alterations such as capitalization or lowercasing.
    This requirement applies to both the full XPath and all attribute values within it. Do not modify attribute values (e.g., do not change 'male' to 'Male') under any circumstances.
    Set the Selenium execution speed to 0.1 seconds using the appropriate Robot Framework keyword at the beginning of the test case.
    The script must be minimal and contain only:
    *** Settings ***
    Library           SeleniumLibrary

    *** Test Cases ***
    [Test Case Name]
    [Sequence of Robot Framework keywords with inputs as specified]
    No explanations, markdown, or comments. Output must be plain Robot Framework code only, directly executable.
    """

        try:
            client = LLMClientFactory.get_client("grok")
            ai_text = client.chat([{"role": "user", "content": prompt}])
        except Exception as e:
            logging.error(f"LLM error: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return ai_service_pb2.AiResponse()
        
        if not ai_text:
            ai_text = "No response from LLM."

        return ai_service_pb2.AiResponse(
            response_text=ai_text,
            confidence_score=1.0,
            metadata=metadata
        )

    def ProcessDocument(self, request, context):
        return ai_service_pb2.DocumentResponse(
            processed_content=f"Processed: {request.document_content}",
            extractions=[],
            status="OK"
        )

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    ai_service_pb2_grpc.add_AiServiceServicer_to_server(AiServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()
