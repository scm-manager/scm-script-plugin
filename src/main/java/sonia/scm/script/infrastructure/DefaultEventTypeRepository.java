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

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.checkerframework.checker.nullness.qual.Nullable;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.plugin.ScmModule;
import sonia.scm.script.domain.EventTypeRepository;

import javax.inject.Inject;
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
  public List<Class<?>> findAll() {
    Set<Class<?>> events = new HashSet<>();

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

  private void appendEvents(Set<Class<?>> events, ScmModule module) {
    Iterable<Class<?>> moduleEvents = module.getEvents();
    if (moduleEvents != null) {
      Iterables.addAll(events, moduleEvents);
    }
  }

  private final Ordering<Class<?>> classOrdering = new Ordering<Class<?>>() {
    @Override
    public int compare(@Nullable Class<?> left, @Nullable Class<?> right) {
      if (left == null) {
        return (right == null) ? 0 : -1;
      }
      if (right == null) {
        return 1;
      }
      return left.getName().compareTo(right.getName());
    }
  };

}
