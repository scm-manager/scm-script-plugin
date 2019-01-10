package sonia.scm.script.domain;

import sonia.scm.NotFoundException;

public class ScriptNotFoundException extends NotFoundException {
  public ScriptNotFoundException(String id) {
    super(StorableScript.class, id);
  }
}
