package com.dvcs.gilbertcleanup.neighborhoods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.vividsolutions.jts.geom.Coordinate;

public class NeighborhoodUtil {

	private Context ctx;

	public NeighborhoodUtil(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * @param coord
	 * @return A neighborhood which contains the coordinate, or `null` if no
	 *         such neighborhood exists.
	 */
	public Neighborhood findNeighborhoodForCoordinate(Coordinate coord) {
		Neighborhood[] neighborhoods = null;
		try {
			neighborhoods = getNeighborhoods();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Neighborhood n : neighborhoods) {
			if (n.contains(coord))
				return n;
		}

		return null;
	}

	// TODO: Cache?
	private Neighborhood[] getNeighborhoods() throws IOException,
			XmlPullParserException {
		InputStream is = ctx.getAssets().open("gilbert_neighborhoods.kml");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(is, "UTF-8");

		ArrayList<Neighborhood> neighborhoods = new ArrayList<Neighborhood>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			
			if (parser.getName().equals("Placemark")) {
				neighborhoods.add(readPlacemark(parser));
			}
		}

		return (Neighborhood[]) neighborhoods.toArray();
	}

	private Neighborhood readPlacemark(XmlPullParser p)
			throws XmlPullParserException, IOException {
		p.require(XmlPullParser.START_TAG, null, "Placemark");

		String name = null;
		Coordinate[] outerBoundary = null;
		ArrayList<Coordinate[]> innerBoundaries = new ArrayList<Coordinate[]>();

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG)
				continue;

			String tagName = p.getName();
			if (tagName.equals("name")) {
				name = readName(p);
			} else if (tagName.equals("outerBoundaryIs")) {
				outerBoundary = readBoundary(p);
			} else if (tagName.equals("innerBoundaryIs")) {
				innerBoundaries.add(readBoundary(p));
			} else {
				skip(p);
			}
		}

		return new Neighborhood(name, outerBoundary,
				(Coordinate[][]) innerBoundaries.toArray());
	}

	private String readName(XmlPullParser p) throws XmlPullParserException,
			IOException {
		p.require(XmlPullParser.START_TAG, null, "name");
		String name = readText(p);
		p.require(XmlPullParser.END_TAG, null, "name");
		return name;
	}

	private Coordinate[] readBoundary(XmlPullParser p)
			throws XmlPullParserException, IOException {
		Coordinate[] ret = null;

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG)
				continue;

			String tagName = p.getName();
			if (tagName.equals("coordinates")) {
				ret = readCoordinates(p);
			}
		}

		return ret;
	}

	private Coordinate[] readCoordinates(XmlPullParser p)
			throws XmlPullParserException, IOException {
		p.require(XmlPullParser.START_TAG, null, "coordinates");
		String coordString = readText(p);
		p.require(XmlPullParser.END_TAG, null, "coordinates");

		String[] coordinateTriples = coordString.split(" ");
		Coordinate[] ret = new Coordinate[coordinateTriples.length];
		int i = 0;
		for (String triple : coordinateTriples) {
			String[] elements = triple.split(",");
			ret[i] = new Coordinate(Double.parseDouble(elements[0]),
					Double.parseDouble(elements[1]),
					Double.parseDouble(elements[2]));
			
			i++;
		}
		
		return ret;
	}

	private String readText(XmlPullParser p) throws XmlPullParserException,
			IOException {
		String result = "";
		if (p.next() == XmlPullParser.TEXT) {
			result = p.getText();
			p.nextTag();
		}

		return result;
	}
	
	private void skip(XmlPullParser p) throws XmlPullParserException, IOException {
		if (p.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		
		int depth = 1;
		while ( depth != 0 ) {
			switch ( p.next() ) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
