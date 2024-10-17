package com.examples.helloNrPrintAddress;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
public class PrintAddressClient {

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public PrintAddressClient() {
      // Register custom NameResolverProvider
      NameResolverRegistry.getDefaultRegistry().register(new MyNameResolver.MyNameResolverProvider());

      // Use custom resolver: my-resolver://service-name
      this.channel = ManagedChannelBuilder
          .forTarget("my-resolver://authority/service-name")
          .usePlaintext()  // Simplification for local testing
          .build();

      blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

   public void greet(String name) {
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();
      HelloReply response = blockingStub.sayHello(request);
      System.out.println("Message: " + response.getMessage());
    }

    public void shutdown() throws InterruptedException {
      channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
      PrintAddressClient client = new PrintAddressClient();
      try {
        client.greet("Trying to print IP address from Name Resolver");
        System.out.println("Client is ready!");
      } finally {
        client.shutdown();
      }
    }
}
