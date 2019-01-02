package sonia.scm.script.domain;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Value;

@Value
public final class Id {

  private final String value;

  private Id(String value) {
    this.value = value;
  }

  public static Id valueOf(String value) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
    return new Id(value);
  }
}
