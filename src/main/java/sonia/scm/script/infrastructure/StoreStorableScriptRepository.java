/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
