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

import com.google.common.collect.Ordering;
import org.checkerframework.checker.nullness.qual.Nullable;
import sonia.scm.plugin.ClassElement;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.plugin.ScmModule;
import sonia.scm.script.domain.EventTypeRepository;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultEventTypeRepository implements EventTypeRepository {

  private final PluginLoader pluginLoader;

  @Inject
  public DefaultEventTypeRepository(PluginLoader pluginLoader) {
    this.pluginLoader = pluginLoader;
  }

  @Override
  public List<String> findAll() {
    Set<String> events = new HashSet<>();

    Collection<ScmModule> modules = pluginLoader.getInstalledModules();
    for (ScmModule module : modules) {
      appendEvents(events, module);
    }

    Collection<InstalledPlugin> installedPlugins = pluginLoader.getInstalledPlugins();
    for (InstalledPlugin installedPlugin : installedPlugins) {
      appendEvents(events, installedPlugin.getDescriptor());
    }

    return classOrdering.sortedCopy(events);
  }

  private void appendEvents(Set<String> events, ScmModule module) {
    Iterable<ClassElement> moduleEvents = module.getEvents();
    if (moduleEvents != null) {
      moduleEvents.forEach(e -> events.add(e.getClazz()));
    }
  }

  private final Ordering<String> classOrdering = new Ordering<String>() {
    @Override
    public int compare(@Nullable String left, @Nullable String right) {
      if (left == null) {
        return (right == null) ? 0 : -1;
      }
      if (right == null) {
        return 1;
      }
      return left.compareTo(right);
    }
  };

}
