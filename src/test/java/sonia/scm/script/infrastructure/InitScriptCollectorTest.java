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

@ExtendWith(MockitoExtension.class)
class InitScriptCollectorTest {

  @Mock
  private InitScriptFactory factory;

  private InitScriptCollector collector;

  private Path directory;

  @BeforeEach
  void prepare(@TempDir Path directory) {
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
