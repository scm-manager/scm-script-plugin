package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdTest {

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsNull() {
    assertThrows(IllegalArgumentException.class, () -> Id.valueOf(null));
  }

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> Id.valueOf(""));
  }

}
