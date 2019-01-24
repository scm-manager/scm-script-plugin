package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.EventListenerService;
import sonia.scm.script.domain.ExecutionContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

  @Mock
  private EventListenerService service;

  @InjectMocks
  private EventListener eventListener;

  @Test
  void shouldHandleAsyncEvent() {
    when(service.createTrigger(String.class, true)).thenReturn(Optional.empty());

    eventListener.subscribeAsync("Hello World");
  }

  @Test
  void shouldHandleSyncEvent() {
    when(service.createTrigger(String.class, false)).thenReturn(Optional.empty());

    eventListener.subscribeSync("Hello World");
  }

  @Test
  void shouldExecuteTrigger() {
    EventListenerService.Trigger trigger = mock(EventListenerService.Trigger.class);

    when(service.createTrigger(String.class, false)).thenReturn(Optional.of(trigger));

    eventListener.subscribeSync("Hello World");

    ArgumentCaptor<ExecutionContext> contextArgumentCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
    verify(trigger).execute(contextArgumentCaptor.capture());

    ExecutionContext context = contextArgumentCaptor.getValue();
    assertThat(context.getAttributes().get("event")).isEqualTo("Hello World");
  }

}
