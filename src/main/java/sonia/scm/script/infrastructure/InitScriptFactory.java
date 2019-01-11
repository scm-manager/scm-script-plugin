package sonia.scm.script.infrastructure;

import com.google.common.io.MoreFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
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
