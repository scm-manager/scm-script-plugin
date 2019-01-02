package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListenerTest {

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfEventTypeIsNull() {
    assertThrows(IllegalArgumentException.class, () -> Listener.valueOf(null, false));
  }

}
