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
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorableScriptResourceTest {

  @Mock
  private StorableScriptRepository repository;

  @Mock
  private ScriptMapper mapper;

  @Mock
  private Executor executor;

  @InjectMocks
  private ScriptResource resource;

  @Test
  void shouldReturnLocationLinkAfterCreation() {
    ScriptDto dto = new ScriptDto();
    StorableScript script = ScriptTestData.createHelloWorld();

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
    List<StorableScript> scripts = Lists.newArrayList(ScriptTestData.createHelloWorld());
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
    StorableScript script = ScriptTestData.createHelloWorld();

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
    StorableScript script = ScriptTestData.createHelloWorld();

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
  void shouldExecuteTheScript() {
    ExecutionResult executionResult = new ExecutionResult(true, "Hello World", Instant.now(), Instant.now());
    when(executor.execute(any(Script.class), any(ExecutionContext.class))).thenReturn(executionResult);

    ExecutionResult result = resource.run("Groovy", "println 'Hello World';");

    ArgumentCaptor<StorableScript> scriptCaptor = ArgumentCaptor.forClass(StorableScript.class);
    ArgumentCaptor<ExecutionContext> contextCaptor = ArgumentCaptor.forClass(ExecutionContext.class);

    verify(executor).execute(scriptCaptor.capture(), contextCaptor.capture());

    StorableScript script = scriptCaptor.getValue();
    assertThat(script.getType()).isEqualTo("Groovy");
    assertThat(script.getContent()).isEqualTo("println 'Hello World';");

    assertThat(result).isSameAs(executionResult);
  }
}
