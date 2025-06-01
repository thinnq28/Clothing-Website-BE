package com.datn.shop_app.response.comment;

import com.datn.shop_app.entity.CommentRate;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRateResponse {
    private Integer id;
    private Integer productId;
    private String fullName;
    private Integer rating;
    private String content;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentRateResponse from(CommentRate commentRate) {
        return CommentRateResponse.builder()
                .id(commentRate.getId())
                .productId(commentRate.getProduct() != null ? commentRate.getProduct().getId() : null)
                .fullName(commentRate.getUser().getFullName())
                .rating(commentRate.getRating())
                .content(commentRate.getContent())
                .active(commentRate.getActive())
                .createdAt(commentRate.getCreatedAt())
                .updatedAt(commentRate.getUpdatedAt())
                .build();
    }
}
