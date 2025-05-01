from .ollama_client import OllamaClient
from .grok_client import GrokClient

class LLMClientFactory:
    @staticmethod
    def get_client(model: str):
        model_l = model.lower()
        if model_l == "ollama":
            return OllamaClient()
        elif model_l == "grok":
            return GrokClient()
        else:
            raise ValueError(f"Unknown LLM model: {model}")
