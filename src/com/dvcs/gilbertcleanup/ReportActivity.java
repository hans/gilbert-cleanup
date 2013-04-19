package com.dvcs.gilbertcleanup;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;

public class ReportActivity extends Activity {

	private static final int CAMERA_REQUEST_CODE = 1;

	ArrayList<Bitmap> pictures;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		// Show the Up button in the action bar.
		setupActionBar();

		pictures = new ArrayList<Bitmap>();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void launchCamera(View v) {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent data) {
		if ( requestCode == CAMERA_REQUEST_CODE ) {
			pictures.add((Bitmap) data.getExtras().get("data"));
		}
	}

	private Location getLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = lm.getBestProvider(new Criteria(), false);
		Location ret = lm.getLastKnownLocation(provider);

		return ret;
	}

	public void submitForm(View v) {
		String title = ((TextView) findViewById(R.id.title)).getText()
				.toString();
		String description = ((TextView) findViewById(R.id.description))
				.getText().toString();

		int urgency = 0;
		int urgencyId = ((RadioGroup) findViewById(R.id.urgency))
				.getCheckedRadioButtonId();
		switch ( urgencyId ) {
		case R.id.urgencyLow:
			urgency = 0;
			break;
		case R.id.urgencyMedium:
			urgency = 1;
			break;
		case R.id.urgencyHigh:
			urgency = 2;
			break;
		}

		new AddIssueTask().execute(this, title, description, urgency,
				pictures.toArray(new Bitmap[] {}), getLocation());
	}

	/**
	 * Task which submits a new issue.
	 * 
	 * The parameters of this task are expected in this order:
	 * 
	 * - Context ctx - String title - String description - int urgency -
	 * Bitmap[] pictures - Location location
	 */
	private class AddIssueTask extends AsyncTask<Object, Void, Boolean> {
		protected Boolean doInBackground(Object... params) {
			assert params.length == 5;

			return HeroesOfGilbert.submitIssue((Context) params[0],
					(String) params[1], (String) params[2],
					(Integer) params[3], (Bitmap[]) params[4],
					(Location) params[5]);
		}

		protected void onPostExecute(Boolean result) {
			if ( !result.booleanValue() ) {
				MessageDialog
						.show(ReportActivity.this,
								R.drawable.superhero_helpful,
								"The issue submission failed. Are you connected to the Internet?");
			}
		}
	}

}
