package sonia.scm.script.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.io.MoreFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.SCMContext;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.Description;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.Type;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
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

  private final TypeRepository repository;
  private final Path directory;

  @Inject
  public InitScriptCollector(TypeRepository repository) {
    this.repository = repository;
    this.directory = findDirectory();
  }

  @VisibleForTesting
  InitScriptCollector(TypeRepository repository, Path directory) {
    this.repository = repository;
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
  public List<Script> collect() {
    if (Files.isDirectory(directory)) {
      return collectFromDirectory(directory);
    }

    LOG.info("init script directory {}, does not exists", directory);
    return Collections.emptyList();
  }

  private List<Script> collectFromDirectory(Path directory) {
    List<Path> paths = Ordering.natural().sortedCopy(findFiles(directory));
    return paths.stream()
      .map(this::from)
      .filter(p -> p != null)
      .collect(Collectors.toList());
  }

  private Script from(Path path) {
    String extension = MoreFiles.getFileExtension(path);
    Optional<Type> byExtension = repository.findByExtension(extension);
    if (byExtension.isPresent()) {
      return createScript(byExtension.get(), path);
    }
    return null;
  }

  private Script createScript(Type type, Path path) {
    try {
      Content content = readContent(path);
      Script script = new Script(type, content);
      script.changeDescription(createDescription(path));
      return script;
    } catch (IOException ex) {
      LOG.error("failed to read init script: ".concat(path.toString()), ex);
    }
    return null;
  }

  private Description createDescription(Path path) {
    return Description.valueOf("Init-Script ".concat(path.toString()));
  }

  private Content readContent(Path path) throws IOException {
    String value = new String(Files.readAllBytes(path), Charsets.UTF_8);
    return Content.valueOf(value);
  }

  private ImmutableList<Path> findFiles(Path directory) {
    try {
      return ImmutableList.copyOf(Files.newDirectoryStream(directory, entry -> Files.isRegularFile(entry)));
    } catch (IOException e) {
      throw new ScriptExecutionException("failed to read init script directory", e);
    }
  }

}
