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

import com.google.common.base.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.TypeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitScriptFactoryTest {

  @Mock
  private TypeRepository typeRepository;

  @InjectMocks
  private InitScriptFactory initScriptFactory;

  private Path directory;

  @BeforeEach
  void createTempDirectory(@TempDir Path directory) {

    this.directory = directory;
  }

  @Test
  void shouldCreateInitScript() throws IOException {
    when(typeRepository.findByExtension("groovy")).thenReturn(Optional.of("Groovy"));

    Path path = directory.resolve("hello-world.groovy");
    Files.write(path, "println 'Hello World';".getBytes(Charsets.UTF_8));

    InitScript script = initScriptFactory.create(path).get();
    assertThat(script.getPath()).isSameAs(path);
    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getContent()).isEqualTo("println 'Hello World';");
  }

  @Test
  void shouldReturnEmptyOnReadFailure() {
    when(typeRepository.findByExtension("groovy")).thenReturn(Optional.of("Groovy"));

    Path path = directory.resolve("hello-world.groovy");

    Optional<InitScript> initScript = initScriptFactory.create(path);
    assertThat(initScript).isNotPresent();
  }

  @Test
  void shouldReturnEmptyOnUnknownExtension() throws IOException {
    Path path = directory.resolve("hello-world.groovy");
    Files.write(path, "println 'Hello World';".getBytes(Charsets.UTF_8));

    Optional<InitScript> initScript = initScriptFactory.create(path);
    assertThat(initScript).isNotPresent();
  }

}
