package com.dvcs.gilbertcleanup.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;

import com.dvcs.gilbertcleanup.Issue;
import com.vividsolutions.jts.geom.Coordinate;

public class HeroesOfGilbert {

	private static final String ENDPOINT_URL = "http://heroes-of-gilbert.herokuapp.com/";

	private static final String ROUTE_ISSUES = "issues";
	private static final String ROUTE_ISSUE_ADD = "issues/add";

	/**
	 * Get a list of recent issues.
	 * 
	 * @return A list of issues, or `null` if there was an error during
	 *         retrieval.
	 */
	public static Issue[] getIssues() {
		JSONArray issueJson = null;
		try {
			JSONObject apiRet = getJSONObject(ROUTE_ISSUES);
			issueJson = apiRet.getJSONArray("issues");
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}

		Issue[] ret = new Issue[issueJson.length()];
		for ( int i = 0; i < ret.length; i++ ) {
			Issue issue = null;
			try {
				JSONObject obj = issueJson.getJSONObject(i);

				JSONArray picturesJson = obj.getJSONArray("pictures");
				URL[] pictures = new URL[picturesJson.length()];
				for ( int j = 0; j < pictures.length; j++ ) {
					pictures[j] = new URL(picturesJson.getString(j));
				}

				JSONObject locationJson = obj.optJSONObject("location");
				Coordinate location = locationJson == null ? null
						: new Coordinate(locationJson.getDouble("lat"),
								locationJson.getDouble("lon"));

				issue = new Issue(obj.getString("title"),
						obj.getString("description"),
						obj.getString("reporter"), pictures, location,
						obj.getInt("key"), obj.getLong("time"),
						obj.getInt("urgency"));
			} catch ( JSONException e ) {
				e.printStackTrace();
				return null;
			} catch ( MalformedURLException e ) {
				e.printStackTrace();
				return null;
			}

			ret[i] = issue;
		}

		return ret;
	}

	/**
	 * Submit a new issue.
	 */
	public static void submitIssue(Context ctx, String title,
			String description, int urgency, Bitmap[] pictures) {
		String guid = getDeviceGUID(ctx);

		final SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ",
				ctx.getResources().getConfiguration().locale);
		String dateString = sdf.format(new Date());

		String url = ENDPOINT_URL + ROUTE_ISSUE_ADD;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			int i = 0;
			for ( Bitmap picture : pictures ) {
				byte[] pictureBytes = convertBitmapToByteArray(picture);

				// TODO relies on the fact that we compress to PNG in previous
				// step
				entity.addPart("pictures[]", new ByteArrayBody(pictureBytes,
						"bam" + i + ".png"));

				i++;
			}

			// Add fields
			entity.addPart("user", new StringBody(guid));
			entity.addPart("time", new StringBody(dateString));
			entity.addPart("title", new StringBody(title));
			entity.addPart("description", new StringBody(description));
			entity.addPart("urgency", new StringBody(String.valueOf(urgency)));

			post.setEntity(entity);
			client.execute(post);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
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

		if ( responseEntity == null )
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

	private static String getDeviceGUID(Context ctx) {
		TelephonyManager tManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}

	private static byte[] convertBitmapToByteArray(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		// TODO: Should we just copy instead?
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	private static String convertStreamToString(InputStream stream)
			throws IOException {
		if ( stream == null )
			return "";

		Writer writer = new StringWriter();
		Reader reader = new BufferedReader(new InputStreamReader(stream,
				"UTF-8"), 1024);
		char[] buffer = new char[1024];

		int n;
		while ( (n = reader.read(buffer)) != -1 ) {
			writer.write(buffer, 0, n);
		}

		stream.close();
		return writer.toString();
	}

}
