package com.examples.helloNameRes;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class MyNameResolver extends NameResolver {
  private final String serviceName;
  private Listener2 listener;

  public MyNameResolver(String serviceName) {
    this.serviceName = serviceName;
  }

  @Override
  public String getServiceAuthority() {
    return serviceName != null ? serviceName : "localhost";
  }

  @Override
  public void start(Listener2 listener) {
    this.listener = listener;
    System.out.println("Resolving service: " + serviceName);

    // For demonstration purposes, resolving to localhost:5005
    List<EquivalentAddressGroup> addresses = Collections.singletonList(
        new EquivalentAddressGroup(new InetSocketAddress("localhost", 5005))
    );

    // Notify the listener of the resolved addresses
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
    // Implement if the resolver needs to refresh the addresses dynamically
  }

  @Override
  public void shutdown() {
    // Clean up resources if needed
  }

  public static class MyNameResolverProvider extends NameResolverProvider {
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
      if ("my-resolver".equals(targetUri.getScheme())) {
        String serviceName = targetUri.getAuthority();
        return new MyNameResolver(serviceName);
      }
      return null; // Not handled by this resolver
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
      return 5; // Set priority to determine the resolver's precedence
    }
  }
}

