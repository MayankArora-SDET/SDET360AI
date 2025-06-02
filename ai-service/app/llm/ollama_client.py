from langchain_ollama import OllamaLLM

class OllamaClient:
    def __init__(self, model: str = "llama3.1:70b"):
        self.client = OllamaLLM(model=model)

    def chat(self, messages: list) -> str:
        # Extract the last user message as prompt
        prompt = ""
        for msg in reversed(messages):
            if msg.get("role") == "user":
                prompt = msg.get("content", "")
                break
        ai_response = self.client.invoke(input=prompt)
        # Handle error response
        if isinstance(ai_response, dict) and "error" in ai_response:
            raise RuntimeError(ai_response["error"])
        # Extract message content
        if isinstance(ai_response, dict):
            return ai_response.get("message", "")
        return str(ai_response)
