package com.datn.shop_app.controller.user;

import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.commodity.CommodityResponse;
import com.datn.shop_app.service.CommodityService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/client/commodity")
public class ClientCommodityController {

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping
    public ResponseEntity<ResponseObject> getCommodities() {
        List<CommodityResponse> commodityResponses = commodityService.getAllCommodities();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_COMMODITY_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(commodityResponses)
                .build());
    }
}
