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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.event.ScmEventBus;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * The InitScriptContextListener executes init scripts from the init.script.d directory of scm-manager. The directory is
 * located in the home of scm-manager, but it can be changed by using the system property sonia.scm.init.script.d. The
 * scripts are executed with admin privileges. Each script can be written in any installed language which is supported
 * by JSR-223.
 *
 * @author Sebastian Sdorra
 */
@Extension
public class InitScriptContextListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(InitScriptContextListener.class);

  private final AdministrationContext administrationContext;
  private final InitScriptCollector collector;
  private final Executor executor;
  private final ScmEventBus eventBus;

  @Inject
  public InitScriptContextListener(AdministrationContext administrationContext, InitScriptCollector collector, Executor executor, ScmEventBus eventBus) {
    this.administrationContext = administrationContext;
    this.collector = collector;
    this.executor = executor;
    this.eventBus = eventBus;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    List<InitScript> scripts = collector.collect();
    administrationContext.runAsAdmin(executeScripts(scripts));
  }

  private PrivilegedAction executeScripts(List<InitScript> scripts) {
    return () -> {
      scripts.forEach(this::executeScript);
      // Fire started event.
      // We have to do this in a PrivilegedAction,
      // because we need a shiro context which is not available in a ServletContextListener without PrivilegedAction.
      eventBus.post(new ScmStartedEvent());
    };
  }

  private void executeScript(InitScript script) {
    try {
      ExecutionResult result = executor.execute(script, ExecutionContext.empty());
      LOG.debug("{}: {}", script, result);
    } catch (ScriptExecutionException ex) {
      LOG.error("failed to execute script", ex);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // no action required
  }
}
