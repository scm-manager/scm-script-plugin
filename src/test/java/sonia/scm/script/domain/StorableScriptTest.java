package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;
import sonia.scm.script.ScriptTestData;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class StorableScriptTest {

  @Test
  void shouldHandleListeners() {
    StorableScript script = new StorableScript("groovy", null);

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
    StorableScript script = ScriptTestData.createHelloWorld();

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JAXB.marshal(script, output);

    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    script = JAXB.unmarshal(input, StorableScript.class);

    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getTitle()).contains("Hello World");
    assertThat(script.getDescription()).contains("Awesome Hello World");
    assertThat(script.getContent()).isEqualTo("println 'Hello World'");
  }

}
