package com.backendtoolkit.common.assetdelivery;

import com.backendtoolkit.common.exception.ServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;

@Slf4j
@RequiredArgsConstructor
public class CloudFrontAssetDelivery implements AssetDelivery {

	private final String publicBaseUrl;

	private final String distributionId;

	private final CloudFrontClient cloudFrontClient;

	private final String errorCode;

	private final String errorMessage;

	@Override
	public String getFileUrlByKey(String key) {
		String baseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
		return baseUrl + key;
	}

	@Override
	public void invalidateByKey(String key) {
		try {
			cloudFrontClient.createInvalidation(CreateInvalidationRequest.builder()
					.distributionId(distributionId)
					.invalidationBatch(batch -> batch
							.paths(paths -> paths.quantity(1).items("/" + key))
							.callerReference(String.valueOf(System.currentTimeMillis())))
					.build());
		} catch (CloudFrontException ex) {
			log.error("Error invalidating CloudFront object {}", key, ex);
			throw new ServiceUnavailableException(errorCode, errorMessage);
		}
	}
}
