package hackathon.kb.chakchak.global.s3.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3StorageService {

	private final AmazonS3 s3Client;
	private final String bucketName;

	private static final String XLSX_APPLICATION_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public S3StorageService(AmazonS3 s3Client, @Value("${bucket_name}") String bucketName) {
		this.s3Client = s3Client;
		this.bucketName = bucketName;
	}

	/**
	 * 단일 이미지 업로드
	 */
	public URL uploadImages(MultipartFile file) {
		String originalFilename = StringUtils.hasText(file.getOriginalFilename())
			? file.getOriginalFilename()
			: UUID.randomUUID().toString();

		String key = validateFileName(originalFilename, bucketName);
		log.info("originalFilename: {}", originalFilename);
		log.info("resolved key: {}", key);

		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());
			log.info("contentType: {}", file.getContentType());

			s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
			return s3Client.getUrl(bucketName, key);

		} catch (IOException e) {
			throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
		}
	}

	/**
	 * 다중 이미지 업로드 (prefix 없이)
	 */
	public List<URL> uploadImages(List<MultipartFile> files) {
		return uploadImages(files, null);
	}

	/**
	 * 다중 이미지 업로드 (S3 key에 prefix 적용 가능)
	 * 예) dirPrefix = "uploads/img" → uploads/img/{filename}
	 */
	public List<URL> uploadImages(List<MultipartFile> files, String dirPrefix) {
		if (files == null || files.isEmpty()) {
			throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
		}

		final List<URL> uploadedUrls = new ArrayList<>();
		final List<String> uploadedKeys = new ArrayList<>();
		final String prefix = (dirPrefix == null || dirPrefix.isBlank())
			? ""
			: dirPrefix.replaceAll("^/+", "").replaceAll("/+$", "") + "/";

		try {
			for (MultipartFile file : files) {
				if (file == null || file.isEmpty()) {
					throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
				}

				String originalFilename = StringUtils.hasText(file.getOriginalFilename())
					? file.getOriginalFilename()
					: UUID.randomUUID().toString();

				String key = validateFileName(prefix + originalFilename, bucketName);

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(file.getSize());
				metadata.setContentType(file.getContentType());

				s3Client.putObject(bucketName, key, file.getInputStream(), metadata);

				uploadedKeys.add(key);
				uploadedUrls.add(s3Client.getUrl(bucketName, key));
			}

			return uploadedUrls;

		} catch (IOException | RuntimeException e) {
			// 부분 성공 방지: 이미 업로드된 객체 롤백
			for (String key : uploadedKeys) {
				try {
					s3Client.deleteObject(bucketName, key);
				} catch (Exception ignore) {}
			}
			throw new BusinessException(ResponseCode.S3_UPLOAD_FAIL);
		}
	}

	public URL getImageUrl(String key) {
		try {
			return s3Client.getUrl(bucketName, key);
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.S3_NOT_FOUND);
		}
	}

	public void deleteImage(String key) {
		try {
			s3Client.deleteObject(bucketName, key);
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.S3_NOT_FOUND);
		}
	}

	private String validateFileName(String key, String bucketName) {
		if (s3Client.doesObjectExist(bucketName, key) || !StringUtils.hasText(key)) {
			log.info("getFileExtension 테스트 {}", getFileExtension(key));
			return UUID.randomUUID() + getFileExtension(key);
		}
		return key;
	}

	private String getFileExtension(String key) {
		int dotIndex = key.lastIndexOf(".");
		if (dotIndex > 0 && dotIndex < key.length() - 1) {
			return key.substring(dotIndex);
		}
		return ""; // 확장자가 없는 경우 빈 문자열 반환
	}
}
