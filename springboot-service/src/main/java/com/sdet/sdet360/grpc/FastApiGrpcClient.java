package com.sdet.sdet360.grpc;

import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import com.sdet.sdet360.grpc.generated.DocumentRequest;
import com.sdet.sdet360.grpc.generated.DocumentResponse;
import com.sdet.sdet360.config.TenantContextHolder;
import io.grpc.ManagedChannel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class FastApiGrpcClient {

    private final AiServiceGrpc.AiServiceBlockingStub aiServiceStub;

    public FastApiGrpcClient(ManagedChannel channel) {
        this.aiServiceStub = AiServiceGrpc.newBlockingStub(channel);
    }

    public AiResponse generateResponseForGeneralChat(String prompt, Map<String, String> parameters) {
        UUID tenantId = TenantContextHolder.getTenantId();
        String tenantIdStr = tenantId != null ? tenantId.toString() : TenantContextHolder.MASTER_TENANT_ID.toString();

        AiRequest request = AiRequest.newBuilder()
                .setPrompt(prompt)
                .setTenantId(tenantIdStr)
                .putAllParameters(parameters)
                .build();

        return aiServiceStub.generateResponseForGeneralChat(request);
    }

    public DocumentResponse processDocument(String documentContent, String documentType) {
        UUID tenantId = TenantContextHolder.getTenantId();
        String tenantIdStr = tenantId != null ? tenantId.toString() : TenantContextHolder.MASTER_TENANT_ID.toString();

        DocumentRequest request = DocumentRequest.newBuilder()
                .setDocumentContent(documentContent)
                .setTenantId(tenantIdStr)
                .setDocumentType(documentType)
                .build();

        return aiServiceStub.processDocument(request);
    }
}