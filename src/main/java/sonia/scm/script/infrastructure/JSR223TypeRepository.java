package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSR223TypeRepository implements TypeRepository {

  private final Map<String, String> byExtension;

  @Inject
  public JSR223TypeRepository(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    this.byExtension = collectTypes(scriptEngineManagerProvider);
  }

  private Map<String, String> collectTypes(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    Map<String, String> types = Maps.newLinkedHashMap();
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    for (ScriptEngineFactory factory : engineManager.getEngineFactories()) {
      String type = factory.getLanguageName();

      for (String extension : factory.getExtensions()) {
        types.put(extension, type);
      }
    }
    return types;
  }

  @Override
  public Optional<String> findByExtension(String extension) {
    return Optional.ofNullable(byExtension.get(extension));
  }

  @Override
  public List<String> findAll() {
    return ImmutableList.copyOf(byExtension.values()).asList();
  }
}
