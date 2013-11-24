package com.yoero.base.highscore;

public class ForbiddenWords {
	public static String _badwords = "fuck,suck,dick,pussy,shit,piss,cunt,cock,porn";
	public static String[] _splitted;

	public static boolean isOk(String word) {
		if (_splitted == null) {
			_splitted = _badwords.split(",");
		}

		String toCompare = word.toLowerCase();
		for (int i = 0; i < _splitted.length; i++) {
			if (toCompare.contains(_splitted[i]))
				return false;
		}

		return true;
	}
	
	public static String sanitizeChars(String nick) {
		return nick.replaceAll(",", "");
	}
}
