package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.OptionValueDTO;
import com.datn.shop_app.entity.OptionValue;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.option_value.ListOptionValueResponse;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import com.datn.shop_app.service.OptionService;
import com.datn.shop_app.service.OptionValueService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/option-values")
public class OptionValueController {
    private final OptionValueService optionValueService;

    private final LocalizationUtils localizationUtils;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getOptions(@RequestParam(name = "option_id") Integer optionId,
                                                     @RequestParam(defaultValue = "") String name,
                                                     @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<OptionValueResponse> optionPage = optionValueService.getOptionValues(optionId, name, active, pageRequest);
        totalPages = optionPage.getTotalPages();
        List<OptionValueResponse> optionValueResponses = optionPage.getContent();

        for (OptionValueResponse optionValueResponse : optionValueResponses) {
            optionValueResponse.setTotalPages(totalPages);
        }

        ListOptionValueResponse listOptionValueResponse = ListOptionValueResponse.builder()
                .optionValues(optionValueResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_OPTION_VALUE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(listOptionValueResponse)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION')")
    public ResponseEntity<ResponseObject> updateOption(@PathVariable Integer id,
                                                       @Valid @RequestBody OptionValueDTO optionValueDTO,
                                                       BindingResult result) {
        List<String> errors = optionValueService.validateOptionValue(id, result, optionValueDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_OPTION_VALUE_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }

        OptionValueResponse optionResponse = optionValueService.update(id, optionValueDTO);
        if(optionResponse == null) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_OPTION_VALUE_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .data(optionResponse).build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_OPTION_VALUE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(optionResponse).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        OptionValue option = optionValueService.getOptionValue(id);
        if (option == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.OPTION_VALUE_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        optionValueService.deleteOptionValue(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_OPTION_VALUE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data("").build());
    }
}
