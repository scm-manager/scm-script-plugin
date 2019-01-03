package sonia.scm.script.domain;

import com.google.common.collect.ImmutableMap;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

  private final Reader input;
  private final Writer output;
  private final Map<String, Object> attributes;

  public ExecutionContext(Reader input, Writer output) {
    this(input, output, ImmutableMap.of());
  }

  public ExecutionContext(Reader input, Writer output, Map<String, Object> attributes) {
    this.input = input;
    this.output = output;
    this.attributes = attributes;
  }

  public Reader getInput() {
    return input;
  }

  public Writer getOutput() {
    return output;
  }

  public Map<String, Object> getAttributes() {
    return ImmutableMap.copyOf(attributes);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Writer output;
    private Reader input;
    private Map<String, Object> attributes = new HashMap<>();

    public Builder withOutput(Writer output) {
      this.output = output;
      return this;
    }

    public Builder withInput(Reader input) {
      this.input = input;
      return this;
    }

    public Builder withAttribute(String key, Object value) {
      attributes.put(key, value);
      return this;
    }

    public ExecutionContext build() {
      return new ExecutionContext(input, output, attributes);
    }

  }
}
