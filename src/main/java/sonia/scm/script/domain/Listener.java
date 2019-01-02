package sonia.scm.script.domain;

import com.google.common.base.Preconditions;
import lombok.Value;

@Value
public class Listener {

  private final Class<?> eventType;
  private final boolean asynchronous;

  private Listener(Class<?> eventType, boolean asynchronous) {
    this.eventType = eventType;
    this.asynchronous = asynchronous;
  }

  public static Listener valueOf(Class<?> eventType, boolean asynchronous) {
    Preconditions.checkArgument(eventType != null, "event type is required");
    return new Listener(eventType, asynchronous);
  }
}
