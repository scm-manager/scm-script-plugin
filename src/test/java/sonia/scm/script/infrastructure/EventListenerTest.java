package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.EventListenerService;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

  @Mock
  private EventListenerService service;

  @Mock
  private JSR223InternalExecutor executor;

  @Mock
  private AdministrationContext administrationContext;

  @InjectMocks
  private EventListener eventListener;

  @BeforeEach
  void setUpAdministrationContext() {
    doAnswer((ic) -> {
      PrivilegedAction action = ic.getArgument(0);
      action.run();
      return null;
    }).when(administrationContext).runAsAdmin(any(PrivilegedAction.class));
  }

  @Test
  void shouldHandleAsyncEvent() {
    eventListener.subscribeAsync("Hello World");

    verify(service).createTrigger(String.class, true);
  }

  @Test
  void shouldHandleSyncEvent() {
    eventListener.subscribeSync("Hello World");

    verify(service).createTrigger(String.class, false);
  }

  @Test
  void shouldExecuteTrigger() {
    EventListenerService.Trigger trigger = mock(EventListenerService.Trigger.class);

    when(service.createTrigger(String.class, false)).thenReturn(Optional.of(trigger));

    eventListener.subscribeSync("Hello World");

    ArgumentCaptor<ExecutionContext> contextArgumentCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
    verify(trigger).execute(any(Executor.class), contextArgumentCaptor.capture());

    ExecutionContext context = contextArgumentCaptor.getValue();
    assertThat(context.getAttributes().get("event")).isEqualTo("Hello World");
  }

}
