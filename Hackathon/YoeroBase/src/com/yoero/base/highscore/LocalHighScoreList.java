package com.yoero.base.highscore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.yoero.base.UtilSettings;

public class LocalHighScoreList implements ICallback {
	private static String KEY_LOCAL_HIGH_SCORE_LIST = "KEY_LOCHSLIST_";
	private static int SCORES_TO_DISPLAY = 30;

	private ArrayList<HighScoreItem> _scoreItems;
	private int _modeId;

	public LocalHighScoreList(Context ctx, int modeId) {
		_scoreItems = new ArrayList<HighScoreItem>();
		_modeId = modeId;

		loadScoreItems(ctx);
	}

	public void saveScoreItems(Context ctx) {
		UtilSettings.saveSettingsString(ctx, KEY_LOCAL_HIGH_SCORE_LIST + _modeId, listToString(_scoreItems));
	}

	public void loadScoreItems(Context ctx) {
		String contents = UtilSettings.loadSettingsString(ctx, KEY_LOCAL_HIGH_SCORE_LIST + _modeId);

		if (contents == null || contents == "")
			return;

		try {
			String dec = _ctrl.decrypt(contents);
			_scoreItems = stringToList(dec, getCutoff());
		} catch (Exception ex) {
			_scoreItems = new ArrayList<HighScoreItem>();
		}
	}

	public static String listToString(ArrayList<HighScoreItem> list) {
		StringBuilder builder = new StringBuilder();
		for (HighScoreItem item : list) {
			builder.append(item.toString()).append("\n");
		}

		String toEnc = builder.toString().trim();
		return _ctrl.encrypt(toEnc, Integer.MAX_VALUE);
	}

	public static ArrayList<HighScoreItem> stringToList(String str, Date cutoff) {
		ArrayList<HighScoreItem> list = new ArrayList<HighScoreItem>();
		for (String item : str.split("\\r?\\n")) {
			if (item != null && item != "") {
				HighScoreItem it = HighScoreItem.parse(item);
				if (it != null) {
					if ((cutoff != null && it.date.after(cutoff)) || list.size() < SCORES_TO_DISPLAY) {
						list.add(it);
					}
				}
			}
		}

		return list;
	}

	public boolean isHighScore(int score) {
		removeOldEntries();

		if (score > 0 && _scoreItems.size() < SCORES_TO_DISPLAY * 2) {
			return true;
		} else if (score <= 0) {
			return false;
		}

		return _scoreItems.get(_scoreItems.size() - 1).score <= score;
	}

	private void removeOldEntries() {
		Date cutoff = getCutoff();
		for (int i = 0; i < _scoreItems.size(); i++) {
			HighScoreItem it = _scoreItems.get(i);
			if (it.date.before(cutoff)) {
				_scoreItems.remove(it);
				i--;
			}
		}
	}

	private void addScoreItem(HighScoreItem item) {
		int size = _scoreItems.size();
		int index = -1;
		for (int i = 0; i < size; i++) {
			HighScoreItem it = _scoreItems.get(i);
			if (it.score < item.score) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			_scoreItems.add(index, item);
		} else {
			_scoreItems.add(item);
		}

		while (_scoreItems.size() > SCORES_TO_DISPLAY * 2) {
			_scoreItems.remove(_scoreItems.size() - 1);
		}
	}

	public void addScoreItemAndSave(Context ctx, HighScoreItem item) {
		addScoreItem(item);
		saveScoreItems(ctx);
		uploadListToServer(ctx);
	}

	public ArrayList<HighScoreItem> getHighScoreListAllTime() {
		ArrayList<HighScoreItem> toReturn = new ArrayList<HighScoreItem>();
		for (int i = 0; i < SCORES_TO_DISPLAY && i < _scoreItems.size(); i++) {
			toReturn.add(_scoreItems.get(i));
		}

		return toReturn;
	}

	public ArrayList<HighScoreItem> getHighScoreList3Days() {
		Date cutoff = getCutoff();
		ArrayList<HighScoreItem> toReturn = new ArrayList<HighScoreItem>();
		for (int i = 0; i < SCORES_TO_DISPLAY && i < _scoreItems.size(); i++) {
			HighScoreItem it = _scoreItems.get(i);
			if (it.date.after(cutoff)) {
				toReturn.add(_scoreItems.get(i));
			}
		}

		return toReturn;
	}

	public void getHighScoreOnline(Context ctx, IHighScoreListCallback call, int days) {
		if (_ctrl != null) {
			_ctrl.SaveListAndDownloadOnline(ctx, getListChanges(), _modeId, days, this, call);
		}
	}
	
	public static void getScoreTotals(Context ctx, IHighScoreListCallback call, int days) {
		if (_ctrl != null) {
			_ctrl.HighScoreList(ctx, 0, days, call);
		}
	}

	public Date getCutoff() {
		// if (_cutoff == null)
		// _cutoff = UtilSettings.addToDate(new Date(), Calendar.DATE, -7);
		//
		// return _cutoff;

		return UtilSettings.addToDate(new Date(), Calendar.DATE, -3);
	}

	// ONLINE FUNCTIONALITY
	private static HighScoreController _ctrl;

	public static void initOnlineController(String env, String iv, String key, int cid, int gameId, Context ctx) {
		_ctrl = new HighScoreController(env, iv, key, cid, gameId, ctx);
	}

	public void uploadListToServer(Context ctx) {
		if (_ctrl == null) {
			return; // not initialized
		}

		String changes = getListChanges();
		if (changes != null) {
			_ctrl.HighScoreListSave(ctx, changes, _modeId, this);
		}
	}

	@Override
	public void onCallBack(Context ctx, boolean oRes) {
		if (oRes) {
			for (int i = 0; i < _scoreItems.size(); i++) {
				_scoreItems.get(i).savedOnline = true;
			}

			saveScoreItems(ctx);
		}
	}

	public String getListChanges() {
		ArrayList<HighScoreItem> listToUpload = new ArrayList<HighScoreItem>();
		for (int i = 0; i < _scoreItems.size(); i++) {
			HighScoreItem it = _scoreItems.get(i);
			if (!it.savedOnline)
				listToUpload.add(it);
		}

		if (listToUpload.size() > 0)
			return listToString(listToUpload);
		else
			return null;
	}
}
