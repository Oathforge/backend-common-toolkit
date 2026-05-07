# Entrega Publica de Assets

## Para que sirve

`AssetDelivery` resuelve una necesidad simple: transformar la `key` interna de un fichero en la URL publica que debe consumir el cliente.

Ejemplo:
- key en storage: `users/avatar-123.png`
- URL publica: `https://cdn.example.com/users/avatar-123.png`

Ademas, si hay una CDN delante, tambien puede invalidar cache cuando el asset cambia.

## Que hace y que no hace

Hace:
- convertir `key -> URL publica`
- invalidar cache si el proveedor lo soporta

No hace:
- subir ficheros
- borrar ficheros del storage
- generar presigned URLs

En corto:
- `storage` = donde vive el fichero
- `asset delivery` = como se expone publicamente

## Interfaz

```java
public interface AssetDelivery {

  String getFileUrlByKey(String key);

  void invalidateByKey(String key);
}
```

## Uso tipico

Lo normal es guardar en base de datos solo la `key` y devolver por API la URL publica.

```java
public MediaResponse toDto(MediaAsset entity, AssetDelivery assetDelivery) {
  return new MediaResponse(
      entity.getId(),
      assetDelivery.getFileUrlByKey(entity.getStorageKey())
  );
}
```

## Implementaciones incluidas

### `DirectAssetDelivery`

Construye la URL directamente a partir de una funcion.

```java
AssetDelivery delivery = new DirectAssetDelivery(
    key -> "https://cdn.example.com/assets/" + key
);
```

Util cuando no necesitas invalidacion activa.

### `CloudFrontAssetDelivery`

Construye la URL usando una base de CloudFront y permite invalidar cache.

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

Construye la URL usando una base de Edge Services.

```java
AssetDelivery delivery = new EdgeServicesAssetDelivery(
    "https://cdn.example.com",
    "pipeline-id"
);
```

La invalidacion aun no esta implementada en esta libreria.

## Resumen

Si tu servicio trabaja con ficheros y guarda una `key`, `AssetDelivery` es la pieza que traduce esa `key` a una URL publica y, cuando aplica, invalida la cache de la capa publica.
