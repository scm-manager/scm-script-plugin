package sonia.scm.script.domain;

import sonia.scm.NotFoundException;

@SuppressWarnings("squid:MaximumInheritanceDepth") // it is ok for exceptions
public class ScriptNotFoundException extends NotFoundException {
  public ScriptNotFoundException(String id) {
    super(StorableScript.class, id);
  }
}
