package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    @NotBlank(message = "{user_login.phone_number.not_blank}")
    @NotNull(message = "{user_login.phone_number.not_null}")
    private String phoneNumber;

    private String email;

    @NotBlank(message = "{user_login.password.not_blank}")
    @NotNull(message = "{user_login.password.not_null}")
    private String password;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }

}
