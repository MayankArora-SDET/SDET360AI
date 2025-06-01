import logging
from groq import Groq
from app.config import GROK_API_KEY

logger = logging.getLogger(__name__)

class GrokClient:
    def __init__(self, model: str = "llama-3.3-70b-versatile"):
        """Initialize with default configured model."""
        logger.info(f"Initializing Grok client with model: {model}")
        self.model = model 
        try:
            self.client = Groq(api_key=GROK_API_KEY)
            logger.info("Grok client initialized successfully")
        except Exception as e:
            logger.error(f"Failed to initialize Grok client: {str(e)}")
            raise

    def chat(self, messages: list) -> str:
        """Send messages to Groq and return the assistant response."""
        logger.info(f"Grok chat request received with {len(messages)} messages")
        try:
            response = self.client.chat.completions.create(
                model=self.model,
                messages=messages
            )
            if getattr(response, 'choices', None):
                logger.debug(f"Grok response received successfully")
                return response.choices[0].message.content
            logger.warning("No choices in Grok response")
            return ""
        except Exception as e:
            logger.error(f"Error in Grok chat: {str(e)}")
            raise
