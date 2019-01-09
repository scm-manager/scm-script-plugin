package sonia.scm.script.domain;

import java.util.List;
import java.util.Optional;

public interface TypeRepository {

  Optional<String> findByExtension(String extension);

  List<String> findAll();

}
