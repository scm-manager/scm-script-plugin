package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import sonia.scm.ContextEntry;
import sonia.scm.NotFoundException;
import sonia.scm.script.domain.EventTypeRepository;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;

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
  @Consumes(ScriptMediaType.ONE)
  @SuppressWarnings("squid:S3655") // id is never empty after store
  public Response create(@Context UriInfo info, @Valid ScriptDto dto) {
    StorableScript script = repository.store(mapper.map(dto));
    URI uri = info.getRequestUriBuilder().path(script.getId().get()).build();
    return Response.created(uri).build();
  }

  @GET
  @Path("")
  @Produces(ScriptMediaType.COLLECTION)
  public Response findAll() {
    HalRepresentation collection = mapper.collection(repository.findAll());
    return Response.ok(collection).build();
  }

  @GET
  @Path("{id}")
  @Produces(ScriptMediaType.ONE)
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
  public Response getListeners(@PathParam("id") String id) {
    StorableScript script = findScriptById(id);
    ListenersDto collectionDto = listenerMapper.toCollection(script.getId().get(), script.getListeners());
    return Response.ok(collectionDto).build();
  }

  @GET
  @Path("{id}/history")
  @Produces(ScriptMediaType.LISTENER_COLLECTION)
  public ExecutionHistoryDto getHistory(@PathParam("id") String id, @Context UriInfo uriInfo) {
    return new ExecutionHistoryDto(createSelfLink(uriInfo), findScriptById(id).getExecutionHistory());
  }

  private StorableScript findScriptById(String id) {
    Optional<StorableScript> byId = repository.findById(id);
    if (!byId.isPresent()) {
      throw NotFoundException.notFound(ContextEntry.ContextBuilder.entity(StorableScript.class, id));
    }
    return byId.get();
  }

  @PUT
  @Path("{id}/listeners")
  @Consumes(ScriptMediaType.LISTENER_COLLECTION)
  public Response setListeners(@PathParam("id") String id, @Valid ListenersDto collectionDto) {
    StorableScript script = findScriptById(id);

    script.setListeners(listenerMapper.fromCollection(collectionDto));
    repository.store(script);

    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(ScriptMediaType.ONE)
  public Response modify(@PathParam("id") String id, @Valid ScriptDto dto) {
    if (id.equals(dto.getId())) {
      // TODO keep listeners
      repository.store(mapper.map(dto));
      return Response.noContent().build();
    }
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") String id) {
    repository.remove(id);
    return Response.noContent().build();
  }

  @POST
  @Path("run")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(ScriptMediaType.EXECUTION_RESULT)
  public ExecutionResult run(@QueryParam("lang") String type, String content) {
    return executor.execute(new StorableScript(type, content), ExecutionContext.empty());
  }

  @GET
  @Path("eventTypes")
  @Produces(ScriptMediaType.EVENT_TYPE_COLLECTION)
  public EventTypesDto findAllEventTypes(@Context UriInfo uriInfo) {
    return new EventTypesDto(createSelfLink(uriInfo), eventTypeRepository.findAll());
  }

  private Links createSelfLink(@Context UriInfo uriInfo) {
    return Links.linkingTo().self(uriInfo.getAbsolutePath().toASCIIString()).build();
  }

}
