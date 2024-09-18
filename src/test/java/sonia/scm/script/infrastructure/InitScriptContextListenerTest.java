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

package sonia.scm.script.infrastructure;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.event.ScmEventBus;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.InitScript;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitScriptContextListenerTest {

  @Mock
  private AdministrationContext administrationContext;

  @Mock
  private InitScriptCollector collector;

  @Mock
  private Executor executor;

  @Mock
  private ScmEventBus eventBus;

  @InjectMocks
  private InitScriptContextListener listener;

  @Captor
  private ArgumentCaptor<InitScript> scriptCaptor;

  @BeforeEach
  void prepareMocks() {
    doAnswer((ic) -> {
      PrivilegedAction action = ic.getArgument(0);
      action.run();
      return null;
    }).when(administrationContext).runAsAdmin(any(PrivilegedAction.class));
  }

  @Test
  void shouldExecuteTheScripts() {
    InitScript one = createScript("one");
    InitScript two = createScript("two");
    List<InitScript> scripts = Lists.newArrayList(one, two);

    when(collector.collect()).thenReturn(scripts);

    listener.contextInitialized(null);

    verify(executor, times(2)).execute(scriptCaptor.capture(), any(ExecutionContext.class));
    assertThat(scriptCaptor.getAllValues()).containsOnly(one, two);
    verify(eventBus).post(any(ScmStartedEvent.class));
  }

  private InitScript createScript(String one) {
    return new InitScript(Paths.get(one), "Groovy", one);
  }

}
