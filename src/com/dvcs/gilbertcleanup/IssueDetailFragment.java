package com.dvcs.gilbertcleanup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dvcs.gilbertcleanup.models.Comment;
import com.dvcs.gilbertcleanup.models.ExtendedIssue;
import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.neighborhoods.NeighborhoodUtil;
import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;

/**
 * A fragment representing a single Issue detail screen. This fragment is either
 * contained in a {@link IssueListActivity} in two-pane mode (on tablets) or a
 * {@link IssueDetailActivity} on handsets.
 */
public class IssueDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_POSITION = "item_position";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Issue mItem;

	private View rootView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public IssueDetailFragment() {
	}

	public void submitComment() {
		String txt = ((EditText) rootView.findViewById(R.id.editText1))
				.getText().toString();
		new SubmitCommentTask().execute(getActivity(), mItem.getKey(), txt);
	}
	public void viewLocation() {
		NeighborhoodUtil neighborhood = new NeighborhoodUtil(getActivity());
		String uri = String.format(Locale.ENGLISH, "geo:%f,%f", neighborhood.findNeighborhoodForCoordinate(mItem.getLocation()));
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		getActivity().startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( getArguments().containsKey(ARG_ITEM_POSITION) ) {
			int position = getArguments().getInt(ARG_ITEM_POSITION);
			mItem = IssueContainer.issues[position];
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_issue_detail, container,
				false);

		new FetchSingleIssueTask().execute(mItem.getKey());

		// Show the dummy content as text in a TextView.
		URL[] pictureUrls = mItem.getPictureUrls();
		new FetchIssueImageTask().execute(pictureUrls);

		String authorName = "Anonymous"; // TODO

		TextView byline = (TextView) rootView.findViewById(R.id.issue_byline);
		byline.setText(String.format(
				(String) byline.getText(),
				new Object[] {
						authorName,
						new RelativeTime(new Date(mItem.getTime() * 1000))
								.getRelativeTime()
				}));

		((TextView) rootView.findViewById(R.id.issue_title)).setText(mItem
				.getTitle());
		((TextView) rootView.findViewById(R.id.issue_description))
				.setText(mItem.getDescription());

		return rootView;
	}

	@Override
	public void onDestroy() {
		IssueDetailActivity.pictures = null;

		super.onDestroy();
	}

	private class FetchSingleIssueTask extends
			AsyncTask<Integer, Void, ExtendedIssue> {
		@Override
		protected ExtendedIssue doInBackground(Integer... ids) {
			assert ids.length == 1;

			return HeroesOfGilbert.getIssue(ids[0]);
		}

		@Override
		public void onPostExecute(ExtendedIssue issue) {
			Comment[] comments = issue.getComments();

			ListView lv = (ListView) rootView
					.findViewById(R.id.issue_comments_list);
			lv.setAdapter(new CommentListAdapter(getActivity(),
					R.layout.custom_comment_row, comments));

			String countStr = comments.length + " comment"
					+ (comments.length == 1 ? "" : "s");
			((TextView) rootView.findViewById(R.id.issue_comment_count))
					.setText(countStr);
		}
	}

	private class FetchIssueImageTask extends AsyncTask<URL, Void, Drawable[]> {
		@Override
		protected Drawable[] doInBackground(URL... urls) {
			// TODO: Multiple URLs

			if ( urls.length == 0 )
				return new Drawable[] {};

			InputStream is;
			try {
				is = (InputStream) urls[0].getContent();
			} catch ( IOException e ) {
				e.printStackTrace();
				return new Drawable[] {};
			}

			return new Drawable[] {
				Drawable.createFromStream(is, "src")
			};
		}

		@Override
		protected void onPostExecute(Drawable[] drawables) {
			IssueDetailActivity.pictures = drawables;

			if ( drawables.length == 0 )
				return;

			ImageView iv = (ImageView) rootView.findViewById(R.id.issue_image);
			Drawable d = drawables[0];

			if ( d != null )
				iv.setImageDrawable(d);
		}
	}

	/**
	 * Parameters are equivalent to those of
	 * {@link HeroesOfGilbert.submitComment}.
	 */
	private class SubmitCommentTask extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Object... params) {
			assert params.length == 3;

			return HeroesOfGilbert.submitComment((Context) params[0],
					(Integer) params[1], (String) params[2]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ( result.booleanValue() ) {
				MessageDialog.show(getActivity(), R.drawable.superhero_smug,
						"Your comment was accepted. Thanks!");
			} else {
				MessageDialog
						.show(getActivity(),
								R.drawable.superhero_helpful,
								"Oops! Your comment couldn't be submitted. Check that you are connected to the Internet and try again.");
			}
		}
	}
}
