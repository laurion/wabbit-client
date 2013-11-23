package com.yoero.base.highscore;

public enum HighScoreSaveResult {
	InvalidNick, 	// -3
	NickTaken, 		// -2
	NoConnection, 	// -1
	GeneralError, 	//  0
	Success			//  1
}
