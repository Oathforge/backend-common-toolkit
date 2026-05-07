# Geo Util Opcional

## Para que sirve

`GeoUtil` aporta utilidades basicas para convertir geometria en formato WKT a `Point` y viceversa.

Ejemplo:
- entrada WKT: `POINT (-3.70379 40.41678)`
- salida: objeto `Point`

## Que hace

- `fromWKT(String wkt)`: convierte un WKT a `Point`
- `toWKT(Point point)`: convierte un `Point` a WKT

## Activacion

```yml
backend-toolkit:
  geo:
    enabled: true
```

Por defecto esta desactivado.

## Ejemplo de uso

```java
@Service
@RequiredArgsConstructor
public class LocationService {

  private final GeoUtil geoUtil;

  public String normalizePoint(String wkt) {
    Point point = geoUtil.fromWKT(wkt);
    return geoUtil.toWKT(point);
  }
}
```

## Importante

Este bloque no depende de PostgreSQL, MySQL, Oracle o Mongo como motor concreto. La utilidad solo trabaja con conversiones WKT usando JTS.

Es decir:
- no es una funcionalidad atada a un proveedor de base de datos
- no necesita `hibernate-spatial` para funcionar
- puede convivir con proyectos relacionales o no relacionales

## Resumen

Si tu proyecto necesita manejar puntos geograficos en WKT, puedes activar `GeoUtil`. Si no lo necesita, el bloque permanece apagado y no afecta al arranque.
