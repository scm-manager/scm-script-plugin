/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.script.infrastructure;

import com.google.common.collect.Sets;
import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.ScriptTestData;
import sonia.scm.script.domain.StorableScript;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptMapperTest {

  @Mock
  private Subject subject;

  private ScriptMapper mapper;

  @BeforeEach
  void setUpObjectUnderTest() {
    mapper = Mappers.getMapper(ScriptMapper.class);
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    mapper.setScmPathInfoStore(pathInfoStore);

    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDown() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldAppendSelfLink() {
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/plugins/scripts/42");
  }

  @Test
  void shouldAppendListenersLink() {
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("listeners").get().getHref()).isEqualTo("/v2/plugins/scripts/42/listeners");
  }

  @Test
  void shouldAppendHistoryLink() {
    StorableScript helloWorld = ScriptTestData.createHelloWorld();
    helloWorld.setStoreListenerExecutionResults(true);

    ScriptDto dto = mapper.map(helloWorld);
    assertThat(dto.getLinks().getLinkBy("history").get().getHref()).isEqualTo("/v2/plugins/scripts/42/history");
  }

  @Test
  void shouldAppendHistoryLinkEventWithDisabledCapturing() {
    StorableScript helloWorld = ScriptTestData.createHelloWorld();
    helloWorld.setStoreListenerExecutionResults(false);

    ScriptDto dto = mapper.map(helloWorld);
    assertThat(dto.getLinks().getLinkBy("history").get().getHref()).isEqualTo("/v2/plugins/scripts/42/history");
  }

  @Test
  void shouldAppendUpdateLink() {
    assignPermissions("script:modify");
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("update").get().getHref()).isEqualTo("/v2/plugins/scripts/42");
  }

  @Test
  void shouldAppendDeleteLink() {
    assignPermissions("script:modify");
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("delete").get().getHref()).isEqualTo("/v2/plugins/scripts/42");
  }

  @Test
  void shouldNotAppendUpdateLink() {
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("update")).isEmpty();
  }

  @Test
  void shouldNotAppendDeleteLink() {
    ScriptDto dto = mapper.map(ScriptTestData.createHelloWorld());
    assertThat(dto.getLinks().getLinkBy("delete")).isEmpty();
  }

  @Test
  void shouldNotAppendSelfLinkWithoutId() {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setId(null);
    ScriptDto dto = mapper.map(script);
    assertThat(dto.getLinks().getLinkBy("self")).isEmpty();
  }

  @Test
  void shouldCreateEmbedded() {
    List<StorableScript> scripts = Lists.newArrayList(script("42"), script("21"));
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

  @Test
  void shouldAppendEventTypeLinkToCollection() {
    HalRepresentation collection = mapper.collection(Lists.newArrayList(script("42"), script("21")));
    String eventTypes = collection.getLinks().getLinkBy("eventTypes").get().getHref();
    assertThat(eventTypes).isEqualTo("/v2/plugins/scripts/eventTypes");
  }

  @Test
  void shouldAppendCreateLinkToCollection() {
    assignPermissions("script:modify");

    HalRepresentation collection = mapper.collection(Lists.newArrayList(script("42"), script("21")));
    String self = collection.getLinks().getLinkBy("create").get().getHref();
    assertThat(self).isEqualTo("/v2/plugins/scripts");
  }

  @Test
  void shouldAppendExecuteLinkToCollection() {
    assignPermissions("script:execute");

    HalRepresentation collection = mapper.collection(Lists.newArrayList());
    String self = collection.getLinks().getLinkBy("execute").get().getHref();
    assertThat(self).isEqualTo("/v2/plugins/scripts/run");
  }

  @Test
  void shouldNotAppendCreateLinkToCollection() {
    HalRepresentation collection = mapper.collection(Lists.newArrayList(script("42"), script("21")));
    assertThat(collection.getLinks().getLinkBy("create")).isEmpty();
  }

  private StorableScript script(String id) {
    StorableScript script = ScriptTestData.createHelloWorld();
    script.setId(id);
    return script;
  }

  private void assignPermissions(String... permissions) {
    Set<String> assigned = Sets.newHashSet(permissions);
    when(subject.isPermitted(anyString())).then(ic -> assigned.contains(ic.getArgument(0)));
  }

}
