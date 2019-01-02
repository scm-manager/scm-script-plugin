package sonia.scm.script.domain;

import com.google.common.base.Strings;
import lombok.Value;

@Value
public class Content {

  private final String value;

  private Content(String value) {
    this.value = value;
  }

  public static Content valueOf(String value) {
    return new Content(Strings.nullToEmpty(value));
  }
}
