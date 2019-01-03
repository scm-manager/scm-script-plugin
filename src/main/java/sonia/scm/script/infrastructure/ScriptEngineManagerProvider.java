package sonia.scm.script.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import sonia.scm.plugin.PluginLoader;

import javax.inject.Inject;
import javax.inject.Provider;
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
