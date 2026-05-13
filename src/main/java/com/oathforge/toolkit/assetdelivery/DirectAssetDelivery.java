package com.oathforge.toolkit.assetdelivery;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

/**
 * {@link AssetDelivery} implementation that resolves public URLs directly from
 * the provided key resolver.
 * <p>
 * This implementation is intended for scenarios where asset exposure is purely
 * deterministic and there is no cache invalidation workflow to execute.
 */
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
