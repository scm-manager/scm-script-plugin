package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.Description;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.ScriptTypeNotFoundException;
import sonia.scm.script.domain.Type;

import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSR223ExecutorTest {

  private JSR223Executor executor;

  @BeforeEach
  void beforeEach() {
    executor = new JSR223Executor(ScriptEngineManagerProvider.context());
  }

  @Test
  void shouldExecuteGroovyScript() {
    Script script = createScript("print \"Don't Panic\"");
    assertThat(execute(script)).isEqualTo("Don't Panic");
  }

  @Test
  void shouldPassInput() {
    Script script = createScript("print context.reader.readLine()");
    assertThat(execute(script, "Don't Panic").trim()).isEqualTo("Don't Panic");
  }

  @Test
  void shouldPassAttribute() {
    StringWriter writer = new StringWriter();
    ExecutionContext context = ExecutionContext.builder()
      .withOutput(writer)
      .withAttribute("message", "Don't Panic")
      .build();

    Script script = createScript("print message");
    executor.execute(script, context);

    assertThat(writer.toString()).isEqualTo("Don't Panic");
  }

  @Test
  void shouldThrowScriptTypeNotFoundException() {
    Script script = new Script(Type.valueOf("hitchhikerScripting"), Description.valueOf("ka"), Content.valueOf(""));
    assertThrows(ScriptTypeNotFoundException.class, () -> executor.execute(script, ExecutionContext.builder().build()));
  }

  @Test
  void shouldThrowScriptExecutionException() {
    Script script = new Script(Type.valueOf("Groovy"), Description.valueOf("ka"), Content.valueOf("should fail"));
    assertThrows(ScriptExecutionException.class, () -> executor.execute(script, ExecutionContext.builder().build()));
  }

  private Script createScript(String content) {
    return new Script(Type.valueOf("Groovy"), Description.valueOf("some value"), Content.valueOf(content));
  }

  private String execute(Script script) {
    return execute(script, "");
  }

  private String execute(Script script, String input) {
    StringWriter writer = new StringWriter();
    ExecutionContext context = ExecutionContext.builder()
      .withOutput(writer)
      .withInput(new StringReader(input))
      .build();

    executor.execute(script, context);

    return writer.toString();
  }


}
