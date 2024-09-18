/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.script.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
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
        if (!result.isSuccess() && result.getException().isPresent()) {
          throw new TriggeredScriptFailedException(result.getException().get());
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

  private static class TriggeredScriptFailedException extends RuntimeException {
    public TriggeredScriptFailedException(Throwable cause) {
      super("Triggered script failed with internal exception: " + cause, cause);
    }
  }
}
