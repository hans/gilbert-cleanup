package com.dvcs.gilbertcleanup;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dvcs.gilbertcleanup.models.Comment;
import com.dvcs.gilbertcleanup.models.Issue;
import com.dvcs.gilbertcleanup.neighborhoods.NeighborhoodUtil;

public class CommentListAdapter extends ArrayAdapter<Comment> {

	private LayoutInflater mInflater;
	private int mViewResourceId;
	private Comment[] comments;
	private Context context;

	public CommentListAdapter(Context ctx, int viewResourceId, Comment[] data) {
		super(ctx, viewResourceId, data);

		this.context = ctx;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewResourceId = viewResourceId;
		this.comments = data;

	}

	@Override
	public int getCount() {
		return comments.length;
	}

	@Override
	public Comment getItem(int position) {
		return comments[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.custom_comment_row, null);

		Comment comment = getItem(position);

		String username = comment.getAuthor().getUsername();
		username = username == null ? "Anonymous" : username;
		((TextView) rowView.findViewById(R.id.comment_author))
				.setText(username);

		((TextView) rowView.findViewById(R.id.comment_text)).setText(comment
				.getText());

		return rowView;
	}
}