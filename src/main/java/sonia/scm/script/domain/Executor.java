package sonia.scm.script.domain;

public interface Executor {

  ExecutionResult execute(Script script, ExecutionContext context);

}
