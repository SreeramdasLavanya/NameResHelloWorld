package com.examples.helloNameRes;

import com.examples.helloNameRes.HelloNameResProto.HelloReply;
import com.examples.helloNameRes.HelloNameResProto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class HelloWorldClient {

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  public HelloWorldClient(String host, int port) {
    //Client Initializes ManagedChannel
    this.channel = ManagedChannelBuilder.forAddress(host, port)
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
    } catch (StatusRuntimeException e) {
      System.out.println("RPC failed: " + e.getStatus());
    }
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }

  public static void main(String[] args) throws InterruptedException {
    HelloWorldClient client = new HelloWorldClient("localhost", 5005);
    try {
      client.greet("World");
    } finally {
      client.shutdown();
    }
  }
}

