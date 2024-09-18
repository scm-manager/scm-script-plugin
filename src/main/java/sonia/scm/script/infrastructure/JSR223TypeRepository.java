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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import sonia.scm.script.domain.TypeRepository;

import jakarta.inject.Inject;
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
