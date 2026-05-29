package backend.service;

import backend.entity.Product;
import backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final String UPLOAD_DIR = "uploads/";

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        // Create uploads directory if not exists
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Product addProduct(String name, String description, Double price,
                              String category, MultipartFile image) {

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, String name, String description, Double price,
                                 String category, MultipartFile image) {

        return productRepository.findById(id).map(product -> {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);

            if (image != null && !image.isEmpty()) {
                String imageUrl = saveImage(image);
                product.setImageUrl(imageUrl);
            }

            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), filePath);
            return "/" + UPLOAD_DIR + fileName;   // e.g. /uploads/abc123_image.jpg
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}