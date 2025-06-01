package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionValueDTO {
    @NotNull(message = "{option_value.option_value_id.not_null}")
    private Integer optionValueId;

    @Size(max = 100, message = "{option_value.option_value_name.size}")
    @NotNull(message = "{option_value.option_value_name.not_null}")
    @NotBlank(message = "{option_value.option_value_name.not_blank}")
    private String optionValueName;

    @NotNull(message = "{option_value.option.not_null}")
    private Integer optionId;
}
