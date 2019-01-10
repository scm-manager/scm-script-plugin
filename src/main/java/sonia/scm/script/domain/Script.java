package sonia.scm.script.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Script {

  private String type;
  private String content;

  protected Script() {
  }

  public Script(String type, String content) {
    this.type = type;
    this.content = content;
  }
}
