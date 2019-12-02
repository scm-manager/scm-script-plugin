package sonia.scm.script.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventListenerService {

  private static final Logger LOG = LoggerFactory.getLogger(EventListenerService.class);

  private final StorableScriptRepository scriptRepository;

  @Inject
  public EventListenerService(StorableScriptRepository scriptRepository) {
    this.scriptRepository = scriptRepository;
  }

  public Optional<Trigger> createTrigger(Class<?> eventClass, boolean asynchronous) {
    String eventType = eventClass.getName();
    List<StorableScript> scripts = scriptRepository.findAll().stream().filter(script -> {
      if (asynchronous) {
        return script.isListeningAsynchronous(eventType);
      }
      return script.isListeningSynchronous(eventType);
    }).collect(Collectors.toList());

    if (scripts.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new Trigger(new Listener(eventType, asynchronous), scripts));
  }

  public class Trigger {

    private final Listener listener;
    private final List<StorableScript> scripts;
    private final List<StorableScript> needsStoring = new ArrayList<>();

    private Trigger(Listener listener, List<StorableScript> scripts) {
      this.listener = listener;
      this.scripts = scripts;
    }

    public void execute(Executor executor, ExecutionContext context) {
      for (StorableScript script : scripts) {
        ExecutionResult result = executor.execute(script, context);
        LOG.debug("script {} triggered by {} event {}: {}", script, listener.isAsynchronous() ? "asynchronous" : "synchronous", listener.getEventType(), result);
        if (script.captureListenerExecution(listener, result)) {
          needsStoring.add(script);
        }
      }
    }

    public void store() {
      for (StorableScript script : needsStoring) {
        LOG.debug("store script: {}", script);
        scriptRepository.store(script);
      }
    }

  }

}
