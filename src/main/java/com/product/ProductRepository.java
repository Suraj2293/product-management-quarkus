package com.product;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Integer> {

    public Uni<Product> updateProduct(Integer id, Product updatedProduct) {
        return findById(id).invoke(product -> {
            if (product != null) {
                product.setName(updatedProduct.getName());
                product.setDescription(updatedProduct.getDescription());
                product.setPrice(updatedProduct.getPrice());
                product.setQuantity(updatedProduct.getQuantity());

            }
        });
    }
}
