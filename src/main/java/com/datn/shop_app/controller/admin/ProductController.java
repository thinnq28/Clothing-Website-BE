package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.ProductDTO;
import com.datn.shop_app.entity.Product;
import com.datn.shop_app.repository.ProductRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.product.ListProductResponse;
import com.datn.shop_app.response.product.ProductResponse;
//import com.datn.shop_app.service.ProductRedisService;
import com.datn.shop_app.service.ProductService;
import com.datn.shop_app.utils.FileUtils;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

//    private final ProductRedisService productRedisService;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCT')")
    public ResponseEntity<ResponseObject> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                     BindingResult result) {
        List<String> errors = productService.validateInsert(productDTO, result);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_PRODUCT_FAILED))
                    .data(errors)
                    .status(HttpStatus.BAD_REQUEST).build());
        }

        ProductResponse productResponse = productService.save(productDTO);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_PRODUCT_SUCCESSFULLY))
                .data(productResponse)
                .status(HttpStatus.OK).build());
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCT')")
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Integer productId,
            @ModelAttribute("file") MultipartFile file) throws Exception {
        Product product = productService.getProductById(productId);

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder().message(localizationUtils.getLocalizedMessage(MessageKeys.FILE_IMAGE_CANNOT_BE_EMPTY))
                            .status(HttpStatus.BAD_REQUEST).build());
        } else {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                FileUtils.deleteFile(product.getImageUrl());
            }
        }

        //kiểm tra kích thước file và định dạng
        if (file.getSize() > 10 * 1024 * 1024) {// > 10MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                    ResponseObject.builder().message(String.format(localizationUtils.getLocalizedMessage(MessageKeys.FILE_IMAGE_LARGE), null, "10MB"))
                            .status(HttpStatus.BAD_REQUEST).build());
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder().message(localizationUtils.getLocalizedMessage(MessageKeys.FILE_MUST_BE_IMAGE))
                            .status(HttpStatus.BAD_REQUEST).build());
        }
        // Lưu file và cập nhật thumbnail trong DTO
        String filename = FileUtils.storeFile(file);

        product.setImageUrl(filename);
        product = productRepository.save(product);

        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(ProductResponse.fromProduct(product))
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                        .status(HttpStatus.OK).build());
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getProducts(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String supplierName,
            @RequestParam(defaultValue = "") String commodityName,
            @RequestParam(defaultValue = "true", name = "isActive") Boolean active,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        try {
            int totalPages = 0;
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").ascending()
            );

//            List<ProductResponse> productResponses = productRedisService.getProducts(name, supplierName, commodityName, active, pageRequest);
            List<ProductResponse> productResponses = new ArrayList<>();

            if (productResponses != null && !productResponses.isEmpty()) {
                totalPages = productResponses.get(0).getTotalPages();
            } else {
                Page<ProductResponse> productPage = productService.getProducts(name, supplierName, commodityName, active, pageRequest);
                totalPages = productPage.getTotalPages();
                productResponses = productPage.getContent();

                for (ProductResponse productResponse : productResponses) {
                    productResponse.setTotalPages(totalPages);
                }

//                productRedisService.saveProducts(productResponses, name, supplierName, commodityName, active, pageRequest);
            }

            ListProductResponse listProductValueResponse = ListProductResponse.builder()
                    .products(productResponses)
                    .totalPages(totalPages).build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(listProductValueResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_FAILED))
                    .status(HttpStatus.OK)
                    .build());
        }
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCT') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getSuppliers(@RequestParam(defaultValue = "", name = "product_name") String name) {
        try {
            List<ProductResponse> productResponses = productService.getProductByName(name, true);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(productResponses)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCT')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result) {
        List<String> errors = productService.validateUpdate(id, productDTO, result);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_PRODUCT_FAILED))
                    .data(errors)
                    .status(HttpStatus.BAD_REQUEST).build());
        }

        ProductResponse productResponse = productService.update(id, productDTO);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_PRODUCT_SUCCESSFULLY))
                .data(productResponse)
                .status(HttpStatus.OK).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCT')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_IS_NOT_EXISTS))
                        .status(HttpStatus.NOT_FOUND)
                        .data("").build());
            }

            productService.delete(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_PRODUCT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data("").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_PRODUCT_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("").build());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ResponseObject> getProduct(@PathVariable Integer id) {
        try {
            Product product = productService.getProductById(id);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_SUCCESSFULLY))
                    .data(ProductResponse.fromProduct(product))
                    .status(HttpStatus.OK).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_FAILED)).build());
        }
    }
}
