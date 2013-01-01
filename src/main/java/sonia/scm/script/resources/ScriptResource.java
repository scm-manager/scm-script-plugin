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


package sonia.scm.script.resources;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Throwables;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptTypes;
import sonia.scm.script.ScriptWrapperException;
import sonia.scm.script.Scripts;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Sebastian Sdorra
 */
@Path("plugins/script")
public class ScriptResource
{

  /**
   * the logger for ConsoleResource
   */
  private static final Logger logger =
    LoggerFactory.getLogger(ScriptResource.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param executor
   */
  @Inject
  public ScriptResource(ScriptManager executor)
  {
    this.executor = executor;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param request
   * @param content
   *
   * @return
   *
   * @throws IOException
   */
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.WILDCARD)
  public Response execute(@Context HttpServletRequest request, String content)
    throws IOException
  {
    Response response;

    try
    {
      String result = Scripts.execute(executor, request.getContentType(),
                        content);

      if (logger.isTraceEnabled())
      {
        logger.trace("script results: {}", result);
      }

      response = Response.ok(result).build();
    }
    catch (Exception ex)
    {
      Throwable throwable = ex;

      if (ex instanceof ScriptWrapperException)
      {
        throwable = ex.getCause();
      }

      String result = Throwables.getStackTraceAsString(throwable);

      if (logger.isTraceEnabled())
      {
        logger.trace("script fails with result: {}", result);
      }

      response = Response.serverError().entity(result).build();
    }

    return response;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("supported-types")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON })
  public ScriptTypes getSupportedTypes()
  {
    return new ScriptTypes(executor.getSupportedTypes());
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ScriptManager executor;
}
