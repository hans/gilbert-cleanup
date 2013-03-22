package com.dvcs.gilbertcleanup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

	}

	/**
	 * Task which submits a new issue.
	 * 
	 * The parameters of this task are expected in this order:
	 * 
	 * - Context ctx - String title - String description - int urgency -
	 * Bitmap[] pictures
	 */
	private class AddIssueTask extends AsyncTask<Object, Void, Void> {
		protected Void doInBackground(Object... params) {
			assert params.length == 5;

			HeroesOfGilbert.submitIssue((Context) params[0],
					(String) params[1], (String) params[2], (Integer) params[3],
					(Bitmap[]) params[4]);

			return null;
		}
	}

}