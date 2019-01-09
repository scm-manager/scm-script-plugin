package sonia.scm.script.infrastructure;

import org.mapstruct.Mapper;
import sonia.scm.script.domain.Script;

import java.util.Optional;

@Mapper
abstract class ScriptMapper {

  abstract ScriptDto map(Script script);

  abstract Script map(ScriptDto dto);

  String field(Optional<String> optional) {
    return optional.orElse(null);
  }

  Optional<String> field(String value) {
    return Optional.ofNullable(value);
  }

}
