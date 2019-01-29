package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;

import javax.inject.Inject;

/**
 * The {@link JSR223Executor} checks permissions and delegates the execution of the script to the
 * {@link JSR223InternalExecutor}.
 */
public class JSR223Executor implements Executor {

  private final JSR223InternalExecutor executor;

  @Inject
  public JSR223Executor(JSR223InternalExecutor executor) {
    this.executor = executor;
  }

  @Override
  public ExecutionResult execute(Script script, ExecutionContext context) {
    ScriptPermissions.checkExecute();
    return executor.execute(script, context);
  }
}
