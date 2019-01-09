package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventListenerServiceTest {

  @Mock
  private ScriptRepository scriptRepository;

  @Mock
  private Executor executor;

  @Mock
  private ExecutionContext executionContext;

  @InjectMocks
  private EventListenerService listenerService;

  @Test
  void shouldReturnExecutableTrigger() {
    List<Script> scripts = new ArrayList<>();

    scripts.add(script());

    Script script = script(Integer.class, true);
    scripts.add(script);
    scripts.add(script(Integer.class, false));
    scripts.add(script(String.class, true));

    when(scriptRepository.findAll()).thenReturn(scripts);

    EventListenerService.Trigger trigger = listenerService.createTrigger(Integer.class, true).get();
    trigger.execute(executionContext);

    verify(executor).execute(script, executionContext);
  }

  @Test
  void shouldReturnEmptyIfNoListenerIsRegistered() {
    List<Script> scripts = new ArrayList<>();

    when(scriptRepository.findAll()).thenReturn(scripts);

    Optional<EventListenerService.Trigger> trigger = listenerService.createTrigger(String.class, true);

    assertThat(trigger).isNotPresent();
  }

  private Script script(Class<?> eventType, boolean asynchronous) {
    Script script = script();
    script.addListener(Listener.valueOf(eventType, asynchronous));
    return script;
  }

  private Script script() {
    return new Script("groovy", "print 'Hello';");
  }

}
