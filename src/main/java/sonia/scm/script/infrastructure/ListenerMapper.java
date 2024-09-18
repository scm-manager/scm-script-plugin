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

import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.domain.Listener;

import jakarta.inject.Inject;
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
