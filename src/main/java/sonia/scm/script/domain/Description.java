package sonia.scm.script.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Value;

@Value
public class Description {

  private final String value;

  private Description(String value) {
    this.value = value;
  }

  public static Description valueOf(String value) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
    return new Description(value);
  }
}
