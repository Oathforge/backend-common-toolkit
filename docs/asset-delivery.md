# Public Asset Delivery

## Purpose

`AssetDelivery` solves a simple problem: turning the internal file `key` into the public URL that the client should consume.

Example:
- storage key: `users/avatar-123.png`
- public URL: `https://cdn.example.com/users/avatar-123.png`

In addition, if there is a CDN in front, it can also invalidate cache when the asset changes.

## What it does and what it does not do

It does:
- convert `key -> public URL`
- invalidate cache if the provider supports it

It does not do:
- upload files
- delete files from storage
- generate presigned URLs

In short:
- `storage` = where the file lives
- `asset delivery` = how it is exposed publicly

## Interface

```java
public interface AssetDelivery {

  String getFileUrlByKey(String key);

  void invalidateByKey(String key);
}
```

## Typical usage

The usual approach is to store only the `key` in the database and return the public URL through the API.

```java
public MediaResponse toDto(MediaAsset entity, AssetDelivery assetDelivery) {
  return new MediaResponse(
      entity.getId(),
      assetDelivery.getFileUrlByKey(entity.getStorageKey())
  );
}
```

## Included implementations

### `DirectAssetDelivery`

Builds the URL directly from a function.

```java
AssetDelivery delivery = new DirectAssetDelivery(
    key -> "https://cdn.example.com/assets/" + key
);
```

Useful when you do not need active invalidation.

### `CloudFrontAssetDelivery`

Builds the URL from a CloudFront base URL and allows cache invalidation.

```java
AssetDelivery delivery = new CloudFrontAssetDelivery(
    "https://d123.cloudfront.net",
    "E123456789",
    client,
    "AWS0001",
    "CloudFront invalidation failed"
);
```

### `EdgeServicesAssetDelivery`

Builds the URL from an Edge Services base URL.

```java
AssetDelivery delivery = new EdgeServicesAssetDelivery(
    "https://cdn.example.com",
    "pipeline-id"
);
```

Invalidation is not implemented yet in this library.

## Summary

If your service works with files and stores a `key`, `AssetDelivery` is the piece that translates that `key` into a public URL and, when applicable, invalidates the cache of the public delivery layer.
