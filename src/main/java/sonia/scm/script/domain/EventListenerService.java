package sonia.scm.script.domain;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventListenerService {

  private final StorableScriptRepository scriptRepository;
  private final Executor executor;

  @Inject
  public EventListenerService(StorableScriptRepository scriptRepository, Executor executor) {
    this.scriptRepository = scriptRepository;
    this.executor = executor;
  }

  public Optional<Trigger> createTrigger(Class<?> eventType, boolean asynchronous) {
    List<StorableScript> scripts = scriptRepository.findAll().stream().filter(script -> {
      if (asynchronous) {
        return script.isListeningAsynchronous(eventType);
      }
      return script.isListeningSynchronous(eventType);
    }).collect(Collectors.toList());

    if (scripts.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new Trigger(scripts));
  }

  public class Trigger {

    private final List<StorableScript> scripts;

    private Trigger(List<StorableScript> scripts) {
      this.scripts = scripts;
    }

    void execute(ExecutionContext context) {
      for (StorableScript script : scripts) {
        executor.execute(script, context);
      }
    }

  }

}
