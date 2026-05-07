package com.backendtoolkit.common.geo;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.backendtoolkit.common.enums.ExceptionEnum;
import com.backendtoolkit.common.exception.ConflictException;

@Component
@ConditionalOnProperty(prefix = "backend-toolkit.geo", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GeoUtil {

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

	public String toWKT(Point point) {
		if (point == null) {
			return null;
		}
		WKTWriter writer = new WKTWriter();
		return writer.write(point);
	}
}
