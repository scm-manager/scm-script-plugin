package sonia.scm.script.domain;

import java.util.List;

public interface EventTypeRepository {

  List<Class<?>> findAll();

}
