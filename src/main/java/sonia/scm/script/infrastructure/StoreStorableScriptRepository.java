/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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

    Optional<String> id = script.getId();
    return id.map(s -> modify(s, script)).orElseGet(() -> create(script));
  }

  private StorableScript modify(String id, StorableScript script) {
    store.put(id, script);
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
