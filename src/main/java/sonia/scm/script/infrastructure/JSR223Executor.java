package sonia.scm.script.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.inject.Injector;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptTypeNotFoundException;

import javax.inject.Inject;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.StringWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class JSR223Executor implements Executor {

  private final ScriptEngineManagerProvider scriptEngineManagerProvider;
  private final PluginLoader pluginLoader;
  private final Injector injector;

  private Clock clock;

  @Inject
  public JSR223Executor(ScriptEngineManagerProvider scriptEngineManagerProvider, PluginLoader pluginLoader, Injector injector) {
    this.scriptEngineManagerProvider = scriptEngineManagerProvider;
    this.pluginLoader = pluginLoader;
    this.injector = injector;

    this.setClock(Clock.systemUTC());
  }

  @VisibleForTesting
  void setClock(Clock clock) {
    this.clock = clock;
  }

  @Override
  public ExecutionResult execute(Script script, ExecutionContext context) {
    ScriptPermissions.checkExecute();

    ScriptEngine engine = findEngine(script.getType());
    SimpleScriptContext scriptContext = createScriptContext(context);

    return executeScript(engine, scriptContext, script.getContent());
  }

  private ExecutionResult executeScript(ScriptEngine engine, SimpleScriptContext scriptContext, String content) {
    StringWriter writer = new StringWriter();
    applyWriter(scriptContext, writer);

    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    Instant started = now();
    try {
      Thread.currentThread().setContextClassLoader(pluginLoader.getUberClassLoader());
      engine.eval(content, scriptContext);
      return success(started, writer);
    } catch (ScriptException e) {
      return failed(started, writer, e);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  private ExecutionResult success(Instant started, StringWriter writer) {
    return createResult(true, started, writer);
  }

  private ExecutionResult failed(Instant started, StringWriter writer, ScriptException e) {
    appendException(writer, e);
    return createResult(false, started, writer);
  }

  private ExecutionResult createResult(boolean success, Instant started, StringWriter writer) {
    return new ExecutionResult(success, createOutput(writer), started, now());
  }

  private String createOutput(StringWriter writer) {
    return writer.toString().trim();
  }

  private void appendException(StringWriter writer, ScriptException e) {
    String stackTraceAsString = Throwables.getStackTraceAsString(e);
    writer.append("\n\n").append(stackTraceAsString);
  }

  private Instant now() {
    return Instant.now(clock);
  }

  private void applyWriter(SimpleScriptContext scriptContext, StringWriter writer) {
    scriptContext.setWriter(writer);
    scriptContext.setErrorWriter(writer);
  }

  private SimpleScriptContext createScriptContext(ExecutionContext context) {
    SimpleScriptContext scriptContext = new SimpleScriptContext();
    scriptContext.setAttribute("injector", injector, ScriptContext.ENGINE_SCOPE);
    context.getAttributes().forEach((key, value) -> scriptContext.setAttribute(key, value, ScriptContext.ENGINE_SCOPE));
    return scriptContext;
  }

  private ScriptEngine findEngine(String type) {
    ScriptEngine engine = findEngineByLanguage(type);
    if (engine == null) {
      throw new ScriptTypeNotFoundException("could not find engine for " + type);
    }
    return engine;
  }

  private ScriptEngine findEngineByLanguage(String type) {
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    List<ScriptEngineFactory> engineFactories = engineManager.getEngineFactories();
    for (ScriptEngineFactory factory : engineFactories) {
      if (factory.getLanguageName().equals(type)) {
        return factory.getScriptEngine();
      }
    }
    return null;
  }
}
