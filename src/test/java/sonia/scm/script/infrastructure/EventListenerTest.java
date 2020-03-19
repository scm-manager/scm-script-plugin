/**
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
