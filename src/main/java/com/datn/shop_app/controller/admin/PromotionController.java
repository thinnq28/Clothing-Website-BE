package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping()
    public ResponseEntity<ResponseObject> addPromotion(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult) {
        List<String> errors = promotionService.validateInsertion(promotionDTO, bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Tạo khuyến mại cho sản phẩm thất bại")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors).build());
        }

        Promotion promotion = promotionService.save(promotionDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Tạo khuyến mại cho sản phẩm thành công")
                .status(HttpStatus.OK)
                .data(promotion)
                .build());
    }
}
