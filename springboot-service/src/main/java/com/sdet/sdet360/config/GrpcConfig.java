package com.sdet.sdet360.config;

import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcConfig {

    @Value("${fastapi.grpc.host}")
    private String host;

    @Value("${fastapi.grpc.port}")
    private int port;

    private ManagedChannel channel;

    @Bean
    public ManagedChannel grpcChannel() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        return channel;
    }

    @Bean
    public AiServiceGrpc.AiServiceBlockingStub aiStub(ManagedChannel channel) {
        return AiServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void closeChannel() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                channel.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
    }
}