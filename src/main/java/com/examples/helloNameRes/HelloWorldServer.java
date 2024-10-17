package com.examples.helloNameRes;

import com.examples.helloNameRes.HelloNameResProto.HelloReply;
import com.examples.helloNameRes.HelloNameResProto.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class HelloWorldServer {

  private final Server server;

  public HelloWorldServer(int port) {
    this.server = ServerBuilder.forPort(port)
        .addService(new GreeterImpl())
        .build();
  }

  public void start() throws Exception {
    server.start();
    System.out.println("Server started on port " + server.getPort());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("Shutting down...");
      stop();
    }));
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      String message = "Hello, " + request.getName();
      HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

  public static void main(String[] args) throws Exception {
    HelloWorldServer server = new HelloWorldServer(5005);
    server.start();
    server.server.awaitTermination();
  }
}
