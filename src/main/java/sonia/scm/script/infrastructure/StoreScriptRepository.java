package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import sonia.scm.script.domain.Id;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptRepository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class StoreScriptRepository implements ScriptRepository {

  private static final String STORE = "scripts";

  private final DataStore<Script> store;

  @Inject
  public StoreScriptRepository(DataStoreFactory dataStoreFactory) {
    this.store = dataStoreFactory.withType(Script.class).withName(STORE).build();
  }

  @Override
  public Script store(Script script) {
    if (script.getId().isPresent()) {
      return modify(script);
    }
    return create(script);
  }

  private Script modify(Script script) {
    store.put(script.getId().get().getValue(), script);
    return script;
  }

  private Script create(Script script) {
    String idValue = store.put(script);

    return new Script(
      Id.valueOf(idValue),
      script.getType(),
      script.getDescription(),
      script.getContent(),
      script.getListeners()
    );
  }

  @Override
  public void remove(Id id) {
    store.remove(id.getValue());
  }

  @Override
  public Optional<Script> findById(Id id) {
    return Optional.ofNullable(store.get(id.getValue()));
  }

  @Override
  public List<Script> findAll() {
    return ImmutableList.copyOf(store.getAll().values());
  }
}
