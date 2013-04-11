package com.sqisland.android.swipe_image_viewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dvcs.gilbertcleanup.IssueDetailActivity;
import com.dvcs.gilbertcleanup.R;

public class SwipeImageViewerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe_image_viewer);
		
		Drawable[] pictures = IssueDetailActivity.pictures;
		if ( pictures == null ) {
			// TODO: handle missing data
		}

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		ImagePagerAdapter adapter = new ImagePagerAdapter(pictures);
		viewPager.setAdapter(adapter);
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private Drawable[] items;
		
		public ImagePagerAdapter(Drawable[] items) {
			this.items = items;
		}
		
		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Context context = SwipeImageViewerActivity.this;
			ImageView imageView = new ImageView(context);
			int padding = context.getResources().getDimensionPixelSize(
					R.dimen.padding_medium);
			imageView.setPadding(padding, padding, padding, padding);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setImageDrawable(items[position]);
			((ViewPager) container).addView(imageView, 0);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
		
	}
}