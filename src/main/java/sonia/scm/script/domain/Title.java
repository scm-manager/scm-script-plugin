package sonia.scm.script.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Value;

@Value
public final class Title {
  private final String value;

  private Title(String value) {
    this.value = value;
  }

  public static Title valueOf(String value) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
    return new Title(value);
  }
}
