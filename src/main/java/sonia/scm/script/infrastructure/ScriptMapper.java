package sonia.scm.script.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.domain.StorableScript;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class ScriptMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @Mappings(
    @Mapping(target = "attributes", ignore = true)
  )
  abstract ScriptDto map(StorableScript script);

  @Mappings({
    @Mapping(target = "listeners", ignore = true),
    @Mapping(target = "executionHistory", ignore = true),
    @Mapping(target = "storeListenerExecutionResults", ignore = true)
  })
  abstract StorableScript map(ScriptDto dto);

  String field(Optional<String> optional) {
    return optional.orElse(null);
  }

  @VisibleForTesting
  void setScmPathInfoStore(ScmPathInfoStore scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @AfterMapping
  void appendLinks(StorableScript script, @MappingTarget ScriptDto target) {
    Optional<String> id = script.getId();
    if (id.isPresent()) {
      LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ScriptResource.class);

      Links.Builder builder = linkingTo();
      builder.self(linkBuilder.method("findById").parameters(id.get()).href());
      builder.single(Link.link("listeners", linkBuilder.method("getListeners").parameters(id.get()).href()));

      if (script.isStoreListenerExecutionResults()) {
        builder.single(Link.link("history", linkBuilder.method("getHistory").parameters(id.get()).href()));
      }

      if (ScriptPermissions.isPermittedToModify()) {
        builder.single(Link.link("update", linkBuilder.method("modify").parameters(id.get()).href()));
        builder.single(Link.link("delete", linkBuilder.method("delete").parameters(id.get()).href()));
      }

      target.add(builder.build());
    }
  }

  HalRepresentation collection(List<StorableScript> scripts) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ScriptResource.class);

    Links.Builder builder = linkingTo();

    builder.self(linkBuilder.method("findAll").parameters().href());
    builder.single(Link.link("eventTypes", linkBuilder.method("findAllEventTypes").parameters().href()));

    if (ScriptPermissions.isPermittedToModify()) {
      builder.single(Link.link("create", linkBuilder.method("create").parameters().href()));
    }

    if (ScriptPermissions.isPermittedToExecute()) {
      builder.single(Link.link("execute", linkBuilder.method("run").parameters().href()));
    }

    List<ScriptDto> dtos = scripts.stream().map(this::map).collect(Collectors.toList());
    return new HalRepresentation(builder.build(), Embedded.embedded("scripts", dtos));
  }
}
