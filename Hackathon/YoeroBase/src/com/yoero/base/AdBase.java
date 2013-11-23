package com.yoero.base;

//import com.yoero.base.adnetworks.ApplovinController;
//
//import android.app.Activity;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;

//public class AdBase {
//	
//	private static ApplovinController _ac = new ApplovinController(0);	
//	private static FrameLayout _adHolder;
//	
//	public static void initAdView(Activity ctx, LinearLayout llAdHolder, boolean useCache) {
//		// Activity needs to be the same all the time for caching to work
//		// set useCache = false if switching between activities
//		
//		if (!useCache || _adHolder == null) {
//			_ac.onDestroy();
//			
//			_adHolder = new FrameLayout(ctx);
//			
//			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, ctx.getResources().getDisplayMetrics());
//			
//			_adHolder.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
//			llAdHolder.addView(_adHolder);
//			
//			_ac.refreshAd(ctx, _adHolder, null);
//		} else if (useCache) {
//			LinearLayout par = (LinearLayout)_adHolder.getParent();
//			par.removeView(_adHolder);
//			
//			llAdHolder.addView(_adHolder);
//		}
//	}
//	
////	private static boolean _loaded;
////	public static void loadAdIfNeeded() {
////		if (_loaded) {
////			_ac.resumeAd();
////		}
////	}
//	
//	// Don't need it for now
////	public static void removeAdFromView() {
////		LinearLayout v = ((LinearLayout)_adHolder.getParent());
////		v.removeAllViews();
////	}
//
//}
