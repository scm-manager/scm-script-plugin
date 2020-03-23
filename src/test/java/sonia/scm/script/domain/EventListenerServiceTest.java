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

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerServiceTest {

  @Mock
  private StorableScriptRepository scriptRepository;

  @Mock
  private Executor executor;

  @Mock
  private ExecutionContext executionContext;

  @InjectMocks
  private EventListenerService listenerService;

  @Test
  void shouldReturnExecutableTrigger() {
    List<StorableScript> scripts = new ArrayList<>();

    scripts.add(script());

    StorableScript script = script(Integer.class, true);
    scripts.add(script);
    scripts.add(script(Integer.class, false));
    scripts.add(script(String.class, true));

    when(scriptRepository.findAll()).thenReturn(scripts);

    EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, true).get();
    trigger.execute(executor, executionContext);

    verify(executor).execute(script, executionContext);
  }

  @Test
  void shouldReturnEmptyIfNoListenerIsRegistered() {
    List<StorableScript> scripts = new ArrayList<>();

    when(scriptRepository.findAll()).thenReturn(scripts);

    Optional<EventListenerService.Trigger> trigger = listenerService.createTrigger(String.class, true);

    assertThat(trigger).isNotPresent();
  }

  @Test
  void shouldCaptureExecution() {
    StorableScript script = script(Integer.class, false);
    script.setStoreListenerExecutionResults(true);

    ExecutionResult result = new ExecutionResult(true, "Hello World", Instant.now(), Instant.now());

    when(scriptRepository.findAll()).thenReturn(ImmutableList.of(script));
    when(executor.execute(script, executionContext)).thenReturn(result);

    EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, false).get();
    trigger.execute(executor, executionContext);
    trigger.store();

    verify(executor).execute(script, executionContext);
    verify(scriptRepository).store(script);

    ExecutionHistoryEntry historyEntry = script.getExecutionHistory().get(0);
    assertThat(historyEntry.getResult().getOutput()).isEqualTo("Hello World");
  }

  @Test
  void shouldNotStoreScriptIfNothingWasCaptured() {
    StorableScript script = script(Integer.class, false);
    script.setStoreListenerExecutionResults(false);

    ExecutionResult result = new ExecutionResult(true, "Hello World", Instant.now(), Instant.now());

    when(scriptRepository.findAll()).thenReturn(ImmutableList.of(script));
    when(executor.execute(script, executionContext)).thenReturn(result);

    EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, false).get();
    trigger.execute(executor, executionContext);

    verify(executor).execute(script, executionContext);
    verify(scriptRepository, never()).store(script);
  }

  private StorableScript script(Class<?> eventType, boolean asynchronous) {
    StorableScript script = script();
    script.addListener(new Listener(eventType.getName(), asynchronous));
    return script;
  }

  private StorableScript script() {
    return new StorableScript("groovy", "print 'Hello';");
  }

}
