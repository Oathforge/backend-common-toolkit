package com.backendtoolkit.common.assetdelivery;

public interface AssetDelivery {

	String getFileUrlByKey(String key);

	void invalidateByKey(String key);
}
