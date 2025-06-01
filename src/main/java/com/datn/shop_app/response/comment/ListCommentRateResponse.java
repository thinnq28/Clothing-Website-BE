package com.datn.shop_app.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListCommentRateResponse {
    private List<CommentRateResponse> commentRates;
    private int totalPages;
}
