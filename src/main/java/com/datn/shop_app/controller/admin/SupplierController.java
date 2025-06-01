package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.SupplierDTO;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.supplier.ListSupplierResponse;
import com.datn.shop_app.response.supplier.SupplierResponse;
import com.datn.shop_app.service.SupplierService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
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
@RequestMapping("${api.prefix}/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    private final LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody SupplierDTO supplierDTO,
                                                 BindingResult result) {

        List<String> errors = supplierService.validateSupplier(result, supplierDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_SUPPLIER_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }
        Supplier supplier = supplierService.save(supplierDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_SUPPLIER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(supplier).build());
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPPLIER') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getSuppliers(@RequestParam(defaultValue = "", name = "supplier_name") String name) {
        try {
            List<SupplierResponse> supplierResponses = supplierService.getAllSuppliers(name, true);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SUPPLIER_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(supplierResponses)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SUPPLIER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPPLIER') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getSuppliers(@RequestParam(defaultValue = "") String name,
                                                       @RequestParam(defaultValue = "", name = "phone_number") String phoneNumber,
                                                       @RequestParam(defaultValue = "", name = "email") String email,
                                                       @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<SupplierResponse> userPage = supplierService.getAllSuppliers(name, phoneNumber, email, active, pageRequest);
        totalPages = userPage.getTotalPages();
        List<SupplierResponse> supplierResponses = userPage.getContent();

        for (SupplierResponse supplierResponse : supplierResponses) {
            supplierResponse.setTotalPages(totalPages);
        }

        ListSupplierResponse listSupplierResponse = ListSupplierResponse.builder()
                .suppliers(supplierResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SUPPLIER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(listSupplierResponse)
                .build());
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPPLIER') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getSupplierDetail(@PathVariable Integer id) {
        Supplier supplier = supplierService.getSupplier(id);

        if (supplier == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.SUPPLIER_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SUPPLIER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(supplier).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ResponseObject> updateSupplier(@PathVariable Integer id,
                                                         @Valid @RequestBody SupplierDTO supplierDTO,
                                                         BindingResult result) {
        Supplier supplier = supplierService.getSupplier(id);
        List<String> errors = supplierService.validateUpdateSupplier(result, supplierDTO, supplier);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SUPPLIER_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }
        supplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SUPPLIER_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .data(supplier).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        Supplier supplier = supplierService.getSupplier(id);
        if (supplier == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.SUPPLIER_IS_NOT_EXISTS))
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_SUPPLIER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data("").build());
    }

}
