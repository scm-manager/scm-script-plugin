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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.ClassElement;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.InstalledPluginDescriptor;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.plugin.ScmModule;
import sonia.scm.script.domain.EventTypeRepository;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultEventTypeRepositoryTest {

  @Mock
  private PluginLoader pluginLoader;

  @Test
  void shouldReturnAllRegisteredEvents() {
    ScmModule moduleOne = createModule(String.class.getName(), Integer.class.getName());
    ScmModule moduleTwo = createModule(Long.class.getName());
    List<ScmModule> modules = ImmutableList.of(moduleOne, moduleTwo);
    when(pluginLoader.getInstalledModules()).thenReturn(modules);

    InstalledPlugin pluginWrapper = createPlugin(Float.class.getName());
    List<InstalledPlugin> plugins = ImmutableList.of(pluginWrapper);
    when(pluginLoader.getInstalledPlugins()).thenReturn(plugins);

    EventTypeRepository repository = new DefaultEventTypeRepository(pluginLoader);

    List<String> events = repository.findAll();
    assertThat(events).containsExactly(Float.class.getName(), Integer.class.getName(), Long.class.getName(), String.class.getName());
  }

  private ScmModule createModule(String... eventTypes) {
    ScmModule moduleOne = mock(ScmModule.class);
    when(moduleOne.getEvents()).thenReturn(toClassElements(eventTypes));
    return moduleOne;
  }

  private InstalledPlugin createPlugin(String... eventTypes) {
    InstalledPlugin plugin = mock(InstalledPlugin.class);
    InstalledPluginDescriptor descriptor = mock(InstalledPluginDescriptor.class);
    when(descriptor.getEvents()).thenReturn(toClassElements(eventTypes));
    when(plugin.getDescriptor()).thenReturn(descriptor);
    return plugin;
  }

  private Iterable<ClassElement> toClassElements(String... eventTypes) {
    return stream(eventTypes)
      .map(s -> new ClassElement(s, null, Collections.emptySet()))
      .collect(toList());
  }
}
