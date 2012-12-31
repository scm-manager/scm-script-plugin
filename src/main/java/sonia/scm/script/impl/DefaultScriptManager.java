/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.script.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import sonia.scm.script.EnvironmentBuilder;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptType;
import sonia.scm.script.ScriptWrapperException;
import sonia.scm.security.Role;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 *
 * @author Sebastian Sdorra
 */
public class DefaultScriptManager implements ScriptManager
{

  /**
   * Constructs ...
   *
   *
   * @param environmentBuilder
   */
  @Inject
  public DefaultScriptManager(EnvironmentBuilder environmentBuilder)
  {
    this.environmentBuilder = environmentBuilder;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param mimeType
   * @param reader
   * @param writer
   *
   * @throws IOException
   * @throws ScriptWrapperException
   */
  @Override
  public void execute(String mimeType, Reader reader, Writer writer)
    throws IOException, ScriptWrapperException
  {
    Subject subject = SecurityUtils.getSubject();

    subject.checkRole(Role.ADMIN);

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByMimeType(mimeType);

    if (engine == null)
    {
      throw new IOException(
        "no script engine available, for mimetype ".concat(mimeType));
    }

    SimpleScriptContext context = new SimpleScriptContext();

    // context.setReader(reader);
    context.setWriter(writer);
    context.setErrorWriter(writer);

    Map<String, Object> env = environmentBuilder.createEnvironment();

    for (Entry<String, Object> e : env.entrySet())
    {
      context.setAttribute(e.getKey(), e.getValue(),
        ScriptContext.ENGINE_SCOPE);
    }

    try
    {
      engine.eval(reader, context);
    }
    catch (ScriptException ex)
    {
      throw new ScriptWrapperException(ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Set<ScriptType> getSupportedTypes()
  {
    if (supportedTypes == null)
    {
      Builder<ScriptType> builder = ImmutableSet.builder();

      ScriptEngineManager manager = new ScriptEngineManager();
      List<ScriptEngineFactory> factories = manager.getEngineFactories();

      for (ScriptEngineFactory factory : factories)
      {
        builder.add(new ScriptType(factory.getLanguageName(),
          factory.getEngineName(), factory.getMimeTypes()));
      }

      supportedTypes = builder.build();
    }

    return supportedTypes;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private EnvironmentBuilder environmentBuilder;

  /** Field description */
  private Set<ScriptType> supportedTypes;
}
