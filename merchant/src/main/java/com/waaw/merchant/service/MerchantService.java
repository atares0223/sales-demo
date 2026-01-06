package com.waaw.merchant.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waaw.common.exception.BusinessException;
import com.waaw.merchant.Merchant;
import com.waaw.merchant.repository.MerchantRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public Merchant findById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Merchant not found with id: " + id));
    }

    public List<Merchant> findAll() {
        return merchantRepository.findAll();
    }

    public Merchant save( Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public Merchant update( Long id,  Merchant merchant) {
        Merchant existingMerchant = findById(id);
        existingMerchant.setName(merchant.getName());
        existingMerchant.setBusinessName(merchant.getBusinessName());
        existingMerchant.setEmail(merchant.getEmail());
        existingMerchant.setPhone(merchant.getPhone());
        existingMerchant.setAddress(merchant.getAddress());
        existingMerchant.setCity(merchant.getCity());
        existingMerchant.setState(merchant.getState());
        existingMerchant.setZip(merchant.getZip());
        existingMerchant.setCountry(merchant.getCountry());
        existingMerchant.setTaxId(merchant.getTaxId());
        existingMerchant.setBusinessLicense(merchant.getBusinessLicense());
        existingMerchant.setWebsite(merchant.getWebsite());
        existingMerchant.setDescription(merchant.getDescription());
        existingMerchant.setStatus(merchant.getStatus());
        existingMerchant.setCategory(merchant.getCategory());
        return merchantRepository.save(existingMerchant);
    }

    public void deleteById( Long id) {
        if (!merchantRepository.existsById(id)) {
            throw new BusinessException("Merchant not found with id: " + id);
        }
        merchantRepository.deleteById(id);
    }
}