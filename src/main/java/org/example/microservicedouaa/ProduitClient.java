package org.example.microservicedouaa;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "MicroserviceProduits", configuration = FeignConfig.class)
public interface ProduitClient {

    @GetMapping("/products/{id}")
    Produit getProduitById(@PathVariable("id") Long id);


    @GetMapping("/products/exists/{id}")
    Boolean checkProductExists(@PathVariable("id") Long id);


    @GetMapping("/products/search")
    public List<Produit> getProductsByCategory(@RequestParam String category);


    @GetMapping("/products/categories")
    public List<String> getAllCategories();


}
