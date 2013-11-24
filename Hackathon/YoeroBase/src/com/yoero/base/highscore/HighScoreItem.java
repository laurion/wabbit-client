package com.yoero.base.highscore;

import java.util.Date;

import com.yoero.base.UtilSettings;

public class HighScoreItem {
	public HighScoreItem(String aName, int aScore) {
		this(aName, aScore, new Date());
	}

	public HighScoreItem(String aName, int aScore, Date created) {
		name = aName;
		score = aScore;
		date = created;
	}
	
	public static HighScoreItem createUpdateItem() {
		HighScoreItem hsi = new HighScoreItem("UPDATE_NOW", -1);
		return hsi;
	}
	
	public boolean isUpdateItem() {
		return score == -1 && name.compareTo("UPDATE_NOW") == 0;
	}

	public String name;
	public int score;
	public Date date;
	public boolean savedOnline;

	public String toString() {
		return name.replace(",", "{ocur_exp_dot}").replace(":", "{ocur_exp_colon}") + "," + score + ","
				+ UtilSettings.DateToString(date) + "," + savedOnline;
	}

	public static HighScoreItem parse(String str) {
		String[] parts = str.split("\\,");

		HighScoreItem hsci = null;

		try {
			String name = parts[0];
			name = name.replace("{ocur_exp_dot}", ",");
			name = name.replace("{ocur_exp_colon}", ":");

			int score = Integer.parseInt(parts[1]);
			
			hsci = new HighScoreItem(name, score);
			
			if (parts.length > 2) {
				hsci.date = UtilSettings.StringToDate(parts[2]);
				hsci.savedOnline = Boolean.parseBoolean(parts[3]);
			}
		} catch (Exception ex) {

		}

		return hsci;
	}
}
