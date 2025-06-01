package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @JsonProperty("fullname")
    @NotNull(message = "{user.name.not_null}")
    @NotBlank(message = "{user.name.not_blank}")
    private String fullName;

    @JsonProperty("phone_number")
    @NotNull(message = "{user.phone_number.not_null}")
    @NotBlank(message = "{user.phone_number.not_blank}")
    private String phoneNumber = "";


    @JsonProperty("email")
    @NotNull(message = "{user.email.not_null}")
    @NotBlank(message = "{user.email.not_blank}")
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "{user.email.pattern}")
    private String email = "";

    private String address = "";

    @NotBlank(message = "{user.password.not_null}")
    @NotNull(message = "{user.password.not_blank}")
    private String password = "";

    @JsonProperty("retype_password")
    @NotBlank(message = "{user.retype_password.not_blank}")
    @NotNull(message = "{user.retype_password.not_null}")
    private String retypePassword = "";

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @NotNull(message = "{user.role.not_null}")
    @JsonProperty("role_id")
    //role admin not permitted
    private Integer roleId;
}
