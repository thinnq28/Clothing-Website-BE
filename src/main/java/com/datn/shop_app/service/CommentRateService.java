package com.datn.shop_app.service;

import com.datn.shop_app.DTO.CommentRateDTO;
import com.datn.shop_app.entity.CommentRate;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.comment.CommentRateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRateService {
    CommentRate save(CommentRateDTO commentRateDTO, User user) throws DataNotFoundException;

    Page<CommentRateResponse> getCommentRates(Integer productId, Pageable pageable);
}
