package com.examples.helloNrPrintAddress;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class MyNameResolver extends NameResolver {

  private final String authority;
  private final String serviceName;
  private Listener2 listener;

  public MyNameResolver(String authority, String serviceName) {
    this.authority = authority;
    this.serviceName = serviceName;
  }

  @Override
  public String getServiceAuthority() {
    //return serviceName != null ? serviceName : "localhost";
    return authority != null ? authority : "localhost";
    //return authority;
  }

  @Override
  public void start(Listener2 listener) {
    this.listener = listener;
    System.out.println("Authority: " + authority);
    System.out.println("Service Name: " + serviceName);

    // Simulating address resolution (e.g., resolving to localhost:5002)
    List<EquivalentAddressGroup> addresses = Collections.singletonList(
        new EquivalentAddressGroup(new InetSocketAddress("localhost", 5002))
    );

    // Notify the listener with resolved addresses
    listener.onResult(ResolutionResult.newBuilder()
        .setAddresses(addresses)
        .setAttributes(Attributes.EMPTY)
        .build());

    // Print the resolved addresses
    for (EquivalentAddressGroup addressGroup : addresses) {
      System.out.println("Resolved address: " + addressGroup.getAddresses());
    }
  }

  @Override
  public void refresh() {
    // Can be implemented if dynamic refresh the addresses is needed
  }

  @Override
  public void shutdown() {
    // Clean up resources if needed
  }

  public static class MyNameResolverProvider extends NameResolverProvider {
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
      if ("my-resolver".equals(targetUri.getScheme())) {
        //String serviceName = targetUri.getAuthority();
        String authority = targetUri.getAuthority();  // authority part of the URI
        String serviceName = targetUri.getPath().substring(1);  // remove leading '/'
        return new MyNameResolver(authority, serviceName);
        //return new MyNameResolver(serviceName);
      }
      return null;
    }

    @Override
    public String getDefaultScheme() {
      return "my-resolver";
    }

    @Override
    protected boolean isAvailable() {
      return true;
    }

    @Override
    protected int priority() {
      return 5; // Priority for the resolver
    }
  }
}
