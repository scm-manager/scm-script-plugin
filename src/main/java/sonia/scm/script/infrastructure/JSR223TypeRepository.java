package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import sonia.scm.script.domain.Type;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSR223TypeRepository implements TypeRepository {

  private final Map<String, Type> byExtension;

  @Inject
  public JSR223TypeRepository(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    this.byExtension = collectTypes(scriptEngineManagerProvider);
  }

  private Map<String, Type> collectTypes(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    Map<String, Type> byExtension = Maps.newLinkedHashMap();
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    for (ScriptEngineFactory factory : engineManager.getEngineFactories()) {
      Type type = Type.valueOf(factory.getLanguageName());

      for (String extension : factory.getExtensions()) {
        byExtension.put(extension, type);
      }
    }
    return byExtension;
  }

  @Override
  public Optional<Type> findByExtension(String extension) {
    return Optional.ofNullable(byExtension.get(extension));
  }

  @Override
  public List<Type> findAll() {
    return ImmutableList.copyOf(byExtension.values()).asList();
  }
}
