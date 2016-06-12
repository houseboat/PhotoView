/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uk.co.senab.photoview.sample;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import uk.co.senab.photoview.PhotoView;




public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

	private static final String TAG = "MATRIX";
	private ViewPager mViewPager;
	private static View initialBoundsView;
	private View captureBtn,restoreBtn,adjustBtn;
	private TextView info;
	private FrameLayout photoFrame;


	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
		//setContentView(mViewPager);
		initialBoundsView = findViewById(R.id.initialbounds);
		captureBtn=findViewById(R.id.capture);
		captureBtn.setOnClickListener(this);
		restoreBtn=findViewById(R.id.restore);
		restoreBtn.setOnClickListener(this);
		adjustBtn=findViewById(R.id.adjust);
		adjustBtn.setOnClickListener(this);
		info= (TextView) findViewById(R.id.info);
		photoFrame= (FrameLayout) findViewById(R.id.photo_frame);
		initialBoundsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//	PhotoView photoView=(PhotoView) mViewPager.


			}
		});

		mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onGlobalLayout() {
				mViewPager.setAdapter(new SamplePagerAdapter());
				mViewPager.addOnPageChangeListener(ViewPagerActivity.this);
				onPageSelected(0);
				mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ViewPager Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://uk.co.senab.photoview.sample/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"ViewPager Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://uk.co.senab.photoview.sample/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	RectF displayRect;
	Matrix displayMatrix;

	public void captureState(PhotoView v) {
		displayRect=v.getDisplayRect();
		displayMatrix=v.getDisplayMatrix();
		Log.d(TAG,"captured: "+displayMatrix);
	}

	public void restoreState(PhotoView v) {
		v.setDisplayMatrix(displayMatrix);
	}

	public ImageView getCurrentPhotoView() {
		return SamplePagerAdapter.items[mViewPager.getCurrentItem()];
	};

	float safeDivision(float left, float right) {
		if (left == 0) {
			return 0;
		}
		if (right == 0) {
			return 1;
		}
		return left / right;

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void adjustView(PhotoView photoView) {
		Log.d(TAG,"### Adjusting ###");
		int pos=mViewPager.getCurrentItem();
		adjustScale(photoView);
		adjustTranslation(photoView);
		//adjustBounds(photoView);



		/*
		Rect absoluteParentRect=new Rect();
		photoView.getRootView().getLocalVisibleRect(absoluteParentRect);
		Rect absoluteRect=new Rect();
		initialBoundsView.getLocalVisibleRect(absoluteRect);
		Log.d(TAG,"  parent absolute "+absoluteParentRect);
		Log.d(TAG,"  adjust to absolute "+absoluteRect);
		RectF origin=new RectF(
				safeDivision(absoluteRect.left,absoluteParentRect.left),
				safeDivision(absoluteRect.top,absoluteParentRect.top),
				safeDivision(absoluteRect.right,absoluteParentRect.right),
				safeDivision(absoluteRect.bottom,absoluteParentRect.bottom));
		Log.d(TAG,"  adjust to relative "+origin);
		RectF adjustTo=photo.getDisplayRect();
		Matrix rectToRectOperation=new Matrix();
		rectToRectOperation.setRectToRect(
				origin,
				adjustTo,
				Matrix.ScaleToFit.START
		);
		Log.d(TAG,"  operation: "+rectToRectOperation);
		//displayMatrix.postConcat(rectToRectOperation);
		displayMatrix.setRectToRect(origin,adjustTo, Matrix.ScaleToFit.CENTER);
		photo.setDisplayMatrix(displayMatrix);
		Log.d(TAG,"  new display matrix:"+photo.getDisplayMatrix());
		*/

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void adjustBounds(PhotoView photoView) {
		Rect clipBounds=photoView.getRootView().getClipBounds();
		if (clipBounds == null) {
			clipBounds=new Rect();
			photoView.getRootView().getLocalVisibleRect(clipBounds);
		}
		Log.d(TAG," clipBounds: "+clipBounds);
		photoView.setClipBounds(clipBounds);
		Log.d(TAG,"  new display matrix:"+photoView.getDisplayMatrix());
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void adjustTranslation(PhotoView photoView) {
		Log.d(TAG,"  adjusting TRANSLATE");
		Matrix displayMatrix=photoView.getDisplayMatrix();
		//displayMatrix.postTranslate(0.9f,0.1f);
		int y= -photoFrame.getResources().getDimensionPixelOffset(R.dimen.page_position_y);
		int frameWidth=photoFrame.getWidth();
		int photoWidth=photoView.getMeasuredWidth();

		int x=-(frameWidth/2)+photoWidth/2;
		photoFrame.scrollTo(x,y);
		/*
		FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) photoView.getLayoutParams();
		params.topMargin=-200;
		photoView.setLayoutParams(params);
		*/
		Log.d(TAG,"    calculated matrix:"+displayMatrix);
		//photoView.setDisplayMatrix(displayMatrix);
		Log.d(TAG,"   resulting DisplayMatrix"+photoView.getDisplayMatrix());
		Log.d(TAG,"   ");

	}

	private void adjustScale(PhotoView photoView) {
		Log.d(TAG,"  adjusting SCALE");
		Matrix displayMatrix=photoView.getDisplayMatrix();
		Log.d(TAG,"    original DisplayMatrix"+displayMatrix);
		float scaleX=safeDivision(initialBoundsView.getWidth(),photoView.getWidth());
		float scaleY=safeDivision(initialBoundsView.getHeight(),photoView.getHeight());
		float scale=Math.min(scaleX,scaleY);
		Log.d(TAG,"    scaleX="+scaleX+" scaleY="+scaleY);
		displayMatrix.setScale(scale,scale);
		Log.d(TAG,"    calculated matrix:"+displayMatrix);
		photoView.setDisplayMatrix(displayMatrix);
		Log.d(TAG,"   resulting DisplayMatrix"+photoView.getDisplayMatrix());
		Log.d(TAG,"   ");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.capture:
				//captureState(getCurrentPhotoView());
				break;
			case R.id.restore:
				//restoreState(getCurrentPhotoView());
				break;
			case R.id.adjust:
				//adjustView(getCurrentPhotoView());
				break;

		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		info.setText((position +1)+ " / "+mViewPager.getAdapter().getCount());
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}


	static class SamplePagerAdapter extends PagerAdapter {

		private static final int[] sDrawables = {R.drawable.samplepage, R.drawable.samplepage, R.drawable.samplepage,
				R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper};
		private static final String TAG = "PAGER" ;

		public static ImageView[] items=new ImageView[sDrawables.length];

		@Override
		public int getCount() {
			return sDrawables.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			LayoutInflater inflater=LayoutInflater.from(container.getContext());
			View pageView=inflater.inflate(R.layout.item_page,null);

			ImageView photoView = (ImageView) pageView.findViewById(R.id.photoview);
			photoView.setImageResource(sDrawables[position]);
			container.addView(pageView);
			items[position]=photoView;
			return pageView;
		}

		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		private void adjustPosition(PhotoView photoView, int position) {

			/*
			Matrix matrix=photoView.getImageMatrix();
			photoView.setScaleType(ImageView.ScaleType.CENTER);
			float scale= (float) Math.pow(0.8f,(float) position+1);
			Log.d(TAG,"Setting scale for position "+position+" to "+scale);
			photoView.setMinimumScale(0.01f);
			photoView.setScale(0.01f);
			//photoView.setImageMatrix(matrix);
			*/
		}


		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
}
