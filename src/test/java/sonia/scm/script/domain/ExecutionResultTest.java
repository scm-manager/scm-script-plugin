package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionResultTest {

  @Test
  void shouldCreateSuccessString() {
    ExecutionResult result = createExecutionResult(true);
    assertThat(result.toString()).isEqualTo("success in 1ms with output: awesome output");
  }

  @Test
  void shouldCreateFailureString() {
    ExecutionResult result = createExecutionResult(false);
    assertThat(result.toString()).isEqualTo("failed in 1ms with output: awesome output");
  }

  private ExecutionResult createExecutionResult(boolean success) {
    return new ExecutionResult(success, "awesome output", Instant.ofEpochMilli(42L), Instant.ofEpochMilli(43L));
  }

}
