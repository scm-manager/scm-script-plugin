package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.Id;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptRepository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class StoreScriptRepository implements ScriptRepository {

  private static final String STORE = "scripts";

  private final DataStore<ScriptDto> store;

  @Inject
  public StoreScriptRepository(DataStoreFactory dataStoreFactory) {
    this.store = dataStoreFactory.withType(ScriptDto.class).withName(STORE).build();
  }

  @Override
  public Script store(Script script) {
    if (script.getId().isPresent()) {
      return modify(script);
    }
    return create(script);
  }

  private Script modify(Script script) {
    store.put(script.getId().get().getValue(), ScriptMapper.map(script));
    return script;
  }

  private Script create(Script script) {
    String idValue = store.put(ScriptMapper.map(script));
    Script storedScript = new Script(
      Id.valueOf(idValue),
      script.getType(),
      script.getTitle().orElse(null),
      script.getDescription().orElse(null),
      script.getContent(),
      script.getListeners()
    );
    return modify(storedScript);
  }

  @Override
  public void remove(Id id) {
    store.remove(id.getValue());
  }

  @Override
  public Optional<Script> findById(Id id) {
    ScriptDto dto = store.get(id.getValue());
    if (dto != null) {
      return Optional.of(ScriptMapper.map(dto));
    }
    return Optional.empty();
  }

  @Override
  public List<Script> findAll() {
    return store.getAll()
      .values()
      .stream()
      .map(ScriptMapper::map)
      .collect(Collectors.toList());
  }
}
