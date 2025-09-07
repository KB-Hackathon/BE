package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductProgressResponseDto getProgressByProductId(Long productId) {

//        if (!productRepository.existsById(productId)) {
//            throw new BusinessException(ResponseCode.PRODUCT_NOT_FOUND);
//        }

        return productRepository.findProgressByProductId(productId);
    }
}
