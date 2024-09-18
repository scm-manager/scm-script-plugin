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
