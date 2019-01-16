package sonia.scm.script.infrastructure;

import com.google.inject.Injector;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.ScriptTypeNotFoundException;

import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JSR223ExecutorTest {

  private JSR223Executor executor;

  @Mock
  private Injector injector;

  @Mock
  private Subject subject;

  @Mock
  private PluginLoader pluginLoader;

  @BeforeEach
  void beforeEach() {
    executor = new JSR223Executor(ScriptEngineManagerProvider.context(), pluginLoader, injector);
    ThreadContext.bind(subject);
  }

  @AfterEach
  void afterEach() {
    ThreadContext.unbindSubject();
  }

  @Nested
  class ExecutionTests {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @BeforeEach
    void beforeEach() {
      when(pluginLoader.getUberClassLoader()).thenReturn(classLoader);
    }


    @Test
    void shouldExecuteGroovyScript() {
      StorableScript script = createScript("print \"Don't Panic\"");
      assertThat(execute(script)).isEqualTo("Don't Panic");
    }

    @Test
    void shouldPassInput() {
      StorableScript script = createScript("print context.reader.readLine()");
      assertThat(execute(script, "Don't Panic").trim()).isEqualTo("Don't Panic");
    }

    @Test
    void shouldPassInjector() {
      StorableScript script = createScript("print injector != null");
      assertThat(execute(script)).isEqualTo("true");
    }

    @Test
    void shouldPassAttribute() {
      StringWriter writer = new StringWriter();
      ExecutionContext context = ExecutionContext.builder()
        .withOutput(writer)
        .withAttribute("message", "Don't Panic")
        .build();

      StorableScript script = createScript("print message");
      executor.execute(script, context);

      assertThat(writer.toString()).isEqualTo("Don't Panic");
    }

  }

  @Test
  void shouldThrowScriptTypeNotFoundException() {
    StorableScript script = new StorableScript("hitchhikerScripting", "");
    assertThrows(ScriptTypeNotFoundException.class, () -> executor.execute(script, ExecutionContext.builder().build()));
  }

  @Test
  void shouldThrowScriptExecutionException() {
    StorableScript script = new StorableScript("Groovy", "should fail");
    assertThrows(ScriptExecutionException.class, () -> executor.execute(script, ExecutionContext.builder().build()));
  }

  @Test
  void shouldThrowAuthorizationException() {
    doThrow(AuthorizationException.class).when(subject).checkPermission("script:execute");

    StorableScript script = createScript("print \"Don't Panic\"");
    assertThrows(AuthorizationException.class, () -> execute(script));
  }

  private StorableScript createScript(String content) {
    return new StorableScript("Groovy", content);
  }

  private String execute(StorableScript script) {
    return execute(script, "");
  }

  private String execute(StorableScript script, String input) {
    StringWriter writer = new StringWriter();
    ExecutionContext context = ExecutionContext.builder()
      .withOutput(writer)
      .withInput(new StringReader(input))
      .build();

    executor.execute(script, context);

    return writer.toString();
  }


}
