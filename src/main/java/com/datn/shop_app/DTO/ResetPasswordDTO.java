package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordDTO {
    @NotBlank(message = "Token có vẻ không đúng")
    private String token;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "{password.new_password.size}")
    private String password;

    @NotBlank(message = "Mật khẩu không khớp")
    @Size(min = 6, message = "{password.retype_password.size}")
    private String retypePassword;
}
