package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTest {

  @Test
  void shouldTurnNullIntoEmpty() {
    Content content = Content.valueOf(null);
    assertThat(content.getValue()).isEmpty();
  }

}
