package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.ScriptTestData;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptExecutionException;
import sonia.scm.script.domain.ScriptRepository;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptResourceTest {

  @Mock
  private ScriptRepository repository;

  @Mock
  private ScriptMapper mapper;

  @Mock
  private Executor executor;

  @InjectMocks
  private ScriptResource resource;

  @Test
  void shouldReturnLocationLinkAfterCreation() {
    ScriptDto dto = new ScriptDto();
    Script script = ScriptTestData.createHelloWorld();

    when(mapper.map(dto)).thenReturn(script);
    when(repository.store(script)).thenReturn(script);

    UriInfo info = mock(UriInfo.class);
    UriBuilder builder = mock(UriBuilder.class);
    when(info.getRequestUriBuilder()).thenReturn(builder);
    when(builder.path("42")).thenReturn(builder);

    URI location = URI.create("/v2/plugins/42");
    when(builder.build()).thenReturn(location);

    Response response = resource.create(info, dto);
    assertThat(response.getStatus()).isEqualTo(201);
    assertThat(response.getLocation()).isEqualTo(location);
  }

  @Test
  void shouldReturnCollection() {
    List<Script> scripts = Lists.newArrayList(ScriptTestData.createHelloWorld());
    when(repository.findAll()).thenReturn(scripts);

    HalRepresentation collection = new HalRepresentation();
    when(mapper.collection(scripts)).thenReturn(collection);

    Response response = resource.findAll();
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getEntity()).isSameAs(collection);
  }

  @Test
  void shouldFindById() {
    ScriptDto dto = new ScriptDto();
    Script script = ScriptTestData.createHelloWorld();

    when(repository.findById("42")).thenReturn(Optional.of(script));
    when(mapper.map(script)).thenReturn(dto);

    Response response = resource.findById("42");
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getEntity()).isSameAs(dto);
  }

  @Test
  void shouldReturnNotFound() {
    when(repository.findById("42")).thenReturn(Optional.empty());

    Response response = resource.findById("42");
    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  void shouldModify() {
    ScriptDto dto = new ScriptDto();
    dto.setId("42");
    Script script = ScriptTestData.createHelloWorld();

    when(mapper.map(dto)).thenReturn(script);

    Response response = resource.modify("42", dto);
    assertThat(response.getStatus()).isEqualTo(204);

    verify(repository).store(script);
  }

  @Test
  void shouldReturnBadRequest() {
    ScriptDto dto = new ScriptDto();
    dto.setId("21");

    Response response = resource.modify("42", dto);
    assertThat(response.getStatus()).isEqualTo(400);
  }

  @Test
  void shouldRemove() {
    Response response = resource.delete("42");
    assertThat(response.getStatus()).isEqualTo(204);

    verify(repository).remove("42");
  }

  @Test
  void shouldExecuteTheScript() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter writer = new PrintWriter(new StringWriter());
    when(response.getWriter()).thenReturn(writer);

    resource.run(response, "Groovy", "println 'Hello World';");

    ArgumentCaptor<Script> scriptCaptor = ArgumentCaptor.forClass(Script.class);
    ArgumentCaptor<ExecutionContext> contextCaptor = ArgumentCaptor.forClass(ExecutionContext.class);

    verify(executor).execute(scriptCaptor.capture(), contextCaptor.capture());

    Script script = scriptCaptor.getValue();
    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getContent()).isEqualTo("println 'Hello World';");

    ExecutionContext context = contextCaptor.getValue();
    assertThat(context.getOutput()).isSameAs(writer);
  }

  @Test
  void shouldCatchAndUnwrapScriptException() throws IOException {
    ScriptExecutionException ex = new ScriptExecutionException("wrapped", new IOException("damn"));

    doThrow(ex).when(executor).execute(any(Script.class), any(ExecutionContext.class));

    String resource = execute();
    assertThat(resource).startsWith(IOException.class.getName());
  }

  @Test
  void shouldCatchScriptException() throws IOException {
    ScriptExecutionException ex = new ScriptExecutionException("wrapped");

    doThrow(ex).when(executor).execute(any(Script.class), any(ExecutionContext.class));

    String resource = execute();
    assertThat(resource).startsWith(ScriptExecutionException.class.getName());
  }

  @Test
  void shouldCatch() throws IOException {
    RuntimeException ex = new RuntimeException("uncatchable");

    doThrow(ex).when(executor).execute(any(Script.class), any(ExecutionContext.class));

    String resource = execute();
    assertThat(resource).startsWith(RuntimeException.class.getName());
  }

  private String execute() throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    resource.run(response, "Groovy", "println 'Hello World';");

    printWriter.flush();
    return stringWriter.toString();
  }
}
