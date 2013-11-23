package com.hackathon.abstracts;

public class Enums {

	public static class LoginResult {
		public static int WrongPassword = 0;
		public static int Success = 1;
		public static int Disabled = 2;
		public static int TemporaryBan = 3;
		public static int Activating = 4;
		public static int NoGuestAccount = 5;
		public static int UpdateRequired = 6;
		public static int Logout = 7;
	}

	public static class AppUserIsActive {
		public static int PasswordNotSet = 0;
		public static int Disabled = 1;
		public static int ActiveAuto = 2;
		public static int Activating = 3;
		public static int ActivePerm = 4;
		public static int TemporaryBan = 5;
	}
	
	public static class GeneralResult {
		public static int Success = 0;
		public static int Fail = 1;
		public static int AlreadyDone = 2;
		public static int Online = 3;
		public static int Offline = 4;
	}

}
