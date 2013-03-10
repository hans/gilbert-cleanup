package com.dvcs.gilbertcleanup.neighborhoods;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Neighborhood {

	private final String name;

	private final static GeometryFactory gf = new GeometryFactory();
	private final Polygon polygon;

	public Neighborhood(String name, Coordinate[] outerBoundaryCoords,
			Coordinate[][] innerBoundaryCoords) {
		LinearRing[] innerBoundaries = new LinearRing[innerBoundaryCoords.length];
		for (int i = 0; i < innerBoundaryCoords.length; i++) {
			innerBoundaries[i] = new LinearRing(new CoordinateArraySequence(
					innerBoundaryCoords[i]), gf);
		}

		this.name = name;
		this.polygon = new Polygon(new LinearRing(new CoordinateArraySequence(
				outerBoundaryCoords), gf), innerBoundaries, gf);
	}

	public boolean contains(Coordinate coord) {
		return gf.createPoint(coord).within(polygon);
	}

	public String getName() {
		return name;
	}

}
