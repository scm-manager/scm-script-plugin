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

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.script.ScriptContent;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptMetadata;
import sonia.scm.script.ScriptUtil;
import sonia.scm.script.ScriptWrapperException;

//~--- JDK imports ------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
   * @param manager
   */
  @Inject
  public ScriptResource(ScriptManager manager)
  {
    this.manager = manager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param metadata
   *
   * @throws IOException
   */
  @POST
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public void addScript(ScriptMetadata metadata) throws IOException
  {
    manager.add(metadata, new ByteArrayInputStream(new byte[0]));
  }

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
  public Response execute(@Context HttpServletRequest request,
    InputStream content)
    throws IOException
  {
    Response response;

    try
    {
      String result = ScriptUtil.execute(manager, getContentType(request),
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

  /**
   *   Method description
   *
   *
   *
   * @param id
   *   @return
   */
  @DELETE
  @Path("{id}")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public void remove(@PathParam("id") String id)
  {
    manager.remove(id);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("metadata")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Scripts getStoreScripts()
  {
    return new Scripts(manager.getAll());
  }

  /**
   * Method description
   *
   *
   * @param name
   *
   * @param id
   *
   * @return
   */
  @GET
  @Path("metadata/{id}")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public ScriptMetadata getStoredScript(@PathParam("id") String id)
  {
    return manager.get(id);
  }

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   *
   * @throws IOException
   */
  @GET
  @Path("content/{id}")
  public Response getStoredScriptContent(@PathParam("id") String id)
    throws IOException
  {
    Response response = null;
    ScriptContent content = manager.getScriptContent(id);

    if (content != null)
    {
      response = Response.ok(content.getContent(),
        content.getMimetype()).build();
    }
    else
    {
      response = Response.status(Status.NOT_FOUND).build();
    }

    return response;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("types")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public ScriptTypes getSupportedTypes()
  {
    return new ScriptTypes(manager.getSupportedTypes());
  }

  /**
   * Method description
   *
   *
   * @param request
   *
   * @return
   */
  private String getContentType(HttpServletRequest request)
  {
    String contentType = request.getContentType();

    if (!Strings.isNullOrEmpty(contentType))
    {
      int index = contentType.indexOf(";");

      if (index > 0)
      {
        contentType = contentType.substring(0, index);
      }
    }

    return contentType;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ScriptManager manager;
}
