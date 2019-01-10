package sonia.scm.script.domain;

import java.nio.file.Path;

public class InitScript extends Script {

  private final Path path;

  public InitScript(Path path, String type, String content) {
    super(type, content);
    this.path = path;
  }

  public Path getPath() {
    return path;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", path, getType());
  }
}
