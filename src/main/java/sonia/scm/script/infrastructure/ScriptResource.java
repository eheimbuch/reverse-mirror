package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
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
  private final ScriptMapper mapper;
  private final Executor executor;

  @Inject
  public ScriptResource(StorableScriptRepository repository, ScriptMapper mapper, Executor executor) {
    this.repository = repository;
    this.mapper = mapper;
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

  @PUT
  @Path("{id}")
  @Consumes(ScriptMediaType.ONE)
  public Response modify(@PathParam("id") String id, @Valid ScriptDto dto) {
    if (id.equals(dto.getId())) {
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

}
