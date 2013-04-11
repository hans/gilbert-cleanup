package com.dvcs.gilbertcleanup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dvcs.gilbertcleanup.models.Comment;
import com.dvcs.gilbertcleanup.models.ExtendedIssue;
import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;
import com.sqisland.android.swipe_image_viewer.SwipeImageViewerActivity;

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
	public static final String ARG_ITEM_ID = "item_id";

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( getArguments().containsKey(ARG_ITEM_ID) ) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			int index = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
			index--;
			// mItem =
			// DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
			System.out.println(IssueContainer.issues[index].getDescription());
			mItem = IssueContainer.issues[index];
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
}
