package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class StoreStorableScriptRepository implements StorableScriptRepository {

  private static final String STORE_NAME = "scripts";

  private final DataStore<StorableScript> store;

  @Inject
  public StoreStorableScriptRepository(DataStoreFactory dataStoreFactory) {
    this.store = dataStoreFactory.withType(StorableScript.class).withName(STORE_NAME).build();
  }

  @Override
  public StorableScript store(StorableScript script) {
    ScriptPermissions.checkModify();

    if (script.getId().isPresent()) {
      return modify(script);
    }
    return create(script);
  }

  private StorableScript modify(StorableScript script) {
    store.put(script.getId().get(), script);
    return script;
  }

  private StorableScript create(StorableScript script) {
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
  public Optional<StorableScript> findById(String id) {
    ScriptPermissions.checkRead();
    StorableScript script = store.get(id);
    if (script != null) {
      script.setId(id);
      return Optional.of(script);
    }
    return Optional.empty();
  }

  @Override
  public List<StorableScript> findAll() {
    ScriptPermissions.checkRead();
    return store.getAll()
      .entrySet()
      .stream()
      .map(e -> {
        StorableScript script = e.getValue();
        script.setId(e.getKey());
        return script;
      })
      .collect(Collectors.toList());
  }
}
