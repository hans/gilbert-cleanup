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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.telephony.TelephonyManager;

import com.dvcs.gilbertcleanup.models.Comment;
import com.dvcs.gilbertcleanup.models.ExtendedIssue;
import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.models.User;
import com.vividsolutions.jts.geom.Coordinate;

public class HeroesOfGilbert {

	private static final String ENDPOINT_URL = "http://heroes-of-gilbert.herokuapp.com/";

	private static final String ROUTE_ISSUES = "issues";
	private static final String ROUTE_ISSUE_ADD = "issues/add";
	private static final String ROUTE_ISSUE_STATUS = "issues/%d/status";
	private static final String ROUTE_ISSUE_COMMENT_ADD = "issues/%d/comment/add";

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
	 * Get detailed information about the given issue.
	 * 
	 * @return An object describing the issue with the corresponding key, or
	 *         `null` if such an issue could not be found.
	 */
	public static ExtendedIssue getIssue(int key) {
		JSONObject issueJson = null;
		try {
			JSONObject apiRet = getJSONObject(ROUTE_ISSUES + "/" + key);
			issueJson = apiRet.getJSONObject("issue");
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}

		Comment[] comments = getCommentsFromIssueJson(issueJson);
		if ( comments == null )
			return null;

		ExtendedIssue issue;
		try {
			JSONObject userJson = issueJson.getJSONObject("reporter");
			User user = new User(userJson.getInt("key"),
					userJson.getInt("status"), userJson.getString("username"),
					null);

			JSONArray picturesJson = issueJson.getJSONArray("pictures");
			URL[] pictures = new URL[picturesJson.length()];
			for ( int j = 0; j < pictures.length; j++ ) {
				pictures[j] = new URL(picturesJson.getString(j));
			}

			JSONObject locationJson = issueJson.optJSONObject("location");
			Coordinate location = locationJson == null ? null : new Coordinate(
					locationJson.getDouble("lat"),
					locationJson.getDouble("lon"));

			issue = new ExtendedIssue(issueJson.getString("title"),
					issueJson.getString("description"), user, pictures,
					location, issueJson.getInt("key"),
					issueJson.getLong("time"), issueJson.getInt("urgency"),
					comments);
		} catch ( JSONException e ) {
			e.printStackTrace();
			return null;
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
			return null;
		}

		return issue;
	}

	private static Comment[] getCommentsFromIssueJson(JSONObject issueJson) {
		JSONArray commentJson;
		try {
			commentJson = issueJson.getJSONArray("comments");
		} catch ( JSONException e ) {
			e.printStackTrace();
			return null;
		}

		Comment[] comments = new Comment[commentJson.length()];
		for ( int i = 0; i < comments.length; i++ ) {
			Comment comment = null;

			try {
				JSONObject obj = commentJson.getJSONObject(i);
				JSONObject comAuth = obj.getJSONObject("author");
				User commentAuthor = new User(comAuth.getInt("key"),
						comAuth.getInt("status"),
						comAuth.getString("username"), null);

				comment = new Comment(commentAuthor, obj.getString("text"),
						obj.getLong("time"), obj.getInt("key"),
						obj.getInt("issue"));
			} catch ( JSONException e ) {
				e.printStackTrace();
				return null;
			}
			comments[i] = comment;
		}

		return comments;
	}

	/**
	 * Submit a new issue.
	 */
	public static boolean submitIssue(Context ctx, String title,
			String description, int urgency, Bitmap[] pictures,
			Location location) {
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

			if ( location != null ) {
				entity.addPart("location_lat",
						new StringBody(String.valueOf(location.getLatitude())));
				entity.addPart("location_lon",
						new StringBody(String.valueOf(location.getLongitude())));
			}

			post.setEntity(entity);
			client.execute(post);
		} catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Submit comment to issue
	 * @return 
	 */
	public static boolean submitComment(Context ctx, int issueKey, String text) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String guid = getDeviceGUID(ctx);
		params.add(new BasicNameValuePair("author", guid));
		params.add(new BasicNameValuePair("text", text));
		
		String route = String.format(ROUTE_ISSUE_COMMENT_ADD, issueKey);
		
		try {
			post(route, params);
		} catch( IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Update an issue's status.
	 */
	public void updateIssueStatus(int issueKey, int status) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("status", String.valueOf(status)));

		String route = String.format(ROUTE_ISSUE_ADD, issueKey);

		try {
			post(route, params);
		} catch ( IOException e ) {
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

	/**
	 * Send a POST request to an API endpoint.
	 * 
	 * @param route
	 *            Route to the endpoint (without leading or trailing slash)
	 * @param params
	 * @return Response string, or null if the request failed
	 */
	private static String post(String route, List<NameValuePair> params)
			throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(ENDPOINT_URL + route);
		post.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(post);
		HttpEntity responseEntity = response.getEntity();

		if ( responseEntity == null )
			return null;

		String responseString = convertStreamToString(responseEntity
				.getContent());
		return responseString;
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
