package com.example.backend.controller;

import com.example.backend.dto.ProductRequest;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173/")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final MongoTemplate mongoTemplate;

    public ProductController(
            ProductService productService,
            MongoTemplate mongoTemplate) {

        this.productService = productService;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/getAll")
    public List<Product> getAllProducts() {

        System.out.println(
                "Fetching all products from the database..."
        );

        System.out.println(
                "Database: " +
                        mongoTemplate.getDb().getName()
        );

        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable String id) {

        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable String id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }




}