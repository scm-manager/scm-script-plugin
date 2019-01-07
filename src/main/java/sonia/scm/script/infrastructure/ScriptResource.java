package sonia.scm.script.infrastructure;

import com.google.common.base.Throwables;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.Type;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

@Path("v2/plugins/scripts")
public class ScriptResource {

  private Executor executor;

  @Inject
  public ScriptResource(Executor executor) {
    this.executor = executor;
  }

  @POST
  @Path("run")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  public void run(@Context HttpServletResponse response, @QueryParam("lang") Type type, Content content) throws IOException {
    Script script = new Script(type, content);

    try (PrintWriter writer = response.getWriter()) {
      execute(writer, script);
    }
  }

  private void execute(Writer writer, Script script) throws IOException {
    try {
      executor.execute(
        script,
        ExecutionContext.builder()
          .withOutput(writer)
          .build()
      );
    } catch (ScriptExecutionException ex) {
      Throwable cause = ex.getCause();
      if (cause == null) {
        cause = ex;
      }
      writer.append(Throwables.getStackTraceAsString(cause));
    }
    catch (Throwable ex) {
      writer.append(Throwables.getStackTraceAsString(ex));
    }
  }


}
