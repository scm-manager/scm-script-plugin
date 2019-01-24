package sonia.scm.script;

import sonia.scm.script.domain.StorableScript;

import java.util.ArrayList;

public final class ScriptTestData {

  private ScriptTestData() {

  }

  public static StorableScript createHelloWorld() {
    StorableScript script = new StorableScript(
      "Groovy",
      "println 'Hello World'"
    );
    script.setId("42");
    script.setTitle("Hello World");
    script.setDescription("Awesome Hello World");
    return script;
  }
}
