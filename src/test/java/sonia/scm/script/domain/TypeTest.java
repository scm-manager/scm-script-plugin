package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsNull() {
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf(null));
  }

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf(""));
  }

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueContainsInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf("|"));
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf("/"));
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf("="));
    assertThrows(IllegalArgumentException.class, () -> Type.valueOf("&"));
  }

}
