package sonia.scm.script;

import sonia.scm.script.domain.StorableScript;

import java.util.ArrayList;

public final class ScriptTestData {

  private ScriptTestData() {

  }

  public static StorableScript createHelloWorld() {
    return new StorableScript(
      "42",
      "Groovy",
      "Hello World",
      "Awesome Hello World",
      "println 'Hello World'",
      new ArrayList<>()
    );
  }
}
