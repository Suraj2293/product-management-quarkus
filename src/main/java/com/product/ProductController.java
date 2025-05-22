package com.product;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Tag(name = "Product APIs", description = "CRUD operations on products")
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductRepository repository;

    @GET
    public Uni<List<Product>> getAll() {
        return repository.listAll(Sort.by("price").ascending());
    }

    @GET
    @Path("/hello")
    public Uni<String> hello() {
        return Uni.createFrom().item("Hello...");
    }
    
    @GET
    @Path("/{id}")
    public Uni<Product> getById(@PathParam("id") Integer id) {
        return repository.findById(id);
    }

    @GET
    @Path("/{id}/quantity/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> checkQuantity(@PathParam("id") Integer id, @PathParam("count") Integer count) {
        return repository.findById(id)
                .onItem().ifNotNull().transform(product -> {
                    if (product.getQuantity() > count) {
                        return Response.ok(Map.of("message", "Stock is available for product " + id)).build();
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(Map.of("message", "Stock is not available for product " + id)).build();
                    }
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Product not found")).build());
    }

    @POST
    public Uni<Response> create(Product product) {
        return Panache.withTransaction(() ->
                repository.persist(product)
        ).map(p -> Response.created(URI.create("/products/" + p.getId())).entity(p).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> update(@PathParam("id") Integer id, Product updatedProduct) {
        return Panache.withTransaction(() ->
                repository.updateProduct(id, updatedProduct))
                .onItem().ifNull().failWith(() -> new NotFoundException("Product not found"))
                .map(p -> Response.ok(p).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Integer id) {
        return Panache.withTransaction(() ->
                repository.deleteById(id))
                .map(deleted -> deleted ?
                        Response.status(Response.Status.OK).entity(Map.of("message", "Product with Id " + id + " deleted successfully")).build()
                        : Response.status(Response.Status.NOT_FOUND).build());
    }


}

