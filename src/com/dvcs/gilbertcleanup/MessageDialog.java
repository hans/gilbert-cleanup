package com.dvcs.gilbertcleanup;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageDialog {

	public static void show(Context ctx, int drawableResource, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		LayoutInflater factory = LayoutInflater.from(ctx);
		final View errorView = factory.inflate(R.layout.error, null);

		((ImageView) errorView.findViewById(R.id.errorImage))
				.setImageResource(drawableResource);
		((TextView) errorView.findViewById(R.id.errorText)).setText(message);

		builder.setView(errorView);
		builder.show();
	}

}
