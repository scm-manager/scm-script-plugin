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

/**
 * The {@link JSR223InternalExecutor} executes the script with the JSR 223 script engine.
 *
 * <strong>Warning:</strong> This executor does not check permissions, so use it carefully. For an execution with a
 * proper permission check use {@link JSR223Executor}.
 */
class JSR223InternalExecutor implements Executor {

  private final ScriptEngineManagerProvider scriptEngineManagerProvider;
  private final PluginLoader pluginLoader;
  private final Injector injector;

  private Clock clock;

  @Inject
  public JSR223InternalExecutor(ScriptEngineManagerProvider scriptEngineManagerProvider, PluginLoader pluginLoader, Injector injector) {
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
      throw new ScriptTypeNotFoundException(type);
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
