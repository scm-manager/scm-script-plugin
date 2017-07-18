package sonia.scm.script.impl;

import sonia.scm.script.ScriptType;

import java.io.File;

/**
 * Script which gets executed on scm-manager init process. A init can be used to handle automatic installation tasks,
 * such as plugin installations.
 */
public final class InitScript implements Comparable<InitScript> {

  private final ScriptType type;
  private final File file;

  public InitScript(ScriptType type, File file) {
    this.type = type;
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public ScriptType getType() {
    return type;
  }

  @Override
  public int compareTo(InitScript o) {
    return file.getName().compareTo(o.file.getName());
  }
}
