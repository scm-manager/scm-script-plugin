package sonia.scm.script.domain;

import com.google.common.collect.Iterables;
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

    assertThat(script.isListeningSynchronous(Number.class)).isTrue();
    assertThat(script.isListeningSynchronous(Integer.class)).isTrue();

    assertThat(script.isListeningSynchronous(String.class)).isFalse();
    assertThat(script.isListeningAsynchronous(Integer.class)).isFalse();

    script.removeListener(listener);
    assertThat(script.isListeningSynchronous(Number.class)).isFalse();
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
    ExecutionResult result = new ExecutionResult(true, "hello world", Instant.now(), Instant.now());

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
    ExecutionResult result = new ExecutionResult(true, "hello world", Instant.now(), Instant.now());

    boolean captured = script.captureListenerExecution(listener, result);
    assertThat(captured).isFalse();

    List<ExecutionHistoryEntry> history = script.getExecutionHistory();
    assertThat(history).isEmpty();
  }

  @Test
  void shouldNotCaptureMoreThanTheLimit() {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setStoreListenerExecutionResults(true);

    Listener listener = new Listener(String.class, true);
    for (int i = 1; i <= StorableScript.CAPTURE_LIMIT + 10; i++) {
      ExecutionResult result = new ExecutionResult(true, String.valueOf(i), Instant.now(), Instant.now());
      script.captureListenerExecution(listener, result);
    }

    List<ExecutionHistoryEntry> history = script.getExecutionHistory();
    assertThat(history).hasSize(StorableScript.CAPTURE_LIMIT);

    ExecutionHistoryEntry first = history.get(0);
    assertThat(first.getResult().getOutput()).isEqualTo(String.valueOf( StorableScript.CAPTURE_LIMIT + 10 ));
  }

}
