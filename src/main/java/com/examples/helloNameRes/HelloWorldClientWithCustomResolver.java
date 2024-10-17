package com.examples.helloNameRes;

import com.examples.helloNameRes.HelloNameResProto.HelloReply;
import com.examples.helloNameRes.HelloNameResProto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;

public class HelloWorldClientWithCustomResolver {

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  public HelloWorldClientWithCustomResolver(String serviceName) {
    // Register the custom Name Resolver
    NameResolverRegistry.getDefaultRegistry().register(new CustomNameResolver.Provider());

    // Use the custom name resolver with the "custom" scheme
    this.channel = ManagedChannelBuilder
        .forTarget("custom:///" + serviceName)
        .usePlaintext()
        .build();

    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void greet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
      System.out.println("Greeting: " + response.getMessage());
    } catch (Exception e) {
      System.out.println("RPC failed: " + e.getMessage());
    }
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }

  public static void main(String[] args) throws InterruptedException {
    HelloWorldClientWithCustomResolver client = new HelloWorldClientWithCustomResolver("greeter_service");
    try {
      client.greet("World");
    } finally {
      client.shutdown();
    }
  }
}

