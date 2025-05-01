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
