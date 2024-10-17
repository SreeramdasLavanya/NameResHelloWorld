package com.examples.helloNameRes;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class CustomNameResolver extends NameResolver {

  private final String serviceName;
  private Listener2 listener;

  public CustomNameResolver(String serviceName) {
    this.serviceName = serviceName;
  }

  @Override
  public String getServiceAuthority() {
    return serviceName;
  }

  @Override
  public void start(Listener2 listener) {
    this.listener = listener;

    // For simplicity, manually resolve to localhost for now
    List<EquivalentAddressGroup> servers = Collections.singletonList(
        new EquivalentAddressGroup(
            new java.net.InetSocketAddress("localhost", 5005)
        )
    );

    // Notify the listener of the resolved addresses
    listener.onResult(ResolutionResult.newBuilder()
        .setAddresses(servers)
        .setAttributes(Attributes.EMPTY)
        .build());
  }

  @Override
  public void shutdown() {
    // Cleanup if needed
  }

  public static class Provider extends NameResolverProvider {

    @Override
    protected boolean isAvailable() {
      return true;
    }

    @Override
    protected int priority() {
      return 5;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
      if ("custom".equals(targetUri.getScheme())) {
        return new CustomNameResolver(targetUri.getAuthority());
      }
      return null;
    }

    @Override
    public String getDefaultScheme() {
      return "custom";
    }
  }
}

