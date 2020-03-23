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

import com.google.common.base.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.TypeRepository;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TempDirectory.class})
class InitScriptFactoryTest {

  @Mock
  private TypeRepository typeRepository;

  @InjectMocks
  private InitScriptFactory initScriptFactory;

  private Path directory;

  @BeforeEach
  void createTempDirectory(@TempDirectory.TempDir Path directory) {

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
