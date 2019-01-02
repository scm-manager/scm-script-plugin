package sonia.scm.script.domain;

import sonia.scm.NotFoundException;

public class ScriptNotFoundException extends NotFoundException {
  public ScriptNotFoundException(Id id) {
    super(Script.class, id.getValue());
  }
}
