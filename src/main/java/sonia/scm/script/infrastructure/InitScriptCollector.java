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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.SCMContext;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.ScriptExecutionException;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InitScriptCollector {

  private static final Logger LOG = LoggerFactory.getLogger(InitScriptCollector.class);

  private static final String PROPERTY_DIRECTORY = "sonia.scm.init.script.d";
  private static final String DEFAULT_DIRECTORY = "init.script.d";

  private final InitScriptFactory factory;
  private final Path directory;

  @Inject
  public InitScriptCollector(InitScriptFactory factory) {
    this.factory = factory;
    this.directory = findDirectory();
  }

  @VisibleForTesting
  InitScriptCollector(InitScriptFactory factory, Path directory) {
    this.factory = factory;
    this.directory = directory;
  }

  private Path findDirectory() {
    String path = System.getProperty(PROPERTY_DIRECTORY);
    if (Strings.isNullOrEmpty(path)) {
      return Paths.get(SCMContext.getContext().getBaseDirectory().getAbsolutePath(), DEFAULT_DIRECTORY);
    }
    return Paths.get(path);
  }

  /**
   * Collects a list of scripts from the init script directory. If no script could be found or the directory does not
   * exists an empty list is returned.
   *
   * @return a list of collected scripts
   */
  @SuppressWarnings("squid:S3725") // the performance impact is minimal
  public List<InitScript> collect() {
    if (Files.isDirectory(directory)) {
      return collectFromDirectory(directory);
    }

    LOG.info("init script directory {}, does not exists", directory);
    return Collections.emptyList();
  }

  private List<InitScript> collectFromDirectory(Path directory) {
    List<Path> paths = Ordering.natural().sortedCopy(findFiles(directory));
    return paths.stream()
      .map(factory::create)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }

  @SuppressWarnings("squid:S3725") // the performance impact is minimal
  private ImmutableList<Path> findFiles(Path directory) {
    try {
      return ImmutableList.copyOf(Files.newDirectoryStream(directory, Files::isRegularFile));
    } catch (IOException e) {
      throw new ScriptExecutionException("failed to read init script directory", e);
    }
  }

}
