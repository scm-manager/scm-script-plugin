package sonia.scm.script.domain;

import java.util.List;
import java.util.Optional;

public interface ScriptRepository {

  Script store(Script script);

  void remove(Id id);

  Optional<Script> findById(Id id);

  List<Script> findAll();

}
