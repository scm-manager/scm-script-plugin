/*
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.InstalledPluginDescriptor;
import sonia.scm.plugin.PluginLoader;
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

    InstalledPlugin pluginWrapper = createPlugin(Float.class);
    List<InstalledPlugin> plugins = ImmutableList.of(pluginWrapper);
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

  private InstalledPlugin createPlugin(Class<?>... eventTypes) {
    InstalledPlugin plugin = mock(InstalledPlugin.class);
    InstalledPluginDescriptor descriptor = mock(InstalledPluginDescriptor.class);
    when(descriptor.getEvents()).thenReturn(Lists.newArrayList(eventTypes));
    when(plugin.getDescriptor()).thenReturn(descriptor);
    return plugin;
  }

}
