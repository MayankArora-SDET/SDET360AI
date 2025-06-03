import logging
from typing import Union, Type
from .ollama_client import OllamaClient
from .grok_client import GrokClient

logger = logging.getLogger(__name__)

class LLMClientFactory:
    _clients = {
        'ollama': OllamaClient,
        'grok': GrokClient
    }
    
    @classmethod
    def get_client(cls, model: str) -> Union[OllamaClient, GrokClient]:
        """
        Factory method to get an LLM client instance.
        
        Args:
            model: Name of the model (case-insensitive)
            
        Returns:
            An instance of the requested LLM client
            
        Raises:
            ValueError: If the model is not supported
            Exception: If client initialization fails
        """
        model_l = model.lower()
        logger.info(f"Requesting LLM client for model: {model}")
        
        try:
            client_class = cls._clients.get(model_l)
            if not client_class:
                error_msg = f"Unsupported LLM model: {model}. Available models: {', '.join(cls._clients.keys())}"
                logger.error(error_msg)
                raise ValueError(error_msg)
                
            logger.debug(f"Initializing {model_l.capitalize()} client...")
            client = client_class()
            logger.info(f"Successfully initialized {model_l.capitalize()} client")
            return client
            
        except Exception as e:
            logger.error(f"Failed to initialize {model_l} client: {str(e)}")
            raise
