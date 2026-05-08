# Optional Geo Util

## Purpose

`GeoUtil` provides basic utilities to convert WKT geometry into `Point` objects and vice versa.

Example:
- WKT input: `POINT (-3.70379 40.41678)`
- output: `Point` object

## What it does

- `fromWKT(String wkt)`: converts WKT into a `Point`
- `toWKT(Point point)`: converts a `Point` into WKT

## Activation

```yml
backend-toolkit:
  geo:
    enabled: true
```

It is disabled by default.

## Usage example

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

## Important

This block does not depend on PostgreSQL, MySQL, Oracle, or Mongo as a specific engine. The utility only works with WKT conversions using JTS.

In other words:
- it is not tied to a database vendor
- it does not require `hibernate-spatial` to work
- it can coexist with relational and non-relational projects

## Summary

If your project needs to handle geographic points in WKT, you can enable `GeoUtil`. If not, the block remains disabled and does not affect startup.
