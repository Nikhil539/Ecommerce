package com.example.backend.service;

import com.example.backend.dto.ProductRequest;
import com.example.backend.exception.ProductNotFoundException;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product getProductById(String id){
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
    }

    public Product createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setRating(request.getRating());
        product.setStock(request.getStock());

        return productRepository.save(product);
    }
    public Product updateProduct(String id, ProductRequest request) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setImageUrl(request.getImageUrl());
        existingProduct.setRating(request.getRating());
        existingProduct.setStock(request.getStock());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(String id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product Not Found"));

        productRepository.delete(product);
    }


}
