import sonia.scm.plugin.*

PluginManager pluginManager = injector.getInstance(PluginManager.class)
for (PluginInformation plugin : pluginManager.getInstalled()) {
  println "${plugin.name} - ${plugin.version}"
}
