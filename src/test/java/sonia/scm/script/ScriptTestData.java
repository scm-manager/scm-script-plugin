package sonia.scm.script;

import sonia.scm.script.domain.Script;

import java.util.ArrayList;

public final class ScriptTestData {

  private ScriptTestData() {

  }

  public static Script createHelloWorld() {
    return new Script(
      "42",
      "Groovy",
      "Hello World",
      "Awesome Hello World",
      "println 'Hello World'",
      new ArrayList<>()
    );
  }
}
