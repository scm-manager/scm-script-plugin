package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;

import javax.script.ScriptEngineManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorableScriptEngineManagerProviderTest {

  @Mock
  private PluginLoader pluginLoader;

  @Test
  void shouldReturnScriptEngineManager() {
    when(pluginLoader.getUberClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());

    ScriptEngineManagerProvider provider = new ScriptEngineManagerProvider(pluginLoader);

    ScriptEngineManager engineManager = provider.get();
    assertThat(engineManager).isNotNull();
  }

}
