package com.dvcs.gilbertcleanup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A fragment representing a single Issue detail screen.
 * This fragment is either contained in a {@link IssueListActivity}
 * in two-pane mode (on tablets) or a {@link IssueDetailActivity}
 * on handsets.
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IssueDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
        	int index = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
        	index --;
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        	System.out.println(IssueContainer.issues[index].getDescription());
        	mItem = IssueContainer.issues[index];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issue_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
        	
            ((TextView) rootView.findViewById(R.id.issue_detail)).setText(mItem.getDescription());
        }
        else
        {
        	((TextView) rootView.findViewById(R.id.issue_detail)).setText("Content is null");
        }

        return rootView;
    }
}
