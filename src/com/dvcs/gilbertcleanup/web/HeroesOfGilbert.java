package com.dvcs.gilbertcleanup.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dvcs.gilbertcleanup.Issue;
import com.vividsolutions.jts.geom.Coordinate;

public class HeroesOfGilbert {

	private static final String ENDPOINT_URL = "http://heroes-of-gilbert.appspot.com/";

	private static final String ROUTE_ISSUES = "issues";

	/**
	 * Get a list of recent issues.
	 * 
	 * @return A list of issues, or `null` if there was an error during
	 *         retrieval.
	 */
	public static Issue[] getIssues() {
		JSONArray issueJson = null;
		try {
			issueJson = getJSONArray(ROUTE_ISSUES);
		} catch (Exception e) {
			return null;
		}

		Issue[] ret = new Issue[issueJson.length()];
		for (int i = 0; i < ret.length; i++) {
			Issue issue = null;
			try {
				JSONObject obj = issueJson.getJSONObject(i);

				JSONArray picturesJson = obj.getJSONArray("pictures");
				URL[] pictures = new URL[picturesJson.length()];
				for (int j = 0; j < pictures.length; j++) {
					pictures[j] = new URL(picturesJson.getString(j));
				}

				JSONObject locationJson = obj.getJSONObject("location");
				Coordinate location = locationJson == null ? null
						: new Coordinate(locationJson.getDouble("lat"),
								locationJson.getDouble("lon"));

				issue = new Issue(obj.getString("title"),
						obj.getString("description"),
						obj.getString("reporter"), pictures, location,
						obj.getInt("key"), obj.getLong("time"),
						obj.getInt("urgency"));
			} catch (JSONException e) {
				return null;
			} catch (MalformedURLException e) {
				return null;
			}

			ret[i] = issue;
		}

		return ret;
	}

	/**
	 * Send a GET request to an API endpoint.
	 * 
	 * @param route
	 *            Route to the endpoint (without leading or trailing slash)
	 * @return Response string, or null if the request failed
	 */
	private static String get(String route) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(ENDPOINT_URL + route);

		HttpResponse response = client.execute(get);
		HttpEntity responseEntity = response.getEntity();

		if (responseEntity == null)
			return null;

		String responseString = convertStreamToString(responseEntity
				.getContent());
		return responseString;
	}

	/**
	 * Send a GET request to an API endpoint which returns a JSON object.
	 * 
	 * @param route
	 *            Route to the endpoint (without leading or trailing slash)
	 * @return Parsed JSON object, or null if the request failed
	 */
	private static JSONObject getJSONObject(String route) throws IOException,
			JSONException {
		String response = get(route);
		return new JSONObject(response);
	}

	/**
	 * Sent a GET request to an API endpoint which returns a JSON array.
	 * 
	 * @param route
	 *            Route to the endpoint (without leading or trailing slash)
	 * @return Parsed JSON array, or null if the request failed
	 */
	private static JSONArray getJSONArray(String route) throws IOException,
			JSONException {
		String response = get(route);
		return new JSONArray(response);
	}

	private static String convertStreamToString(InputStream stream)
			throws IOException {
		if (stream == null)
			return "";

		Writer writer = new StringWriter();
		Reader reader = new BufferedReader(new InputStreamReader(stream,
				"UTF-8"), 1024);
		char[] buffer = new char[1024];

		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}

		stream.close();
		return writer.toString();
	}

}
