import logging
import os
from typing import Dict, Any, Optional
from langchain_ollama import OllamaLLM
from app.config import OLLAMA_API_BASE, OLLAMA_MODEL

logger = logging.getLogger(__name__)

# Default configuration
DEFAULT_OLLAMA_HOST = os.getenv('OLLAMA_HOST', 'http://localhost')
DEFAULT_OLLAMA_PORT = os.getenv('OLLAMA_PORT', '11434')
DEFAULT_BASE_URL = f"{DEFAULT_OLLAMA_HOST}:{DEFAULT_OLLAMA_PORT}"
DEFAULT_MODEL = os.getenv('OLLAMA_MODEL', 'llama3.1:70b')

class OllamaClient:
    def __init__(
        self,
        model: str = DEFAULT_MODEL,
        base_url: str = DEFAULT_BASE_URL,
        timeout: int = 300,
        temperature: float = 0.7,
        **kwargs
    ):
        """Initialize the Ollama client.
        
        Args:
            model: The Ollama model to use
            base_url: Base URL of the Ollama server (e.g., 'http://localhost:11434')
            timeout: Request timeout in seconds
            temperature: Model temperature (0.0 to 1.0)
            **kwargs: Additional arguments to pass to OllamaLLM
        """
        self.model = model
        self.base_url = base_url
        self.timeout = timeout
        
        logger.info(f"Initializing Ollama client with model: {model} at {base_url}")
        
        try:
            self.client = OllamaLLM(
                model=model,
                base_url=base_url,
                timeout=timeout,
                temperature=temperature,
                **kwargs
            )
            logger.info("Ollama client initialized successfully")
            
            # Test connection
            self._test_connection()
            
        except Exception as e:
            logger.error(f"Failed to initialize Ollama client: {str(e)}")
            raise
            
    def _test_connection(self):
        """Test connection to the Ollama server."""
        try:
            # Simple API call to verify connection
            self.client.invoke("Test connection")
            logger.info("Successfully connected to Ollama server")
        except Exception as e:
            logger.error(f"Failed to connect to Ollama server at {self.base_url}: {str(e)}")
            raise

    def chat(self, messages: list) -> str:
        """Send messages to Ollama and return the assistant response.
        
        Args:
            messages: List of message dictionaries with 'role' and 'content' keys
            
        Returns:
            The generated text response
            
        Raises:
            RuntimeError: If there's an error in the API response
            Exception: For other errors during the API call
        """
        if not messages:
            logger.warning("Empty messages list provided to chat method")
            return ""
            
        logger.info(f"Ollama chat request received with {len(messages)} messages")
        
        try:
            # Extract the last user message as prompt
            prompt = ""
            for msg in reversed(messages):
                if msg.get("role") == "user":
                    prompt = msg.get("content", "")
                    break
                    
            if not prompt:
                logger.warning("No user message found in the messages list")
                return ""
                
            logger.debug(f"Sending prompt to Ollama (model: {self.model}): {prompt[:100]}...")
            
            # Make the API call
            ai_response = self.client.invoke(input=prompt)
            
            # Handle error response
            if isinstance(ai_response, dict) and "error" in ai_response:
                error_msg = f"Ollama API error: {ai_response['error']}"
                logger.error(error_msg)
                raise RuntimeError(error_msg)
                
            # Extract message content
            response_text = ""
            if isinstance(ai_response, dict):
                response_text = ai_response.get("message", "")
            else:
                response_text = str(ai_response)
                
            logger.debug(f"Received response from Ollama (length: {len(response_text)} chars)")
            return response_text
            
        except Exception as e:
            logger.error(f"Error in Ollama chat: {str(e)}")
            raise
