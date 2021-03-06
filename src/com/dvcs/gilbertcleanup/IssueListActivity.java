package com.dvcs.gilbertcleanup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;

/**
 * An activity representing a list of Issues. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link IssueDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link IssueListFragment} and the item details (if present) is a
 * {@link IssueDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link IssueListFragment.Callbacks} interface to listen for item selections.
 */
public class IssueListActivity extends FragmentActivity implements
		IssueListFragment.Callbacks {

	/**
	 * A list of issues as fetched by {@link GetIssuesTask}.
	 */
	private Issue[] issues;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Fetch the issues
		new GetIssuesTask().execute();

		setContentView(R.layout.activity_issue_list);

		if ( findViewById(R.id.issue_detail_container) != null ) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((IssueListFragment) getSupportFragmentManager().findFragmentById(
					R.id.issue_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link IssueListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onIssueSelected(int issuePosition) {
		if ( mTwoPane ) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(IssueDetailFragment.ARG_ITEM_POSITION,
					issuePosition);
			IssueDetailFragment fragment = new IssueDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.issue_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, IssueDetailActivity.class);
			detailIntent.putExtra(IssueDetailFragment.ARG_ITEM_POSITION,
					issuePosition);
			startActivity(detailIntent);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public Issue[] getIssues() {
		return issues;
	}

	public void setIssues(Issue[] issues) {
		this.issues = issues;
		IssueContainer.issues = issues;

		// Notify our fragment of the change.
		IssueListFragment f = (IssueListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.issue_list);
		f.onIssuesLoaded(issues);
	}

	private class GetIssuesTask extends AsyncTask<Void, Void, Issue[]> {
		protected Issue[] doInBackground(Void... _) {
			return HeroesOfGilbert.getIssues();
		}

		protected void onPostExecute(Issue[] issues) {
			setIssues(issues);
		}
	}
}
