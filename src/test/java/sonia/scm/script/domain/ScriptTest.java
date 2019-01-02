package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptTest {

  @Test
  void shouldHandleListeners() {
    Script script = new Script(Type.valueOf("groovy"), Description.valueOf("simple"), Content.valueOf(null));

    Listener listener = Listener.valueOf(Number.class, false);
    script.addListener(listener);

    assertThat(script.isListeningSynchronous(Number.class)).isTrue();
    assertThat(script.isListeningSynchronous(Integer.class)).isTrue();

    assertThat(script.isListeningSynchronous(String.class)).isFalse();
    assertThat(script.isListeningAsynchronous(Integer.class)).isFalse();

    script.removeListener(listener);
    assertThat(script.isListeningSynchronous(Number.class)).isFalse();
  }

}
