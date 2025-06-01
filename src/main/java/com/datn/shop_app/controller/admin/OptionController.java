package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.OptionDTO;
import com.datn.shop_app.entity.Option;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.option.ListOptionResponse;
import com.datn.shop_app.response.option.OptionResponse;
import com.datn.shop_app.service.OptionService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/options")
public class OptionController {

    @Autowired
    private OptionService optionService;

    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOption(@RequestBody OptionDTO optionDTO,
                                                       BindingResult result) {
        List<String> errors = optionService.validateOption(result, optionDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_OPTION_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors).build());
        }

        Option option = optionService.save(optionDTO);
        OptionResponse optionResponse = OptionResponse.fromOption(option);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_OPTION_SUCCESSFUL))
                .status(HttpStatus.OK)
                .data(optionResponse).build());
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getOptions(@RequestParam(defaultValue = "") String name) {
        try{
            List<OptionResponse> optionResponses = optionService.getOptions(name, true);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_OPTION_SUCCESSFUL))
                    .status(HttpStatus.OK)
                    .data(optionResponses)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_OPTION_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getOptions(@RequestParam(defaultValue = "") String name,
                                                     @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<OptionResponse> optionPage = optionService.getOptions(name, active, pageRequest);
        totalPages = optionPage.getTotalPages();
        List<OptionResponse> optionResponses = optionPage.getContent();

        for (OptionResponse optionResponse : optionResponses) {
            optionResponse.setTotalPages(totalPages);
        }

        ListOptionResponse listSupplierResponse = ListOptionResponse.builder()
                .options(optionResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_OPTION_SUCCESSFUL))
                .status(HttpStatus.OK)
                .data(listSupplierResponse)
                .build());
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getOptionDetail(@PathVariable Integer id) {
        Option option = optionService.getOption(id);

        if (option == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.OPTION_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        OptionResponse optionResponse = OptionResponse.fromOption(option);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_OPTION_SUCCESSFUL))
                .status(HttpStatus.OK)
                .data(optionResponse).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION')")
    public ResponseEntity<ResponseObject> updateOption(@PathVariable Integer id,
                                                       @RequestBody OptionDTO optionDTO) {
        Option option = optionService.getOption(id);

        if (option == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.OPTION_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        option = optionService.update(id, optionDTO);
        OptionResponse optionResponse = OptionResponse.fromOption(option);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_OPTION_SUCCESSFUL))
                .status(HttpStatus.OK)
                .data(optionResponse).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPTION')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        Option option = optionService.getOption(id);
        if (option == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.OPTION_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        optionService.deleteOption(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_OPTION_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data("").build());
    }
}
