package sonia.scm.script.infrastructure;

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

  private static final String STORE_NAME = "scripts";

  private final DataStore<Script> store;

  @Inject
  public StoreScriptRepository(DataStoreFactory dataStoreFactory) {
    this.store = dataStoreFactory.withType(Script.class).withName(STORE_NAME).build();
  }

  @Override
  public Script store(Script script) {
    ScriptPermissions.checkModify();

    if (script.getId().isPresent()) {
      return modify(script);
    }
    return create(script);
  }

  private Script modify(Script script) {
    store.put(script.getId().get(), script);
    return script;
  }

  private Script create(Script script) {
    String id = store.put(script);
    script.setId(id);
    return script;
  }

  @Override
  public void remove(String id) {
    ScriptPermissions.checkModify();
    store.remove(id);
  }

  @Override
  public Optional<Script> findById(String id) {
    ScriptPermissions.checkRead();
    Script script = store.get(id);
    if (script != null) {
      script.setId(id);
      return Optional.of(script);
    }
    return Optional.empty();
  }

  @Override
  public List<Script> findAll() {
    ScriptPermissions.checkRead();
    return store.getAll()
      .entrySet()
      .stream()
      .map(e -> {
        Script script = e.getValue();
        script.setId(e.getKey());
        return script;
      })
      .collect(Collectors.toList());
  }
}
