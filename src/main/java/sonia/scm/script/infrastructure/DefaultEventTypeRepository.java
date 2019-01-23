package sonia.scm.script.infrastructure;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.checkerframework.checker.nullness.qual.Nullable;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.plugin.PluginWrapper;
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

    Collection<PluginWrapper> installedPlugins = pluginLoader.getInstalledPlugins();
    for (PluginWrapper installedPlugin : installedPlugins) {
      appendEvents(events, installedPlugin.getPlugin());
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
