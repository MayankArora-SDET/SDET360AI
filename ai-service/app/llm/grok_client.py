from groq import Groq
from app.config import GROK_API_KEY
class GrokClient:
    def __init__(self, model: str = "llama-3.3-70b-versatile"):
        """Initialize with default configured model."""
        self.model = model 
        self.client = Groq(api_key=GROK_API_KEY)

    def chat(self, messages: list) -> str:
        """Send messages to Groq and return the assistant response."""
        response = self.client.chat.completions.create(
            model=self.model,
            messages=messages
        )
        if getattr(response, 'choices', None):
            return response.choices[0].message.content
        return ""
