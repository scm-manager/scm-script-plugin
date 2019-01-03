package sonia.scm.script.domain;

import java.util.List;
import java.util.Optional;

public interface TypeRepository {

  Optional<Type> findByExtension(String extension);

  List<Type> findAll();

}
