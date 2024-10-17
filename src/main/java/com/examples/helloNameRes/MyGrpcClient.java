package com.examples.helloNameRes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;

public class MyGrpcClient {
  private final ManagedChannel channel;

  public MyGrpcClient() {
    // Register the custom NameResolverProvider
    NameResolverRegistry.getDefaultRegistry().register(new MyNameResolver.MyNameResolverProvider());

    // Use the custom resolver with the "my-resolver://service-name" target
    this.channel = ManagedChannelBuilder
        .forTarget("my-resolver://service-name")
        .usePlaintext()  // Use plaintext for simplicity (without TLS)
        .build();
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }

  public static void main(String[] args) throws InterruptedException {
    MyGrpcClient client = new MyGrpcClient();
    // Channel is built, and the resolver has printed the resolved addresses
    try {
      System.out.println("Client is ready!");
    } finally {
      client.shutdown();
    }
  }
}
