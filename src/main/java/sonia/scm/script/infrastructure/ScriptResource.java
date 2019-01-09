package sonia.scm.script.infrastructure;

import com.google.common.base.Throwables;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Id;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.ScriptRepository;
import sonia.scm.script.domain.Type;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("v2/plugins/scripts")
public class ScriptResource {

  private final ScriptRepository repository;
  private final Executor executor;


  @Inject
  public ScriptResource(ScriptRepository repository, Executor executor) {
    this.repository = repository;
    this.executor = executor;
  }

  @GET
  @Path("{id}")
  @Produces(ScriptMediaType.ONE)
  public Response findById(@PathParam("id") Id id) {
    Optional<Script> byId = repository.findById(id);
    if (byId.isPresent()) {
      return Response.ok(ScriptMapper.map(byId.get())).build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @GET
  @Produces(ScriptMediaType.COLLECTION)
  public Response findAll() {
    List<ScriptDto> dtos = repository.findAll().stream().map(ScriptMapper::map).collect(Collectors.toList());
    return Response.ok(dtos).build();
  }

  @POST
  @Consumes(ScriptMediaType.ONE)
  public Response store(ScriptDto dto) {
    Script storedScript = repository.store(ScriptMapper.map(dto));
    // TODO should be created with location header
    return Response.ok().build();
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
