from fastapi import FastAPI
import threading
from app.grpc_server import serve
from app.llm import prompt_templates

app = FastAPI(title="My FastAPI Microservice")

@app.on_event("startup")
def on_startup():
    threading.Thread(target=serve, daemon=True).start()

@app.get("/templates")
def list_templates():
    """Return all available prompt template names."""
    return [name for name in dir(prompt_templates) if name.isupper()]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)