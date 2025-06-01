package com.datn.shop_app.controller.user;

import com.datn.shop_app.entity.Product;
import com.datn.shop_app.repository.ProductRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.commodity.CommodityResponse;
import com.datn.shop_app.response.product.ListProductResponse;
import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.service.CommodityService;
import com.datn.shop_app.service.ProductService;
import com.datn.shop_app.service.VariantService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/client/product")
public class ClientProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private VariantService variantService;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ResponseObject> getProducts(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String supplierName,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "") String commodityName,
            @RequestParam(defaultValue = "") Integer rating,
            @RequestParam(defaultValue = "true", name = "isActive") Boolean active,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "9") int limit
    ) {
        try {
            int totalPages = 0;
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").ascending()
            );
            List<ProductResponse> productResponses = new ArrayList<>();

            if (productResponses != null && !productResponses.isEmpty()) {
                totalPages = productResponses.get(0).getTotalPages();
            } else {
                Page<ProductResponse> productPage =
                        productService.getProductClient(name, supplierName,
                                commodityName, active, maxPrice, rating, pageRequest);
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

    @GetMapping("/max-price")
    public ResponseEntity<ResponseObject> getMaxPrice() {
        Double maxPrice = productRepository.findMaxPrice();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_PRODUCT_SUCCESSFULLY))
                .data(maxPrice)
                .status(HttpStatus.OK).build());
    }

    @GetMapping("/by-name")
    public ResponseEntity<ResponseObject> getProducts(@RequestParam(defaultValue = "", name = "product_name") String name) {
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
}
