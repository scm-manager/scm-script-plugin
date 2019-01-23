package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.Plugin;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.plugin.PluginWrapper;
import sonia.scm.plugin.ScmModule;
import sonia.scm.script.domain.EventTypeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultEventTypeRepositoryTest {

  @Mock
  private PluginLoader pluginLoader;

  @Test
  void shouldReturnAllRegisteredEvents() {
    ScmModule moduleOne = createModule(String.class, Integer.class);
    ScmModule moduleTwo = createModule(Long.class);
    List<ScmModule> modules = ImmutableList.of(moduleOne, moduleTwo);
    when(pluginLoader.getInstalledModules()).thenReturn(modules);

    PluginWrapper pluginWrapper = createPlugin(Float.class);
    List<PluginWrapper> plugins = ImmutableList.of(pluginWrapper);
    when(pluginLoader.getInstalledPlugins()).thenReturn(plugins);

    EventTypeRepository repository = new DefaultEventTypeRepository(pluginLoader);

    List<Class<?>> events = repository.findAll();
    assertThat(events).containsExactly(Float.class, Integer.class, Long.class, String.class);
  }

  private ScmModule createModule(Class<?>... eventTypes) {
    ScmModule moduleOne = mock(ScmModule.class);
    when(moduleOne.getEvents()).thenReturn(Lists.newArrayList(eventTypes));
    return moduleOne;
  }

  private PluginWrapper createPlugin(Class<?>... eventTypes) {
    PluginWrapper pluginWrapper = mock(PluginWrapper.class);
    Plugin plugin = mock(Plugin.class);
    when(plugin.getEvents()).thenReturn(Lists.newArrayList(eventTypes));
    when(pluginWrapper.getPlugin()).thenReturn(plugin);
    return pluginWrapper;
  }

}
