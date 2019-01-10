package sonia.scm.script.domain;

import java.util.List;
import java.util.Optional;

public interface StorableScriptRepository {

  StorableScript store(StorableScript script);

  void remove(String id);

  Optional<StorableScript> findById(String id);

  List<StorableScript> findAll();

}
