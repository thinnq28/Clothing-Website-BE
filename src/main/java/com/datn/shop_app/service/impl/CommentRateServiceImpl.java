package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.CommentRateDTO;
import com.datn.shop_app.entity.CommentRate;
import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.Product;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.repository.CommentRateRepository;
import com.datn.shop_app.repository.ProductRepository;
import com.datn.shop_app.response.comment.CommentRateResponse;
import com.datn.shop_app.service.CommentRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentRateServiceImpl implements CommentRateService {
    private final CommentRateRepository commentRateRepository;
    private final ProductRepository productRepository;

    @Override
    public CommentRate save(CommentRateDTO commentRateDTO, User user) throws DataNotFoundException {
        CommentRate commentRate = new CommentRate();
        Optional<Product> product = productRepository.findByIdAndActive(commentRateDTO.getProductId(), true);
        if (product.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy sản phẩm bạn cần bình luận");
        }

        BeanUtils.copyProperties(commentRateDTO, commentRate);
        commentRate.setProduct(product.get());
        commentRate.setUser(user);
        commentRate.setActive(true);
        return commentRateRepository.save(commentRate);
    }

    @Override
    public Page<CommentRateResponse> getCommentRates(Integer productId, Pageable pageable) {
        Page<CommentRate> commentRates = commentRateRepository.findAllByProductId(productId, pageable);
        return commentRates.map(CommentRateResponse::from);
    }
}
