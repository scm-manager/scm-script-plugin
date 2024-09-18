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

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
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
