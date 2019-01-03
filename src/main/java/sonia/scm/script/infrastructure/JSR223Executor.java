package sonia.scm.script.infrastructure;

import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.ScriptTypeNotFoundException;
import sonia.scm.script.domain.Type;

import javax.inject.Inject;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.List;

public class JSR223Executor implements Executor {

  private final ScriptEngineManagerProvider scriptEngineManagerProvider;

  @Inject
  public JSR223Executor(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    this.scriptEngineManagerProvider = scriptEngineManagerProvider;
  }

  @Override
  public void execute(Script script, ExecutionContext context) {
    ScriptEngine engine = findEngine(script.getType());
    SimpleScriptContext scriptContext = createScriptContext(context);
    executeScript(engine, scriptContext, script.getContent());
  }

  private void executeScript(ScriptEngine engine, SimpleScriptContext scriptContext, Content content) {
    try {
      engine.eval(content.getValue(), scriptContext);
    } catch (ScriptException e) {
      throw new ScriptExecutionException("failed to execute script", e);
    }
  }

  private SimpleScriptContext createScriptContext(ExecutionContext context) {
    SimpleScriptContext scriptContext = new SimpleScriptContext();

    scriptContext.setReader(context.getInput());
    scriptContext.setWriter(context.getOutput());
    scriptContext.setErrorWriter(context.getOutput());

    context.getAttributes().forEach((key, value) -> scriptContext.setAttribute(key, value, ScriptContext.ENGINE_SCOPE));
    return scriptContext;
  }

  private ScriptEngine findEngine(Type type) {
    ScriptEngine engine = findEngineByLanguage(type);
    if (engine == null) {
      throw new ScriptTypeNotFoundException("could not find engine for " + type.getValue());
    }
    return engine;
  }

  private ScriptEngine findEngineByLanguage(Type type) {
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    List<ScriptEngineFactory> engineFactories = engineManager.getEngineFactories();
    for (ScriptEngineFactory factory : engineFactories) {
      if (type.getValue().equals(factory.getLanguageName())) {
        return factory.getScriptEngine();
      }
    }
    return null;
  }
}
