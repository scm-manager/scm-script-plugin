/**
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

import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.domain.Listener;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class ListenerMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @VisibleForTesting
  void setScmPathInfoStore(ScmPathInfoStore pathInfoStore) {
    this.scmPathInfoStore = pathInfoStore;
  }

  @Mapping(target = "attributes", ignore = true) // We do not map HAL attributes
  abstract ListenerDto map(Listener listener);

  abstract Listener map(ListenerDto dto);

  List<Listener> fromCollection(ListenersDto collectionDto) {
    return collectionDto.getListeners().stream().map(this::map).collect(Collectors.toList());
  }

  ListenersDto toCollection(String scriptId, List<Listener> listeners, boolean storeListenerExecutionResults) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ScriptResource.class);

    Links.Builder builder = linkingTo();

    builder.self(linkBuilder.method("getListeners").parameters(scriptId).href());
    if (ScriptPermissions.isPermittedToModify()) {
      builder.single(Link.link("update", linkBuilder.method("setListeners").parameters(scriptId).href()));
    }

    List<ListenerDto> dtos = listeners.stream().map(this::map).collect(Collectors.toList());
    ListenersDto collectionDto = new ListenersDto(dtos, storeListenerExecutionResults);
    collectionDto.add(builder.build());
    return collectionDto;
  }
}
