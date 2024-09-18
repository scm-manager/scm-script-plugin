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

import com.google.common.io.MoreFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.TypeRepository;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class InitScriptFactory {

  private static final Logger LOG = LoggerFactory.getLogger(InitScriptFactory.class);

  private TypeRepository repository;

  @Inject
  public InitScriptFactory(TypeRepository repository) {
    this.repository = repository;
  }

  Optional<InitScript> create(Path path) {
    String extension = MoreFiles.getFileExtension(path);
    Optional<String> byExtension = repository.findByExtension(extension);
    return byExtension.map(s -> createScript(s, path));
  }

  private InitScript createScript(String type, Path path) {
    try {
      String content = readContent(path);
      return new InitScript(path, type, content);
    } catch (IOException ex) {
      LOG.error("failed to read init script: ".concat(path.toString()), ex);
    }
    return null;
  }

  private String readContent(Path path) throws IOException {
    return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
  }

}
