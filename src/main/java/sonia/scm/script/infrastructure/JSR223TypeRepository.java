package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.Type;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.stream.Collectors;

public class JSR223TypeRepository implements TypeRepository {

  private final List<Type> types;

  @Inject
  public JSR223TypeRepository(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    this.types = collectTypes(scriptEngineManagerProvider);
  }

  private final List<Type> collectTypes(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    return engineManager.getEngineFactories()
      .stream()
      .map(factory -> factory.getLanguageName())
      .map(Type::valueOf)
      .collect(Collectors.toList());
  }

  @Override
  public List<Type> findAll() {
    return types;
  }
}
