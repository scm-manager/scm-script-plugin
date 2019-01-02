package sonia.scm.script.domain;

public interface Executor {

  void execute(Script script, ExecutionContext context);

}
