package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.UserRoleDTO;
import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.service.UserRoleService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/user_roles")
@RequiredArgsConstructor
public class UserRoleController {
    private final UserRoleService userRoleService;

    private final LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> addUserRole(@Valid @RequestBody UserRoleDTO userRoleDTO, BindingResult bindingResult) throws DataNotFoundException {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors) {
                errorMessages.add(error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ADD_ROLE_FOR_USER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errorMessages).build());
        }

        UserRole userRole = userRoleService.save(userRoleDTO.getUserId(), userRoleDTO.getRoleId());

        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ADD_ROLE_FOR_USER_SUCCESSFULLY))
                        .data(userRole).build());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteUserRole(@RequestParam(name = "user_id") Integer userId,
                                                         @RequestParam(name = "role_id") Integer roleId
                                                         ) {
        if(userId == null || userId <= 0 || roleId <= 0 || roleId == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ROLE_OF_USER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("").build());
        }

        try {
            userRoleService.delete(userId, roleId);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ROLE_OF_USER_SUCCESSFULLY))
                            .data("").build());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ROLE_OF_USER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(e.getMessage()).build());
        }
    }
}
