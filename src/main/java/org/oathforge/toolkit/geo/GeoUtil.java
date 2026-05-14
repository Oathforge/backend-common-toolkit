package org.oathforge.toolkit.geo;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import org.oathforge.toolkit.enums.ExceptionEnum;
import org.oathforge.toolkit.exception.ConflictException;

/**
 * Utility component for converting between WKT representations and JTS
 * {@link Point} values.
 */
@Component
@ConditionalOnProperty(prefix = "backend-toolkit.geo", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GeoUtil {

	/**
	 * Parses a WKT point into a JTS {@link Point}.
	 * <p>
	 * If the input is {@code null} or blank, the method returns {@code null}. If
	 * the value is not a valid WKT point, a toolkit exception is raised.
	 *
	 * @param wkt WKT point representation
	 * @return parsed point or {@code null} when the input is blank
	 */
	public Point fromWKT(String wkt) {
		if (wkt == null || wkt.isEmpty()) {
			return null;
		}
		WKTReader reader = new WKTReader();
		try {
			return (Point) reader.read(wkt);
		} catch (ParseException e) {
			throw new ConflictException(ExceptionEnum.GEO0001.name(), ExceptionEnum.GEO0001.getValue().formatted(wkt));
		}
	}

	/**
	 * Serializes a JTS {@link Point} into WKT format.
	 *
	 * @param point point to serialize
	 * @return WKT representation or {@code null} when the input is {@code null}
	 */
	public String toWKT(Point point) {
		if (point == null) {
			return null;
		}
		WKTWriter writer = new WKTWriter();
		return writer.write(point);
	}
}
