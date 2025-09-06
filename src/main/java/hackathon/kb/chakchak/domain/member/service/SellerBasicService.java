package hackathon.kb.chakchak.domain.member.service;

import hackathon.kb.chakchak.domain.member.api.dto.res.SellerReadResponseDto;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.member.repository.SellerRepository;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SellerBasicService {
    private final SellerRepository sellerRepository;

    public SellerReadResponseDto getSellerById(Long id) {
        Seller seller = sellerRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.SELLER_NOT_FOUND));

        return ProductToDtoMapper.sellerToSellerProductsResponseDto(seller);
    }
}
