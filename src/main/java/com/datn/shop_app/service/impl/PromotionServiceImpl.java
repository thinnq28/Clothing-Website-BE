package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Product;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.PromotionProduct;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.repository.ProductRepository;
import com.datn.shop_app.repository.PromotionProductRepository;
import com.datn.shop_app.repository.PromotionRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.service.PromotionService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    private final LocalizationUtils localizationUtils;

    private final ProductRepository productRepository;

    private final PromotionProductRepository promotionProductRepository;

    private final UserRepository userRepository;

//    @Override
//    public Page<PromotionResponse> getPromotions(String name, Boolean active, Pageable pageable) {
//        Page<Promotion> promotions = promotionRepository.findAllPromotions(name, active, pageable);
//        return promotions.map(PromotionResponse::fromPromotion);
//    }

    @Override
    public List<String> validateInsertion(PromotionDTO promotionDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        if(promotionDTO.getStartDate().isEqual(promotionDTO.getEndDate())){
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.START_DATE_AFTER_END_DATE));
        }

        return errors;
    }

    @Override
    public Promotion save(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(promotionDTO, promotion);
        promotion.setActive(true);
        Promotion createdPromotion = promotionRepository.save(promotion);

        List<Product> products = productRepository.findAllByIdIn(promotionDTO.getProductIds());

        List<User> users = new ArrayList<>();
        if (promotionDTO.getUserIds() != null && !promotionDTO.getUserIds().isEmpty()) {
            users = userRepository.findAllByIdIn(promotionDTO.getUserIds());
        }

        List<PromotionProduct> promotionProducts = new ArrayList<>();

        for (Product product : products) {
            if (users.isEmpty()) {
                // Nếu không có user, tạo PromotionProduct với product và promotion
                PromotionProduct pp = new PromotionProduct();
                pp.setProduct(product);
                pp.setPromotion(createdPromotion);
                promotionProducts.add(pp);
            } else {
                // Nếu có user, tạo cho mỗi user một bản ghi PromotionProduct
                for (User user : users) {
                    PromotionProduct pp = new PromotionProduct();
                    pp.setProduct(product);
                    pp.setPromotion(createdPromotion);
                    pp.setUser(user);
                    promotionProducts.add(pp);
                }
            }
        }

        promotionProductRepository.saveAll(promotionProducts);
        return createdPromotion;
    }

//    @Override
//    public Promotion update(Integer id, PromotionDTO promotionDTO) {
//        Promotion promotion = promotionRepository.findById(id).orElse(null);
//        if(promotion != null){
//            BeanUtils.copyProperties(promotionDTO, promotion);
//            return promotionRepository.save(promotion);
//        }
//        return null;
//    }
//
//    @Override
//    public Promotion getPromotion(Integer promotionId) {
//        return promotionRepository.findById(promotionId).orElse(null);
//    }
//
//    @Override
//    public List<Promotion> getPromotionByIds(List<Integer> promotionIds) {
//        return promotionRepository.getPromotionsByIds(promotionIds);
//    }
//
//    @Override
//    public List<String> validateUpgrade(Integer id, PromotionDTO promotionDTO, BindingResult bindingResult) {
//        List<String> errors = new ArrayList<>();
//        if (bindingResult.hasErrors()) {
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                errors.add(fieldError.getDefaultMessage());
//            }
//
//            return errors;
//        }
//
//        Promotion promotion = promotionRepository.findById(id).orElse(null);
//        if(promotion == null){
//            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.PROMOTION_IS_NOT_FOUND));
//        }
//
//        if(promotionDTO.getStartDate().isEqual(promotionDTO.getEndDate())){
//            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.START_DATE_AFTER_END_DATE));
//        }
//
//        return errors;
//    }
//
//    @Override
//    public void delete(Integer id) {
//        Promotion promotion = promotionRepository.findByIdAndActive(id, true);
//        if(promotion != null){
//            promotion.setActive(false);
//            promotionRepository.save(promotion);
//        }
//    }
//
//    @Override
//    public List<Promotion> getPromotions(LocalDate endDate){
//        return promotionRepository.getPromotionByEndDate(endDate, true);
//    }
//
//    @Override
//    public void setActive(List<Promotion> promotions){
//        for (Promotion promotion : promotions) {
//                promotion.setActive(false);
//        }
//
//        promotionRepository.saveAll(promotions);
//    }

}