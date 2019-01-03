package sonia.scm.script.domain;

public class ScriptTypeNotFoundException extends ScriptExecutionException {
  public ScriptTypeNotFoundException(String message) {
    super(message);
  }
}
