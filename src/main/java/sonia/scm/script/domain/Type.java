package sonia.scm.script.domain;

import com.google.common.base.Preconditions;
import lombok.Value;

import java.util.regex.Pattern;

@Value
public final class Type {

  private static final Pattern pattern = Pattern.compile("[A-Za-z0-9]+");

  private final String value;

  private Type(String value) {
    this.value = value;
  }

  public static Type valueOf(String value) {
    Preconditions.checkArgument(value != null && pattern.matcher(value).matches(),"value is required");
    return new Type(value);
  }
}
