package com.yoero.base;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class AudioClip {
	private MediaPlayer mPlayer;
	private String name;

	private boolean mPlaying = false;
	private boolean mLoop = false;

	private static boolean IsMute = false;

	public static boolean getIsMute() {
		return IsMute;
	}

	public static boolean MuteOrUnmute(Context ctx) {
		IsMute = !IsMute;
		UtilSettings.saveSettingsBoolean(ctx, "SOUND_MUTE_STATE", AudioClip.IsMute);

		return IsMute;
	}

	public static void MuteStateSave(Context ctx, boolean mute) {
		IsMute = mute;
		UtilSettings.saveSettingsBoolean(ctx, "SOUND_MUTE_STATE", IsMute);
	}

	public static boolean MuteStateLoad(Context ctx) {
		IsMute = UtilSettings.loadSettingsBoolean(ctx, "SOUND_MUTE_STATE");
		return IsMute;
	}

	public AudioClip(Context ctx, String resName) {
		int resId = ctx.getResources().getIdentifier(resName, "raw", ctx.getPackageName());

		init(ctx, resId);
	}

	public AudioClip(Context ctx, int resId) {
		init(ctx, resId);
	}

	private void init(Context ctx, int resId) {
		name = ctx.getResources().getResourceName(resId);

		mPlayer = MediaPlayer.create(ctx, resId);
		// TODO: Quick hack! So that game won't crash if something weird is
		// going on... but find more robust way!
		if (mPlayer != null) {
			mPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					Log.d("AudioClip", "MediaPlayer Error::arg1=" + arg1 + "   arg2=" + arg2);
					return false;
				}
			});
			mPlayer.setVolume(1000, 1000);

			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					mPlaying = false;
//					if (mLoop) {
//						//Log.d("AudioClip","AudioClip loop " + name);
//						mPlaying = true;
//						mp.start();
//					}
				}

			});
		} else {

		}
	}

	public synchronized void play() {
		if (IsMute) {
			return;
		}

		try {
			// if (mPlaying)
			// return;

			if (mPlayer != null) {
				mPlaying = true;
				mPlayer.seekTo(0);
				mPlayer.start();
			}
		} catch (Exception e) {
			Log.d("AudioClip", "AudioClip::play " + name + " " + e.toString());
		}
	}

	public synchronized void stop() {
		try {
			mLoop = false;
			if (mPlaying) {
				mPlaying = false;
				mPlayer.pause();
			}

		} catch (Exception e) {
			Log.d("AudioClip", "AudioClip::stop " + name + " " + e.toString());
		}
	}

	public synchronized void loop() {
		if (IsMute) {
			return;
		}

		mLoop = true;
		mPlaying = true;
		mPlayer.setLooping(true);
		mPlayer.start();

	}

	public void release() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
}