package sonia.scm.script.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.InitScript;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.StringWriter;
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

  @Inject
  public InitScriptContextListener(AdministrationContext administrationContext, InitScriptCollector collector, Executor executor) {
    this.administrationContext = administrationContext;
    this.collector = collector;
    this.executor = executor;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    List<InitScript> scripts = collector.collect();
    administrationContext.runAsAdmin(executeScripts(scripts));
  }

  private PrivilegedAction executeScripts(List<InitScript> scripts) {
    return () -> scripts.forEach(this::executeScript);
  }

  private void executeScript(InitScript script) {
    StringWriter writer = new StringWriter();

    ExecutionContext context = ExecutionContext.builder()
      .withOutput(writer)
      .build();

    try {
      executor.execute(script, context);
      LOG.debug("{}: {}", script, writer);
    } catch (ScriptExecutionException ex) {
      LOG.error("failed to execute script", ex);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // no action required
  }
}
