package com.dvcs.gilbertcleanup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;
import com.sqisland.android.swipe_image_viewer.SwipeImageViewerActivity;

/**
 * An activity representing a single Issue detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link IssueListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link IssueDetailFragment}.
 */
public class IssueDetailActivity extends FragmentActivity {

	/**
	 * A shared array modified by the detail fragment and accessed by the image
	 * slider view.
	 */
	public static Drawable[] pictures;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if ( savedInstanceState == null ) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(IssueDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(IssueDetailFragment.ARG_ITEM_ID));
			IssueDetailFragment fragment = new IssueDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.issue_detail_container, fragment).commit();
		}
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
			NavUtils.navigateUpTo(this, new Intent(this,
					IssueListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onCommentActivity(View v)
	{
		int id = Integer.parseInt(getIntent().getStringExtra(IssueDetailFragment.ARG_ITEM_ID));
		String txt = ((EditText)v.getRootView().findViewById(R.id.editText1)).getText().toString();
		HeroesOfGilbert.submitComment(this.getApplicationContext(), id-1, txt);
	}

	public void onImageClick(View v) {
		Intent i = new Intent(this, SwipeImageViewerActivity.class);
		startActivity(i);
	}
}
