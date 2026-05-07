package com.backendtoolkit.common.assetdelivery;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DirectAssetDelivery implements AssetDelivery {

	private final Function<String, String> urlResolver;

	@Override
	public String getFileUrlByKey(String key) {
		return urlResolver.apply(key);
	}

	@Override
	public void invalidateByKey(String key) {
		// No public cache layer to invalidate.
	}
}
