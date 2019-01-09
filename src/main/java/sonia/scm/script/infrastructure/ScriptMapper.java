package sonia.scm.script.infrastructure;

import com.google.common.base.Strings;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.Description;
import sonia.scm.script.domain.Id;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.Title;
import sonia.scm.script.domain.Type;

import java.util.ArrayList;

class ScriptMapper {

  private ScriptMapper() {
  }

  static ScriptDto map(Script script) {
    ScriptDto dto = new ScriptDto();
    script.getId().ifPresent(id -> dto.setId(id.getValue()));

    dto.setType(script.getType().getValue());
    dto.setContent(script.getContent().getValue());

    script.getTitle().ifPresent(title -> dto.setTitle(title.getValue()));
    script.getDescription().ifPresent(description -> dto.setDescription(description.getValue()));
    return dto;
  }

  static Script map(ScriptDto dto) {
    Id id = null;
    if (!Strings.isNullOrEmpty(dto.getId())) {
      id = Id.valueOf(dto.getId());
    }

    Title title = null;
    if (!Strings.isNullOrEmpty(dto.getTitle())) {
      title = Title.valueOf(dto.getTitle());
    }

    Description description = null;
    if (!Strings.isNullOrEmpty(dto.getDescription())) {
      description = Description.valueOf(dto.getDescription());
    }

    return new Script(
      id,
      Type.valueOf(dto.getType()),
      title,
      description,
      Content.valueOf(dto.getContent()),
      new ArrayList<>()
    );
  }

}
