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

import com.google.common.annotations.VisibleForTesting;
import sonia.scm.plugin.PluginLoader;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javax.script.ScriptEngineManager;

public class ScriptEngineManagerProvider implements Provider<ScriptEngineManager> {

  private final ClassLoader classLoader;

  @Inject
  public ScriptEngineManagerProvider(PluginLoader pluginLoader) {
    this(pluginLoader.getUberClassLoader());
  }

  ScriptEngineManagerProvider(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @VisibleForTesting
  static ScriptEngineManagerProvider context() {
    return new ScriptEngineManagerProvider(Thread.currentThread().getContextClassLoader());
  }

  @Override
  public ScriptEngineManager get() {
    return new ScriptEngineManager(classLoader);
  }
}
