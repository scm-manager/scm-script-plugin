package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptTest {

  @Test
  void shouldHandleListeners() {
    Script script = new Script("groovy", null);

    Listener listener = Listener.valueOf(Number.class, false);
    script.addListener(listener);

    assertThat(script.isListeningSynchronous(Number.class)).isTrue();
    assertThat(script.isListeningSynchronous(Integer.class)).isTrue();

    assertThat(script.isListeningSynchronous(String.class)).isFalse();
    assertThat(script.isListeningAsynchronous(Integer.class)).isFalse();

    script.removeListener(listener);
    assertThat(script.isListeningSynchronous(Number.class)).isFalse();
  }

  @Test
  void shouldMarshalAndUnmarshal() {
    Script script = new Script(
      "42",
      "Groovy",
      "Hello World",
      "Awesome Hello World",
      "println 'Hello World'",
      new ArrayList<>()
    );

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JAXB.marshal(script, output);

    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    script = JAXB.unmarshal(input, Script.class);

    assertThat(script.getId()).contains("42");
    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getTitle()).contains("Hello World");
    assertThat(script.getDescription()).contains("Awesome Hello World");
    assertThat(script.getContent()).isEqualTo("println 'Hello World'");
  }

}
