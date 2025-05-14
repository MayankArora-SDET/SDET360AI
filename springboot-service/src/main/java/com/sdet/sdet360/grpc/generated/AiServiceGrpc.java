package com.sdet.sdet360.grpc.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: ai_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AiServiceGrpc {

  private AiServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ai.sdet360.AiService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateResponse",
      requestType = com.sdet.sdet360.grpc.generated.AiRequest.class,
      responseType = com.sdet.sdet360.grpc.generated.AiResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseMethod() {
    io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseMethod;
    if ((getGenerateResponseMethod = AiServiceGrpc.getGenerateResponseMethod) == null) {
      synchronized (AiServiceGrpc.class) {
        if ((getGenerateResponseMethod = AiServiceGrpc.getGenerateResponseMethod) == null) {
          AiServiceGrpc.getGenerateResponseMethod = getGenerateResponseMethod =
              io.grpc.MethodDescriptor.<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateResponse"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AiServiceMethodDescriptorSupplier("GenerateResponse"))
              .build();
        }
      }
    }
    return getGenerateResponseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseForGeneralChatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateResponseForGeneralChat",
      requestType = com.sdet.sdet360.grpc.generated.AiRequest.class,
      responseType = com.sdet.sdet360.grpc.generated.AiResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseForGeneralChatMethod() {
    io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse> getGenerateResponseForGeneralChatMethod;
    if ((getGenerateResponseForGeneralChatMethod = AiServiceGrpc.getGenerateResponseForGeneralChatMethod) == null) {
      synchronized (AiServiceGrpc.class) {
        if ((getGenerateResponseForGeneralChatMethod = AiServiceGrpc.getGenerateResponseForGeneralChatMethod) == null) {
          AiServiceGrpc.getGenerateResponseForGeneralChatMethod = getGenerateResponseForGeneralChatMethod =
              io.grpc.MethodDescriptor.<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateResponseForGeneralChat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AiServiceMethodDescriptorSupplier("GenerateResponseForGeneralChat"))
              .build();
        }
      }
    }
    return getGenerateResponseForGeneralChatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.DocumentRequest,
      com.sdet.sdet360.grpc.generated.DocumentResponse> getProcessDocumentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ProcessDocument",
      requestType = com.sdet.sdet360.grpc.generated.DocumentRequest.class,
      responseType = com.sdet.sdet360.grpc.generated.DocumentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.DocumentRequest,
      com.sdet.sdet360.grpc.generated.DocumentResponse> getProcessDocumentMethod() {
    io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.DocumentRequest, com.sdet.sdet360.grpc.generated.DocumentResponse> getProcessDocumentMethod;
    if ((getProcessDocumentMethod = AiServiceGrpc.getProcessDocumentMethod) == null) {
      synchronized (AiServiceGrpc.class) {
        if ((getProcessDocumentMethod = AiServiceGrpc.getProcessDocumentMethod) == null) {
          AiServiceGrpc.getProcessDocumentMethod = getProcessDocumentMethod =
              io.grpc.MethodDescriptor.<com.sdet.sdet360.grpc.generated.DocumentRequest, com.sdet.sdet360.grpc.generated.DocumentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ProcessDocument"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.DocumentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.DocumentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AiServiceMethodDescriptorSupplier("ProcessDocument"))
              .build();
        }
      }
    }
    return getProcessDocumentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateApiTestingScenariosMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateApiTestingScenarios",
      requestType = com.sdet.sdet360.grpc.generated.AiRequest.class,
      responseType = com.sdet.sdet360.grpc.generated.AiResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateApiTestingScenariosMethod() {
    io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse> getGenerateApiTestingScenariosMethod;
    if ((getGenerateApiTestingScenariosMethod = AiServiceGrpc.getGenerateApiTestingScenariosMethod) == null) {
      synchronized (AiServiceGrpc.class) {
        if ((getGenerateApiTestingScenariosMethod = AiServiceGrpc.getGenerateApiTestingScenariosMethod) == null) {
          AiServiceGrpc.getGenerateApiTestingScenariosMethod = getGenerateApiTestingScenariosMethod =
              io.grpc.MethodDescriptor.<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateApiTestingScenarios"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AiServiceMethodDescriptorSupplier("GenerateApiTestingScenarios"))
              .build();
        }
      }
    }
    return getGenerateApiTestingScenariosMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateJiraStoriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateJiraStories",
      requestType = com.sdet.sdet360.grpc.generated.AiRequest.class,
      responseType = com.sdet.sdet360.grpc.generated.AiResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest,
      com.sdet.sdet360.grpc.generated.AiResponse> getGenerateJiraStoriesMethod() {
    io.grpc.MethodDescriptor<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse> getGenerateJiraStoriesMethod;
    if ((getGenerateJiraStoriesMethod = AiServiceGrpc.getGenerateJiraStoriesMethod) == null) {
      synchronized (AiServiceGrpc.class) {
        if ((getGenerateJiraStoriesMethod = AiServiceGrpc.getGenerateJiraStoriesMethod) == null) {
          AiServiceGrpc.getGenerateJiraStoriesMethod = getGenerateJiraStoriesMethod =
              io.grpc.MethodDescriptor.<com.sdet.sdet360.grpc.generated.AiRequest, com.sdet.sdet360.grpc.generated.AiResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateJiraStories"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.sdet.sdet360.grpc.generated.AiResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AiServiceMethodDescriptorSupplier("GenerateJiraStories"))
              .build();
        }
      }
    }
    return getGenerateJiraStoriesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AiServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AiServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AiServiceStub>() {
        @java.lang.Override
        public AiServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AiServiceStub(channel, callOptions);
        }
      };
    return AiServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AiServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AiServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AiServiceBlockingStub>() {
        @java.lang.Override
        public AiServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AiServiceBlockingStub(channel, callOptions);
        }
      };
    return AiServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AiServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AiServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AiServiceFutureStub>() {
        @java.lang.Override
        public AiServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AiServiceFutureStub(channel, callOptions);
        }
      };
    return AiServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void generateResponse(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateResponseMethod(), responseObserver);
    }

    /**
     */
    default void generateResponseForGeneralChat(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateResponseForGeneralChatMethod(), responseObserver);
    }

    /**
     */
    default void processDocument(com.sdet.sdet360.grpc.generated.DocumentRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.DocumentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getProcessDocumentMethod(), responseObserver);
    }

    /**
     */
    default void generateApiTestingScenarios(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateApiTestingScenariosMethod(), responseObserver);
    }

    /**
     */
    default void generateJiraStories(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateJiraStoriesMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AiService.
   */
  public static abstract class AiServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AiServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AiService.
   */
  public static final class AiServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AiServiceStub> {
    private AiServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AiServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AiServiceStub(channel, callOptions);
    }

    /**
     */
    public void generateResponse(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateResponseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void generateResponseForGeneralChat(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateResponseForGeneralChatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void processDocument(com.sdet.sdet360.grpc.generated.DocumentRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.DocumentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getProcessDocumentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void generateApiTestingScenarios(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateApiTestingScenariosMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void generateJiraStories(com.sdet.sdet360.grpc.generated.AiRequest request,
        io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateJiraStoriesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AiService.
   */
  public static final class AiServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AiServiceBlockingStub> {
    private AiServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AiServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AiServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.sdet.sdet360.grpc.generated.AiResponse generateResponse(com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateResponseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.sdet.sdet360.grpc.generated.AiResponse generateResponseForGeneralChat(com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateResponseForGeneralChatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.sdet.sdet360.grpc.generated.DocumentResponse processDocument(com.sdet.sdet360.grpc.generated.DocumentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getProcessDocumentMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.sdet.sdet360.grpc.generated.AiResponse generateApiTestingScenarios(com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateApiTestingScenariosMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.sdet.sdet360.grpc.generated.AiResponse generateJiraStories(com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateJiraStoriesMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AiService.
   */
  public static final class AiServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AiServiceFutureStub> {
    private AiServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AiServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AiServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sdet.sdet360.grpc.generated.AiResponse> generateResponse(
        com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateResponseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sdet.sdet360.grpc.generated.AiResponse> generateResponseForGeneralChat(
        com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateResponseForGeneralChatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sdet.sdet360.grpc.generated.DocumentResponse> processDocument(
        com.sdet.sdet360.grpc.generated.DocumentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getProcessDocumentMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sdet.sdet360.grpc.generated.AiResponse> generateApiTestingScenarios(
        com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateApiTestingScenariosMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sdet.sdet360.grpc.generated.AiResponse> generateJiraStories(
        com.sdet.sdet360.grpc.generated.AiRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateJiraStoriesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_RESPONSE = 0;
  private static final int METHODID_GENERATE_RESPONSE_FOR_GENERAL_CHAT = 1;
  private static final int METHODID_PROCESS_DOCUMENT = 2;
  private static final int METHODID_GENERATE_API_TESTING_SCENARIOS = 3;
  private static final int METHODID_GENERATE_JIRA_STORIES = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_RESPONSE:
          serviceImpl.generateResponse((com.sdet.sdet360.grpc.generated.AiRequest) request,
              (io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse>) responseObserver);
          break;
        case METHODID_GENERATE_RESPONSE_FOR_GENERAL_CHAT:
          serviceImpl.generateResponseForGeneralChat((com.sdet.sdet360.grpc.generated.AiRequest) request,
              (io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse>) responseObserver);
          break;
        case METHODID_PROCESS_DOCUMENT:
          serviceImpl.processDocument((com.sdet.sdet360.grpc.generated.DocumentRequest) request,
              (io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.DocumentResponse>) responseObserver);
          break;
        case METHODID_GENERATE_API_TESTING_SCENARIOS:
          serviceImpl.generateApiTestingScenarios((com.sdet.sdet360.grpc.generated.AiRequest) request,
              (io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse>) responseObserver);
          break;
        case METHODID_GENERATE_JIRA_STORIES:
          serviceImpl.generateJiraStories((com.sdet.sdet360.grpc.generated.AiRequest) request,
              (io.grpc.stub.StreamObserver<com.sdet.sdet360.grpc.generated.AiResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGenerateResponseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.sdet.sdet360.grpc.generated.AiRequest,
              com.sdet.sdet360.grpc.generated.AiResponse>(
                service, METHODID_GENERATE_RESPONSE)))
        .addMethod(
          getGenerateResponseForGeneralChatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.sdet.sdet360.grpc.generated.AiRequest,
              com.sdet.sdet360.grpc.generated.AiResponse>(
                service, METHODID_GENERATE_RESPONSE_FOR_GENERAL_CHAT)))
        .addMethod(
          getProcessDocumentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.sdet.sdet360.grpc.generated.DocumentRequest,
              com.sdet.sdet360.grpc.generated.DocumentResponse>(
                service, METHODID_PROCESS_DOCUMENT)))
        .addMethod(
          getGenerateApiTestingScenariosMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.sdet.sdet360.grpc.generated.AiRequest,
              com.sdet.sdet360.grpc.generated.AiResponse>(
                service, METHODID_GENERATE_API_TESTING_SCENARIOS)))
        .addMethod(
          getGenerateJiraStoriesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.sdet.sdet360.grpc.generated.AiRequest,
              com.sdet.sdet360.grpc.generated.AiResponse>(
                service, METHODID_GENERATE_JIRA_STORIES)))
        .build();
  }

  private static abstract class AiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AiServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.sdet.sdet360.grpc.generated.AiServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AiService");
    }
  }

  private static final class AiServiceFileDescriptorSupplier
      extends AiServiceBaseDescriptorSupplier {
    AiServiceFileDescriptorSupplier() {}
  }

  private static final class AiServiceMethodDescriptorSupplier
      extends AiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AiServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AiServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AiServiceFileDescriptorSupplier())
              .addMethod(getGenerateResponseMethod())
              .addMethod(getGenerateResponseForGeneralChatMethod())
              .addMethod(getProcessDocumentMethod())
              .addMethod(getGenerateApiTestingScenariosMethod())
              .addMethod(getGenerateJiraStoriesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
