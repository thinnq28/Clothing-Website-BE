package com.datn.shop_app.controller.user;

import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.service.VariantService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/client/variant")
public class ClientVariantController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private VariantService variantService;

    @GetMapping("/by-product/{product_id}")
    public ResponseEntity<ResponseObject> getVariantsByProduct(@PathVariable(name = "product_id") Integer id) {
        List<VariantResponse> variantResponses = variantService.getVariants(id);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VARIANT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(variantResponses).build());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<ResponseObject> getVariantsByProduct(@RequestParam List<Integer> ids) {
        List<VariantResponse> variantResponses = variantService.getVariantByIds(ids);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VARIANT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(variantResponses).build());
    }
}
