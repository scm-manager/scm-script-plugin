/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
package sonia.scm.script.impl;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.Extension;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptUtil;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import javax.inject.Inject;
import javax.script.ScriptException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
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
public class InitScriptContextListener implements ServletContextListener
{

  private static final Logger logger = LoggerFactory.getLogger(InitScriptContextListener.class);

  private final ScriptManager manager;
  private final AdministrationContext administrationContext;

  @Inject
  public InitScriptContextListener(ScriptManager manager, AdministrationContext administrationContext)
  {
    this.manager = manager;
    this.administrationContext = administrationContext;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    InitScriptCollector collector = new InitScriptCollector(manager);
    List<InitScript> initScripts = collector.collect();

    if (!initScripts.isEmpty()) {
      administrationContext.runAsAdmin(new InitScriptExecutor(manager, initScripts));
    }  else  {
      logger.debug("no init scripts to execute");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {
    // do nothing
  }

  private static class InitScriptExecutor implements PrivilegedAction
  {

    private final ScriptManager manager;
    private final List<InitScript> scripts;

    private InitScriptExecutor(ScriptManager manager, List<InitScript> scripts)
    {
      this.manager = manager;
      this.scripts = scripts;
    }

    @Override
    public void run()
    {
      try
      {
        for (InitScript script : scripts)
        {
          logger.info("executing script {} with engine {}", script.getFile(), script.getType().getName());
          String output = ScriptUtil.execute(manager, script.getType().getFirstMimeType(), script.getFile());
          logger.info("script output: {}", output);
        }
      } 
      catch (IOException | ScriptException ex)
      {
        throw Throwables.propagate(ex);
      }
    }

  }
}
