package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.CommodityDTO;
import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.repository.CommodityRepository;
import com.datn.shop_app.response.commodity.CommodityResponse;
import com.datn.shop_app.service.CommodityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommodityServiceImpl implements CommodityService {
    private final CommodityRepository commodityRepository;

    @Override
    @Transactional
    public Commodity save(CommodityDTO commodityDTO) {
        Commodity commodity = new Commodity();
        BeanUtils.copyProperties(commodityDTO, commodity);
        commodity.setActive(true);
        return commodityRepository.save(commodity);
    }


    @Override
    public Page<CommodityResponse> getAllCommodities(String name, Boolean active, Pageable pageable) {
        Page<Commodity> commodityPage = commodityRepository.findAllCommodities(name, active, pageable);
        return commodityPage.map(CommodityResponse::fromCommodity);
    }

    @Override
    public List<CommodityResponse> getAllCommodities() {
        List<Commodity> commodityPage = commodityRepository.findAllCommoditiesAndActive(true);
        List<CommodityResponse> commodityResponses = new ArrayList<>();
        for (Commodity commodity : commodityPage) {
            commodityResponses.add(CommodityResponse.fromCommodity(commodity));
        }
        return commodityResponses;
    }

    @Override
    public List<CommodityResponse> getAllCommodities(String name, Boolean active) {
        List<Commodity> commodities = commodityRepository.findAllCommodities(name, active);
        List<CommodityResponse> commodityResponses = new ArrayList<>();
        for (Commodity commodity : commodities) {
            commodityResponses.add(CommodityResponse.fromCommodity(commodity));
        }
        return commodityResponses;
    }

    @Override
    public Commodity getCommodity(Integer id) {
        Optional<Commodity> commodity = commodityRepository.findByIdAndActive(id, true);
        return commodity.get();
    }

    @Override
    @Transactional
    public Commodity update(Integer id, CommodityDTO commodityDTO) {
        Optional<Commodity> commodity = commodityRepository.findByIdAndActive(id, true);
        if(commodity.isPresent()){
            commodity.get().setCommodityName(commodityDTO.getCommodityName());
            return commodityRepository.save(commodity.get());
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        Optional<Commodity> commodity = commodityRepository.findByIdAndActive(id, true);
        if(commodity.isPresent()) {
            commodity.get().setActive(false);
            commodityRepository.save(commodity.get());
        }
    }
}
