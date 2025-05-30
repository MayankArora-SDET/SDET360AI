from meta_ai_api import MetaAI

class MetaClient:
    def __init__(self):
        self.client = MetaAI()

    def chat(self, messages: list) -> str:
        try:
            prompt = ""
            for msg in reversed(messages):
                if msg.get("role") == "user":
                    prompt = msg.get("content", "")
                    break

            if not isinstance(prompt, str):
                raise TypeError(f"Prompt must be a string, got {type(prompt)}")

            response = self.client.prompt(message=prompt)
            if isinstance(response, dict) and "message" in response:
                return response["message"]

            return str(response)
        
        except Exception as e:
            print(f"MetaAI request failed: {e}")
            return ""
