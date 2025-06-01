package com.datn.shop_app.service;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

public interface PromotionService {

    List<String> validateInsertion(PromotionDTO promotionDTO, BindingResult bindingResult);

    Promotion save(PromotionDTO promotionDTO);
}