package com.android.vending.billing;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.util.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;
import com.yoero.base.ICallback;

public class BillingHelper implements IabHelper.OnIabSetupFinishedListener {
	private IabHelper mHelper;
	public static final String TAG = "android.billing";
	private boolean _iabSetup;

	private ICallback _onSetup;

	public BillingHelper(Activity ctx, String base64EncodedPublicKey, ICallback onSetup) {
		_onSetup = onSetup;

		mHelper = new IabHelper(ctx, base64EncodedPublicKey);
		mHelper.startSetup(this);
	}

	public void onIabSetupFinished(IabResult result) {
		if (!result.isSuccess()) {
			// Oh noes, there was a problem.
			Log.d(TAG, "Problem setting up In-app Billing: " + result);
		} else {
			// Hooray, IAB is fully set up!
			_iabSetup = true;
			Log.d(TAG, "Billing setup: " + result);
			if (_onSetup != null) {
				_onSetup.call(this);
			}
		}
	}

	public void initiatePurchase(Activity ctx, String productId, int requestCode,
			OnIabPurchaseFinishedListener mPurchaseFinishedListener, String uid) {
		try {
			mHelper.launchPurchaseFlow(ctx, productId, requestCode, mPurchaseFinishedListener, uid);
		} catch (Exception ex) {
			Toast.makeText(ctx, "No support for in-game purchases on this device", Toast.LENGTH_LONG).show();
		}
	}

	public void handleActivityResult(int requestCode, int resultCode, Intent data) {
		mHelper.handleActivityResult(requestCode, resultCode, data);
	}

	public void queryInvetory(IabHelper.QueryInventoryFinishedListener mGotInventoryListener) {
		if (!_iabSetup)
			return;
		
		mHelper.queryInventoryAsync(mGotInventoryListener);
	}
	
	public void consumeAsync(Purchase purchase, OnConsumeFinishedListener listener) {
		mHelper.consumeAsync(purchase, listener);
	}

	public void onDestroy(Activity ctx) {
		if (mHelper != null) {
			mHelper.dispose();
		}

		mHelper = null;
	}
}
