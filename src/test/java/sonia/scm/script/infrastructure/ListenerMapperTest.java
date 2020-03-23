/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.domain.Listener;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListenerMapperTest {

  @Mock
  private Subject subject;

  private ListenerMapper mapper;

  @BeforeEach
  void setUpObjectUnderTest() {
    mapper = Mappers.getMapper(ListenerMapper.class);
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
    ListenersDto dto = mapper.toCollection("42", ImmutableList.of(), false);

    assertThat(dto.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/plugins/scripts/42/listeners");
  }

  @Test
  void shouldNotAppendUpdateLink() {
    ListenersDto dto = mapper.toCollection("42", ImmutableList.of(), false);

    assertThat(dto.getLinks().getLinkBy("update")).isNotPresent();
  }

  @Test
  void shouldAppendUpdateLink() {
    when(subject.isPermitted("script:modify")).thenReturn(true);

    ListenersDto dto = mapper.toCollection("42", ImmutableList.of(), false);
    assertThat(dto.getLinks().getLinkBy("update").get().getHref()).isEqualTo("/v2/plugins/scripts/42/listeners");
  }

  @Test
  void shouldMapListenersToDto() {
    Listener one = new Listener(String.class, true);
    Listener two = new Listener(Integer.class, false);
    ImmutableList<Listener> listeners = ImmutableList.of(one, two);

    ListenersDto dto = mapper.toCollection("42", listeners, true);

    List<ListenerDto> dtos = dto.getListeners();
    assertThat(dto.isStoreListenerExecutionResults()).isTrue();
    assertThat(dtos).hasSize(2);

    ListenerDto dtoOne = dtos.get(0);
    assertThat(dtoOne.getEventType()).isEqualTo(String.class.getName());
    assertThat(dtoOne.isAsynchronous()).isTrue();

    ListenerDto dtoTwo = dtos.get(1);
    assertThat(dtoTwo.getEventType()).isEqualTo(Integer.class.getName());
    assertThat(dtoTwo.isAsynchronous()).isFalse();
  }

  @Test
  void shouldMapListenersFromDto() {
    ListenerDto dtoOne = new ListenerDto();
    dtoOne.setEventType(String.class.getName());
    dtoOne.setAsynchronous(true);

    ListenerDto dtoTwo = new ListenerDto();
    dtoTwo.setEventType(Integer.class.getName());
    dtoTwo.setAsynchronous(false);

    ListenersDto listenersDto = new ListenersDto(ImmutableList.of(dtoOne, dtoTwo), true);

    List<Listener> listeners = mapper.fromCollection(listenersDto);
    assertThat(listeners).hasSize(2);

    Listener one = listeners.get(0);
    assertThat(one.getEventType()).isEqualTo(String.class.getName());
    assertThat(one.isAsynchronous()).isTrue();

    Listener two = listeners.get(1);
    assertThat(two.getEventType()).isEqualTo(Integer.class.getName());
    assertThat(two.isAsynchronous()).isFalse();
  }
}
