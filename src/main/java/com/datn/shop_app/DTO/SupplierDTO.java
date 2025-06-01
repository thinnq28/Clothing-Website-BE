package com.datn.shop_app.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupplierDTO {

    @NotBlank(message = "{supplier.name.not_blank}")
    @NotNull(message = "{supplier.name.not_null}")
    private String supplierName;

    @NotNull(message = "{supplier.phone_number.not_null}")
    @NotBlank(message = "{supplier.phone_number.not_blank}")
    @Length(max = 10, min = 10, message = "{supplier.phone_number.length}")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("email")
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",
            message = "{supplier.email.pattern}")
    private String email;
}
