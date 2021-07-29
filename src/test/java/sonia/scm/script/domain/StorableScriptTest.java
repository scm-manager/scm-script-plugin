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
package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;
import sonia.scm.script.ScriptTestData;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StorableScriptTest {

  @Test
  void shouldHandleListeners() {
    StorableScript script = new StorableScript("groovy", null);

    Listener listener = new Listener(Number.class, false);
    script.addListener(listener);

    assertThat(script.isListeningSynchronous(Number.class.getName())).isTrue();

    assertThat(script.isListeningSynchronous(String.class.getName())).isFalse();
    assertThat(script.isListeningAsynchronous(Integer.class.getName())).isFalse();

    script.removeListener(listener);
    assertThat(script.isListeningSynchronous(Number.class.getName())).isFalse();
  }

  @Test
  void shouldMarshalAndUnmarshal() {
    StorableScript script = ScriptTestData.createHelloWorld();

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JAXB.marshal(script, output);

    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    script = JAXB.unmarshal(input, StorableScript.class);

    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getTitle()).contains("Hello World");
    assertThat(script.getDescription()).contains("Awesome Hello World");
    assertThat(script.getContent()).isEqualTo("println 'Hello World'");
  }

  @Test
  void shouldCaptureListenerExecutions() {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setStoreListenerExecutionResults(true);

    Listener listener = new Listener(String.class, true);
    ExecutionResult result = new ExecutionResult("hello world", Instant.now(), Instant.now());

    boolean captured = script.captureListenerExecution(listener, result);
    assertThat(captured).isTrue();

    List<ExecutionHistoryEntry> history = script.getExecutionHistory();
    assertThat(history).hasSize(1);

    ExecutionHistoryEntry historyEntry = history.get(0);
    assertThat(historyEntry.getListener()).isSameAs(listener);
    assertThat(historyEntry.getResult()).isSameAs(result);
  }

  @Test
  void shouldNotCaptureListenerExecutions() {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setStoreListenerExecutionResults(false);

    Listener listener = new Listener(String.class, true);
    ExecutionResult result = new ExecutionResult("hello world", Instant.now(), Instant.now());

    boolean captured = script.captureListenerExecution(listener, result);
    assertThat(captured).isFalse();

    List<ExecutionHistoryEntry> history = script.getExecutionHistory();
    assertThat(history).isEmpty();
  }

  @Test
  void shouldNotCaptureMoreThanTheLimit() {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setStoreListenerExecutionResults(true);

    Listener listener = new Listener(String.class.getName(), true);
    for (int i = 1; i <= StorableScript.CAPTURE_LIMIT + 10; i++) {
      ExecutionResult result = new ExecutionResult(String.valueOf(i), Instant.now(), Instant.now());
      script.captureListenerExecution(listener, result);
    }

    List<ExecutionHistoryEntry> history = script.getExecutionHistory();
    assertThat(history).hasSize(StorableScript.CAPTURE_LIMIT);

    ExecutionHistoryEntry first = history.get(0);
    assertThat(first.getResult().getOutput()).isEqualTo(String.valueOf( StorableScript.CAPTURE_LIMIT + 10 ));
  }

}
