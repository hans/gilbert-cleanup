package com.dvcs.gilbertcleanup;

import java.util.ArrayList;

import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.neighborhoods.NeighborhoodUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomIssueList extends ArrayAdapter<Issue> {

	private LayoutInflater mInflater;
	private int mViewResourceId;
	private Issue[] your_data;
	private Context context;

	public CustomIssueList(Context ctx, int viewResourceId, Issue[] data) {

		super(ctx, viewResourceId, data);
		this.context = ctx;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewResourceId = viewResourceId;
		this.your_data = data;

	}

	@Override
	public int getCount() {
		return your_data.length;
	}

	@Override
	public Issue getItem(int position) {
		return your_data[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View rowView = mInflater.inflate(R.layout.custom_issue_row, null);// you
																			// are
																			// inflating
																			// your
																			// xml
																			// layout
		TextView neighborhoodText = (TextView) rowView
				.findViewById(R.id.neighborhood);// find a textview in your
													// layout, of course you can
													// add whatever you want
													// such as ImaveView Buttons
													// ....
		// text.setText(listItems_Neighborhood.toString()); // and set text from
		// your_data
		NeighborhoodUtil neighborhood = new NeighborhoodUtil(this.getContext());
		if (your_data[position].getLocation() == null) {
			neighborhoodText.setText("Not available");
		} else {
			neighborhoodText.setText(neighborhood
					.findNeighborhoodForCoordinate(your_data[position]
							.getLocation()).getName());
		}

		TextView titleText = (TextView) rowView.findViewById(R.id.title);// find
																			// a
																			// textview
																			// in
																			// your
																			// layout,
																			// of
																			// course
																			// you
																			// can
																			// add
																			// whatever
																			// you
																			// want
																			// such
																			// as
																			// ImaveView
																			// Buttons
																			// ....

		titleText.setText(your_data[position].getTitle());

		TextView dateText = (TextView) rowView.findViewById(R.id.date);

		dateText.setText(new RelativeTime(new java.util.Date(
				your_data[position].getTime() * 1000)).getRelativeTime());

		TextView urgencyText = (TextView) rowView.findViewById(R.id.urgency);

		if (your_data[position].getUrgency() == 0)
			urgencyText.setText("Urgency: Low");
		else if (your_data[position].getUrgency() == 1)
			urgencyText.setText("Urgency: Medium");
		else if (your_data[position].getUrgency() == 2)
			urgencyText.setText("Urgency: High");
		else
			urgencyText.setText("No urgency found");

		return rowView;
	}
}