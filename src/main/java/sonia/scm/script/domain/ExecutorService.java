package sonia.scm.script.domain;

import javax.inject.Inject;

public class ExecutorService {

  private final StorableScriptRepository scriptRepository;
  private final Executor executor;

  @Inject
  public ExecutorService(StorableScriptRepository scriptRepository, Executor executor) {
    this.scriptRepository = scriptRepository;
    this.executor = executor;
  }

  public void execute(String id, ExecutionContext context) {
    StorableScript script = scriptRepository.findById(id).orElseThrow(() -> new ScriptNotFoundException(id));
    executor.execute(script, context);
  }
}
