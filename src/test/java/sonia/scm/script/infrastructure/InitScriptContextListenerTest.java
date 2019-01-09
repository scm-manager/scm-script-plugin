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
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.Type;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

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

  @InjectMocks
  private InitScriptContextListener listener;

  @Captor
  private ArgumentCaptor<Script> scriptCaptor;

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
    Script one = createScript("one");
    Script two = createScript("two");
    List<Script> scripts = Lists.newArrayList(one, two);

    when(collector.collect()).thenReturn(scripts);

    listener.contextInitialized(null);

    verify(executor, times(2)).execute(scriptCaptor.capture(), any(ExecutionContext.class));
    assertThat(scriptCaptor.getAllValues()).containsOnly(one, two);
  }

  private Script createScript(String one) {
    return new Script(Type.valueOf("Groovy"), Content.valueOf(one));
  }

}