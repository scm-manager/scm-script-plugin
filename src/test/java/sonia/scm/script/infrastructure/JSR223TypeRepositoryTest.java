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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JSR223TypeRepositoryTest {

  private JSR223TypeRepository repository;

  @BeforeEach
  void beforeEach() {
    repository = new JSR223TypeRepository(ScriptEngineManagerProvider.context());
  }

  @Test
  void shouldReturnAtLeastGroovy() {
    List<String> types = repository.findAll();
    long count = types.stream().filter(t -> "Groovy".equals(t)).count();
    assertThat(count).isOne();
  }

  @Test
  void shouldReturnTypeByExtension() {
    Optional<String> groovy = repository.findByExtension("groovy");
    assertThat(groovy).isPresent();
  }
}
