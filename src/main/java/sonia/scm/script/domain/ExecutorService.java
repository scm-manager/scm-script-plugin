package sonia.scm.script.domain;

import javax.inject.Inject;

public class ExecutorService {

  private final ScriptRepository scriptRepository;
  private final Executor executor;

  @Inject
  public ExecutorService(ScriptRepository scriptRepository, Executor executor) {
    this.scriptRepository = scriptRepository;
    this.executor = executor;
  }

  public void execute(Id id, ExecutionContext context) {
    Script script = scriptRepository.findById(id).orElseThrow(() -> new ScriptNotFoundException(id));
    executor.execute(script, context);
  }
}
