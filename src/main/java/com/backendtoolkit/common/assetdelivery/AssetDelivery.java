package com.backendtoolkit.common.assetdelivery;

/**
 * Contract for components that expose internally stored assets through a public
 * delivery layer.
 * <p>
 * Implementations typically take a storage key, translate it into a client-safe
 * public URL, and may also trigger cache invalidation when the public delivery
 * layer supports it.
 */
public interface AssetDelivery {

	/**
	 * Resolves the public URL that should be returned to clients for the provided
	 * storage key.
	 * <p>
	 * The input key is expected to be the internal identifier used by the storage
	 * layer, such as a bucket path or object key. The returned value should be a
	 * fully usable public URL.
	 *
	 * @param key internal storage key
	 * @return public URL for the asset
	 */
	String getFileUrlByKey(String key);

	/**
	 * Invalidates the public cache entry associated with the provided storage key
	 * when the underlying delivery provider supports cache invalidation.
	 * <p>
	 * Implementations that do not expose an active cache layer may safely treat
	 * this operation as a no-op.
	 *
	 * @param key internal storage key
	 */
	void invalidateByKey(String key);
}
