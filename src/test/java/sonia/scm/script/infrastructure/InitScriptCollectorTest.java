/**
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.InitScript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TempDirectory.class})
class InitScriptCollectorTest {

  @Mock
  private InitScriptFactory factory;

  private InitScriptCollector collector;

  private Path directory;

  @BeforeEach
  void prepare(@TempDirectory.TempDir Path directory) {
    this.directory = directory;
    collector = new InitScriptCollector(factory, directory);
  }

  @Test
  void shouldCollectAndSortTheScripts() throws IOException {
    when(factory.create(any(Path.class))).thenAnswer(ic -> Optional.of(readGroovyScript(ic.getArgument(0))));

    writeGroovyScript("020");
    writeGroovyScript("040");
    writeGroovyScript("010");
    writeGroovyScript("030");

    List<InitScript> scripts = collector.collect();
    assertThat(scripts).hasSize(4);

    assertThat(scripts.get(0).getContent()).isEqualTo("010");
    assertThat(scripts.get(1).getContent()).isEqualTo("020");
    assertThat(scripts.get(2).getContent()).isEqualTo("030");
    assertThat(scripts.get(3).getContent()).isEqualTo("040");
  }

  @Test
  void shouldIgnoreUnknownTests() throws IOException {
    when(factory.create(any(Path.class))).thenAnswer(ic -> {
      Path path = ic.getArgument(0);
      if (path.toString().contains("020")) {
        return Optional.empty();
      }
      return Optional.of(readGroovyScript(path));
    });

    writeGroovyScript("040");
    writeGroovyScript("020");
    writeGroovyScript("010");

    List<InitScript> scripts = collector.collect();
    assertThat(scripts).hasSize(2);

    assertThat(scripts.get(0).getContent()).isEqualTo("010");
    assertThat(scripts.get(1).getContent()).isEqualTo("040");
  }

  @Test
  void shouldReturnEmptyListIfDirectoryIsAFile() throws IOException {
    Files.deleteIfExists(directory);
    Files.createFile(directory);

    List<InitScript> scripts = collector.collect();
    assertThat(scripts).isEmpty();
  }


  private InitScript readGroovyScript(Path path) throws IOException {
    return new InitScript(path, "Groovy", new String(Files.readAllBytes(path), Charsets.UTF_8));
  }

  private void writeGroovyScript(String name) throws IOException {
    writeScript(name, "groovy");
  }

  private void writeScript(String name, String extension) throws IOException {
    Path script = directory.resolve(name + "-hello." + extension);
    Files.write(script, name.getBytes(Charsets.UTF_8));
  }

}
