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
package sonia.scm.script.infrastructure;

import com.google.inject.Injector;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.ScriptTypeNotFoundException;
import sonia.scm.script.domain.StorableScript;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JSR223InternalExecutorTest {

  private JSR223InternalExecutor executor;

  @Mock
  private Injector injector;

  @Mock
  private PluginLoader pluginLoader;

  @BeforeEach
  void beforeEach() {
    executor = new JSR223InternalExecutor(ScriptEngineManagerProvider.context(), pluginLoader, injector);
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
      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      assertSuccess(result, "Don't Panic");
    }

    private void assertSuccess(ExecutionResult result, String expectedOutput) {
      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getOutput()).isEqualTo(expectedOutput);
    }

    @Test
    void shouldPassInjector() {
      StorableScript script = createScript("print injector != null");
      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      assertSuccess(result, "true");
    }

    @Test
    void shouldPassAttribute() {
      ExecutionContext context = ExecutionContext.builder()
        .withAttribute("message", "Don't Panic")
        .build();

      StorableScript script = createScript("print message");
      ExecutionResult result = executor.execute(script, context);
      assertSuccess(result, "Don't Panic");
    }

    @Test
    void shouldTrimOutput() {
      StorableScript script = createScript("println ' Panic now  '");
      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      assertSuccess(result, "Panic now");
    }

    @Test
    void shouldCollectStartAndEndDate() {
      StorableScript script = createScript("print \"Don't Panic\"");
      assertStartedAndEnded(script);
    }

    @Test
    void shouldCollectionStartAndEndDateEvenOnAFailedScript() {
      StorableScript script = createScript("invalid");
      assertStartedAndEnded(script);
    }

    private void assertStartedAndEnded(StorableScript script) {
      Instant one = Instant.ofEpochMilli(42L);
      Instant two = Instant.ofEpochMilli(422L);

      mockClock(one, two);

      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      assertThat(result.getStarted()).isEqualTo(one);
      assertThat(result.getEnded()).isEqualTo(two);
    }

    private void mockClock(Instant... instants) {
      List<Instant> list = Lists.newArrayList(instants);
      Collections.reverse(list);

      Stack<Instant> stack = new Stack<>();
      stack.addAll(list);

      Clock clock = mock(Clock.class);
      when(clock.instant()).then(ic -> stack.pop());

      executor.setClock(clock);
    }

    @Test
    void shouldReturnFailedResultWithStackTrace() {
      StorableScript script = new StorableScript("Groovy", "should fail");
      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      assertThat(result.isSuccess()).isFalse();
      assertThat(result.getOutput()).contains("No such property");
    }
  }

  @Test
  void shouldThrowScriptTypeNotFoundException() {
    StorableScript script = new StorableScript("hitchhikerScripting", "");
    assertThrows(ScriptTypeNotFoundException.class, () -> executor.execute(script, ExecutionContext.builder().build()));
  }

  private StorableScript createScript(String content) {
    return new StorableScript("Groovy", content);
  }

}
