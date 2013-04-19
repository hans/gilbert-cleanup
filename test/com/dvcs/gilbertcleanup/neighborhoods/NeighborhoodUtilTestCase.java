package com.dvcs.gilbertcleanup.neighborhoods;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParserException;

import android.test.AndroidTestCase;

import com.vividsolutions.jts.geom.Coordinate;

public class NeighborhoodUtilTestCase extends AndroidTestCase {

	private NeighborhoodUtil util;

	@Override
	public void setUp() {
		util = new NeighborhoodUtil(getContext());
	}

	public void testNeighborhoodsLoaded() throws IOException,
			XmlPullParserException {
		List<Neighborhood> neighborhoods = util.getNeighborhoods();
		Assert.assertTrue("At least one neighborhood loaded",
				neighborhoods.size() > 0);
	}

	private class NeighborhoodFixture {
		public double lat;
		public double lon;
		public String expectedNeighborhood;

		public NeighborhoodFixture(double lat, double lon,
				String expectedNeighborhood) {
			this.lat = lat;
			this.lon = lon;
			this.expectedNeighborhood = expectedNeighborhood;
		}
	}

	public void testFindNeighborhood() {
		NeighborhoodFixture[] fixtures = new NeighborhoodFixture[] {
			new NeighborhoodFixture(33.213429, -111.722679, "ACACIA"),
			new NeighborhoodFixture(33.212976, -111.736095, "ADORA TRAILS"),
			new NeighborhoodFixture(33.240117, -111.726510, "SHAMROCK ESTATES"),
			new NeighborhoodFixture(33.327401, -111.760634, "WESTERN SKIES")
		};

		for ( NeighborhoodFixture fixture : fixtures ) {
			Coordinate coord = new Coordinate(fixture.lon, fixture.lat, 0);
			Neighborhood ret = util.findNeighborhoodForCoordinate(coord);

			Assert.assertEquals("(" + fixture.lat + ", " + fixture.lon + ")",
					fixture.expectedNeighborhood, ret.getName());
		}
	}

}
