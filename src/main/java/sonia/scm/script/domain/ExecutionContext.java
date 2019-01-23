package sonia.scm.script.domain;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

  private final Map<String, Object> attributes;

  public ExecutionContext(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public static ExecutionContext empty() {
    return builder().build();
  }

  public Map<String, Object> getAttributes() {
    return ImmutableMap.copyOf(attributes);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Map<String, Object> attributes = new HashMap<>();

    public Builder withAttribute(String key, Object value) {
      attributes.put(key, value);
      return this;
    }

    public ExecutionContext build() {
      return new ExecutionContext(attributes);
    }

  }
}
