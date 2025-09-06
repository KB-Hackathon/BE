package hackathon.kb.chakchak.domain.product.util;

import hackathon.kb.chakchak.domain.member.api.dto.res.SellerReadResponseDto;
import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.api.dto.ProductProgressResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.*;
import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductToDtoMapper {

	public static ProductReadResponseDto productToProductReadResponseDto(Product product) {
		return ProductReadResponseDto.builder()
				.seller(memberToProductMemberReadResponseDto(product.getSeller()))
				.product(productToProductSimpleResponseDto(product))
				.build();
	}

	public static ProductSimpleResponseDto productToProductSimpleResponseDto(Product product) {
		Short presentPersonCount = getPresentPersonCount(product.getOrders());
		return ProductSimpleResponseDto.builder()
				.productId(product.getId())
				.category(product.getCategory())
				.description(product.getDescription())
				.price(product.getPrice())
				.title(product.getTitle())
				.tags(TagToTagReadResponseDtoList(product.getTags()))
				.images(ImageToImageReadResponseDto(product.getImages()))
				.recruitmentEndPeriod(product.getRecruitmentEndPeriod())
				.recruitmentStartPeriod(product.getRecruitmentStartPeriod())
				.targetAmount(product.getTargetAmount())
				.presentPersonCount(presentPersonCount)
				.totalPrice(getTotalPrice(presentPersonCount, product.getPrice()))
				.isCoupon(product.isCoupon()) // ← 추가 추천
				.build();
	}

	public static SellerReadResponseDto sellerToSellerProductsResponseDto(Seller seller) {
		return SellerReadResponseDto.builder()
				.seller(memberToProductMemberReadResponseDto(seller))
				.products(
						seller.getProducts()
								.stream()
								.map(ProductToDtoMapper::productToProductSimpleResponseDto)
								.toList()
				)
				.build();
	}

	public static List<TagReadResponseDto> TagToTagReadResponseDtoList(List<Tag> tags) {
		List<TagReadResponseDto> tagReadResponseDtoList = new ArrayList<>();
		for (Tag tag : tags) {
			tagReadResponseDtoList.add(new TagReadResponseDto(tag.getId(), tag.getName()));
		}
		return tagReadResponseDtoList;
	}

	public static List<ImageReadResponseDto> ImageToImageReadResponseDto(List<Image> images) {
		List<ImageReadResponseDto> imageReadResponseDtoList = new ArrayList<>();
		for (Image image : images) {
			imageReadResponseDtoList.add(new ImageReadResponseDto(image.getId(), image.getUrl()));
		}
		return imageReadResponseDtoList;
	}

	public static ProductMemberReadResponseDto memberToProductMemberReadResponseDto(Seller seller) {
		return new ProductMemberReadResponseDto(seller.getId(), seller.getCompanyName(), seller.getRepName(), seller.getCompanyPhoneNumber(), seller.getRoadNameAddress());
	}

	private static Short getPresentPersonCount(List<Order> orders){
		Short presentPersonCount = 0;
		for (Order order : orders) {
			presentPersonCount = (short)(presentPersonCount + order.getQuantity());
		}
		return presentPersonCount;
	}

	private static BigDecimal getTotalPrice(Short presentPersonCount, BigDecimal price) {
		return price.multiply(new BigDecimal(presentPersonCount));
	}

	public static ProductPreviewResponseDto productToProductPreviewResponseDto(Product product, ProductProgressResponseDto progress) {
		return ProductPreviewResponseDto.builder()
				.productId(product.getId())
				.title(product.getTitle())
				.images(ImageToImageReadResponseDto(product.getImages()))
				.category(product.getCategory())
				.status(product.getStatus())
				.targetAmount(product.getTargetAmount())
				.recruitmentEndPeriod(product.getRecruitmentEndPeriod())
				.productProgressResponseDto(progress != null
						? progress
						: new ProductProgressResponseDto(product.getId(), 0L, 0) // 기본값
				)
				.build();
	}
}
