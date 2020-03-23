/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.script.domain.EventTypeRepository;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.ScriptNotFoundException;
import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

@OpenAPIDefinition(tags = {
  @Tag(name = "Script Plugin", description = "Script plugin provided endpoints")
})
@Path("v2/plugins/scripts")
public class ScriptResource {

  private final StorableScriptRepository repository;
  private final EventTypeRepository eventTypeRepository;
  private final ScriptMapper mapper;
  private final ListenerMapper listenerMapper;
  private final Executor executor;

  @Inject
  public ScriptResource(StorableScriptRepository repository, EventTypeRepository eventTypeRepository, ScriptMapper mapper, ListenerMapper listenerMapper, Executor executor) {
    this.repository = repository;
    this.eventTypeRepository = eventTypeRepository;
    this.mapper = mapper;
    this.listenerMapper = listenerMapper;
    this.executor = executor;
  }

  @POST
  @Path("")
  @Consumes(ScriptMediaType.SCRIPT)
  @Operation(
    summary = "Create new script",
    description = "Creates a new script.",
    tags = "Script Plugin",
    operationId = "script_create_script"
  )
  @ApiResponse(responseCode = "201", description = "create success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @SuppressWarnings("squid:S3655") // id is never empty after store
  public Response create(@Context UriInfo info, @Valid ScriptDto dto) {
    StorableScript script = repository.store(mapper.map(dto));
    URI uri = info.getRequestUriBuilder().path(script.getId().get()).build();
    return Response.created(uri).build();
  }

  @GET
  @Path("")
  @Produces(ScriptMediaType.SCRIPT_COLLECTION)
  @Operation(
    summary = "Get all scripts",
    description = "Returns all stored scripts.",
    tags = "Script Plugin",
    operationId = "script_get_scripts"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = ScriptMediaType.SCRIPT_COLLECTION,
      schema = @Schema(implementation = HalRepresentation.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response findAll() {
    HalRepresentation collection = mapper.collection(repository.findAll());
    return Response.ok(collection).build();
  }

  @GET
  @Path("{id}")
  @Produces(ScriptMediaType.SCRIPT)
  @Operation(
    summary = "Get single script",
    description = "Returns a single script.",
    tags = "Script Plugin",
    operationId = "script_get_script"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = ScriptMediaType.SCRIPT,
      schema = @Schema(implementation = ScriptDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / no script for given id available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response findById(@PathParam("id") String id) {
    Optional<StorableScript> byId = repository.findById(id);
    if (byId.isPresent()) {
      return Response.ok(mapper.map(byId.get())).build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }


  @GET
  @Path("{id}/listeners")
  @Produces(ScriptMediaType.LISTENER_COLLECTION)
  @Operation(
    summary = "Get listeners for script",
    description = "Returns all listeners for a single script.",
    tags = "Script Plugin",
    operationId = "script_get_listeners"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = ScriptMediaType.LISTENER_COLLECTION,
      schema = @Schema(implementation = ListenersDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / no script for given id available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @SuppressWarnings("squid:S3655") // id is never empty, because scripts from store have always an id
  public Response getListeners(@PathParam("id") String id) {
    StorableScript script = findScriptById(id);
    ListenersDto collectionDto = listenerMapper.toCollection(
      script.getId().get(), script.getListeners(), script.isStoreListenerExecutionResults()
    );
    return Response.ok(collectionDto).build();
  }

  @GET
  @Path("{id}/history")
  @Produces(ScriptMediaType.LISTENER_COLLECTION)
  @Operation(
    summary = "Get history for script",
    description = "Returns the execution history for a single script.",
    tags = "Script Plugin",
    operationId = "script_get_script_history"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = ScriptMediaType.LISTENER_COLLECTION,
      schema = @Schema(implementation = ExecutionHistoryDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / no script for given id available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public ExecutionHistoryDto getHistory(@PathParam("id") String id, @Context UriInfo uriInfo) {
    return new ExecutionHistoryDto(createSelfLink(uriInfo), findScriptById(id).getExecutionHistory());
  }

  private StorableScript findScriptById(String id) {
    Optional<StorableScript> byId = repository.findById(id);
    if (!byId.isPresent()) {
      throw new ScriptNotFoundException(id);
    }
    return byId.get();
  }

  @PUT
  @Path("{id}/listeners")
  @Consumes(ScriptMediaType.LISTENER_COLLECTION)
  @Operation(
    summary = "Create new listener",
    description = "Creates a new listener for a stored script.",
    tags = "Script Plugin",
    operationId = "script_create_listener"
  )
  @ApiResponse(responseCode = "204", description = "no content")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / no script for given id available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response setListeners(@PathParam("id") String id, @Valid ListenersDto collectionDto) {
    StorableScript script = findScriptById(id);

    script.setListeners(listenerMapper.fromCollection(collectionDto));
    script.setStoreListenerExecutionResults(collectionDto.isStoreListenerExecutionResults());
    repository.store(script);

    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(ScriptMediaType.SCRIPT)
  @Operation(
    summary = "Update script",
    description = "Modifies a stored script.",
    tags = "Script Plugin",
    operationId = "script_modify_script"
  )
  @ApiResponse(responseCode = "204", description = "no content")
  @ApiResponse(responseCode = "400", description = "bad request / invalid body")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response modify(@PathParam("id") String id, @Valid ScriptDto dto) {
    if (id.equals(dto.getId())) {
      StorableScript storedScript = findScriptById(id);
      mapper.map(dto, storedScript);
      repository.store(storedScript);
      return Response.noContent().build();
    }
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  @DELETE
  @Path("{id}")
  @Operation(
    summary = "Delete script",
    description = "Deletes a stored script.",
    tags = "Script Plugin",
    operationId = "script_delete_script"
  )
  @ApiResponse(responseCode = "204", description = "delete success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response delete(@PathParam("id") String id) {
    repository.remove(id);
    return Response.noContent().build();
  }

  @POST
  @Path("run")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(ScriptMediaType.EXECUTION_RESULT)
  @Operation(
    summary = "Run script",
    description = "Executes the given script.",
    tags = "Script Plugin",
    operationId = "script_execute_script"
  )
  @ApiResponse(
    responseCode = "204",
    description = "no content",
    content = @Content(
      mediaType = ScriptMediaType.EXECUTION_RESULT,
      schema = @Schema(implementation = ExecutionResult.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public ExecutionResult run(@QueryParam("lang") String type, String content) {
    return executor.execute(new StorableScript(type, content), ExecutionContext.empty());
  }

  @GET
  @Path("eventTypes")
  @Produces(ScriptMediaType.EVENT_TYPE_COLLECTION)
  @Operation(
    summary = "Get event types for script",
    description = "Returns a list of event types which can trigger the execution of a script.",
    tags = "Script Plugin",
    operationId = "script_get_event_types"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = ScriptMediaType.EVENT_TYPE_COLLECTION,
      schema = @Schema(implementation = EventTypesDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public EventTypesDto findAllEventTypes(@Context UriInfo uriInfo) {
    return new EventTypesDto(createSelfLink(uriInfo), eventTypeRepository.findAll());
  }

  private Links createSelfLink(@Context UriInfo uriInfo) {
    return Links.linkingTo().self(uriInfo.getAbsolutePath().toASCIIString()).build();
  }

}
