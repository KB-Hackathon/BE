package hackathon.kb.chakchak.domain.product.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hackathon.kb.chakchak.domain.member.domain.entity.Seller;
import hackathon.kb.chakchak.domain.order.domain.entity.Order;
import hackathon.kb.chakchak.domain.product.domain.dto.ImageReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductMemberReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.ProductReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.dto.TagReadResponseDto;
import hackathon.kb.chakchak.domain.product.domain.entity.Image;
import hackathon.kb.chakchak.domain.product.domain.entity.Product;
import hackathon.kb.chakchak.domain.product.domain.entity.Tag;

public class ProductToDtoMapper {

	public static ProductReadResponseDto productToProductReadResponseDto(Product product) {
		Short presentPersonCount = getPresentPersonCount(product.getOrders());
		return ProductReadResponseDto.builder()
			.productId(product.getId())
			.category(product.getCategory())
			.description(product.getDescription())
			.price(product.getPrice())
			.title(product.getTitle())
			.tags(TagToTagReadResponseDtoList(product.getTags()))
			.images(ImageToImageReadResponseDto(product.getImages()))
			.recruitmentEndPeriod(product.getRecruitmentEndPeriod())
			.recruitmentStartPeriod(product.getRecruitmentStartPeriod())
			.seller(memberToProductMemberReadResponseDto(product.getSeller()))
			.targetAmount(product.getTargetAmount())
			.presentPersonCount(presentPersonCount)
			.totalPrice(getTotalPrice(presentPersonCount, product.getPrice()))
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
}
