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

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Nested
  class WithScript {

    private final List<StorableScript> scripts = new ArrayList<>();
    private StorableScript script;

    @BeforeEach
    void mockStoredScript() {
      scripts.add(script());

      script = script(Integer.class, true);
      scripts.add(script);
      scripts.add(script(Integer.class, false));
      scripts.add(script(String.class, true));

      when(scriptRepository.findAll()).thenReturn(scripts);
    }

    @Test
    void shouldReturnExecutableTrigger() {
      when(executor.execute(any(), any())).thenReturn(new ExecutionResult("excellent", now(), now()));

      EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, true).get();
      trigger.execute(executor, executionContext);

      verify(executor).execute(script, executionContext);
    }

    @Test
    void shouldThrowExceptionIfExecutableTriggerThrowsException() {
      when(executor.execute(any(), any())).thenThrow(RuntimeException.class);

      EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, true).get();

      assertThrows(RuntimeException.class, () -> trigger.execute(executor, executionContext));

      verify(executor).execute(script, executionContext);
    }
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

    ExecutionResult result = new ExecutionResult("Hello World", now(), now());

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

    ExecutionResult result = new ExecutionResult("Hello World", now(), now());

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
