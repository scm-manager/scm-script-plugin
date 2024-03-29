/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
