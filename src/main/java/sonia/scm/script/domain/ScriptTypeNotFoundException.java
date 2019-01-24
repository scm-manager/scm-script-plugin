package sonia.scm.script.domain;

public class ScriptTypeNotFoundException extends ScriptExecutionException {
  public ScriptTypeNotFoundException(String type) {
    super("could not find engine for " + type);
  }
}
