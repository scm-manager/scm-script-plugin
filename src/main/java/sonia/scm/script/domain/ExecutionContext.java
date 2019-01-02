package sonia.scm.script.domain;

import lombok.Getter;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ExecutionContext {

  private final Reader input;
  private final Writer output;

  private Map<String, Object> environment = new HashMap<>();

  public ExecutionContext(Reader input, Writer output) {
    this.input = input;
    this.output = output;
  }
}
