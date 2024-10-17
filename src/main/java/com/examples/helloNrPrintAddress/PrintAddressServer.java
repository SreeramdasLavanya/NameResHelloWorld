package com.examples.helloNrPrintAddress;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import io.grpc.stub.StreamObserver;

public class PrintAddressServer {

  private final Server server;

  public PrintAddressServer(int port) {
    this.server = ServerBuilder.forPort(port)
        .addService(new GreeterImpl())
        .build();
  }

  public void start() throws IOException {
    server.start();
    System.out.println("Server started on port: " + server.getPort());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("Shutting down gRPC server...");
      PrintAddressServer.this.stop();
      System.err.println("Server shut down.");
    }));
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  public void awaitTermination() throws InterruptedException {
    server.awaitTermination();
  }

  static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder()
          .setMessage("Hello " + request.getName())
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    PrintAddressServer server = new PrintAddressServer(5002);  // The server listens on port 5002
    server.start();
    server.awaitTermination();
  }
}
