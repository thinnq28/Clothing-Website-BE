package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "{password.current_password.not_blank}")
    private String currentPassword;

    @NotBlank(message = "{password.new_password.not_blank}")
    @Size(min = 6, message = "{password.new_password.size}")
    private String newPassword;

    @NotBlank(message = "{password.retype_password.not_blank}")
    @Size(min = 6, message = "{password.retype_password.size}")
    private String confirmPassword;
}
