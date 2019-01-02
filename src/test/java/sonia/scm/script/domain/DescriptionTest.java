package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DescriptionTest {

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsNull() {
    assertThrows(IllegalArgumentException.class, () -> Description.valueOf(null));
  }

  @Test
  void shouldThrowAnIllegalArgumentExceptionIfValueIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> Description.valueOf(""));
  }


}
