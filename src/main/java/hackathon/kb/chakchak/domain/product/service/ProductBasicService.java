package hackathon.kb.chakchak.domain.product.service;

import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.enums.Category;
import hackathon.kb.chakchak.domain.product.domain.enums.ProductStatus;
import hackathon.kb.chakchak.domain.product.repository.ProductRepository;
import hackathon.kb.chakchak.domain.product.util.ProductToDtoMapper;
import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductBasicService {

	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public ProductReadResponseDto getProductById(Long id) {
		Product product = productRepository.findByIdWithSeller(id)
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

		return ProductToDtoMapper.productToProductReadResponseDto(product);
	}

	@Transactional(readOnly = true)
	public List<ProductReadResponseDto> getProductsByOptions(
			Category category,
			ProductStatus status,
			Boolean isCoupon,
			int page) {
		Pageable pageable = PageRequest.of(
			page,
			10,
			Sort.by(Sort.Direction.DESC, "createdAt", "id")
		);

		return productRepository
			.findByOptions(category, status, isCoupon, pageable)
			.getContent()
			.stream()
			.map(ProductToDtoMapper::productToProductReadResponseDto)
			.collect(Collectors.toList());
	}

}
