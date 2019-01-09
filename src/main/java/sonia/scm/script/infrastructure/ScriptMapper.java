package sonia.scm.script.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.script.domain.Script;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
abstract class ScriptMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  abstract ScriptDto map(Script script);

  abstract Script map(ScriptDto dto);

  String field(Optional<String> optional) {
    return optional.orElse(null);
  }

  @VisibleForTesting
  void setScmPathInfoStore(ScmPathInfoStore scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @AfterMapping
  void appendLinks(Script script, @MappingTarget ScriptDto target) {
    Optional<String> id = script.getId();
    if (id.isPresent()) {
      Links.Builder linksBuilder = linkingTo().self(self(id.get()));
      target.add(linksBuilder.build());
    }
  }

  private String self(String id) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ScriptResource.class);
    return linkBuilder.method("findById").parameters(id).href();
  }

  HalRepresentation collection(List<Script> scripts) {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ScriptResource.class);
    String self = linkBuilder.method("findAll").parameters().href();
    List<ScriptDto> dtos = scripts.stream().map(this::map).collect(Collectors.toList());
    return new HalRepresentation(linkingTo().self(self).build(), Embedded.embedded("scripts", dtos));
  }
}
