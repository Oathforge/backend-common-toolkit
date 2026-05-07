package com.backendtoolkit.common.assetdelivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EdgeServicesAssetDelivery implements AssetDelivery {

	private final String publicBaseUrl;

	private final String pipelineId;

	@Override
	public String getFileUrlByKey(String key) {
		String baseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
		return baseUrl + key;
	}

	@Override
	public void invalidateByKey(String key) {
		log.debug("Edge Services invalidation not implemented yet for pipeline {} and key {}", pipelineId, key);
	}
}
