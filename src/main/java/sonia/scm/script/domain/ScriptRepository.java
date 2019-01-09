package sonia.scm.script.domain;

import java.util.List;
import java.util.Optional;

public interface ScriptRepository {

  Script store(Script script);

  void remove(String id);

  Optional<Script> findById(String id);

  List<Script> findAll();

}
