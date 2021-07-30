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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionResultTest {

  @Test
  void shouldCreateSuccessString() {
    ExecutionResult result = createExecutionResult(true);
    assertThat(result).hasToString("success in 1ms with output: awesome output");
  }

  @Test
  void shouldCreateFailureString() {
    ExecutionResult result = createExecutionResult(false);
    assertThat(result).hasToString("failed in 1ms with output: awesome output");
  }

  private ExecutionResult createExecutionResult(boolean success) {
    if (success) {
      return new ExecutionResult("awesome output", Instant.ofEpochMilli(42L), Instant.ofEpochMilli(43L));
    } else {
      return new ExecutionResult(new RuntimeException(), "awesome output", Instant.ofEpochMilli(42L), Instant.ofEpochMilli(43L));
    }
  }
}
