package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.ScriptTestData;
import sonia.scm.script.domain.Script;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptMapperTest {

  private ScriptMapper mapper;

  @BeforeEach
  void setUpObjectUnderTest() {
    mapper = new ScriptMapperImpl();
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    mapper.setScmPathInfoStore(pathInfoStore);
  }

  @Test
  void shouldAppendSelfLink() {
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/plugins/scripts/42");
  }

  @Test
  void shouldNotAppendSelfLinkWithoutId() {
    Script script = ScriptTestData.createHelloWorld();
    script.setId(null);
    ScriptDto dto = mapper.map(script);
    assertThat(dto.getLinks().getLinkBy("self")).isEmpty();
  }

  @Test
  void shouldCreateEmbedded() {
    List<Script> scripts = Lists.newArrayList(script("42"), script("21"));
    HalRepresentation collection = mapper.collection(scripts);
    List<HalRepresentation> embedded = collection.getEmbedded().getItemsBy("scripts");
    assertThat(embedded).hasSize(2);
  }

  @Test
  void shouldAppendSelfLinkToCollection() {
    HalRepresentation collection = mapper.collection(Lists.newArrayList(script("42"), script("21")));
    String self = collection.getLinks().getLinkBy("self").get().getHref();
    assertThat(self).isEqualTo("/v2/plugins/scripts");
  }

  private Script script(String id) {
    Script script = ScriptTestData.createHelloWorld();
    script.setId(id);
    return script;
  }

}
