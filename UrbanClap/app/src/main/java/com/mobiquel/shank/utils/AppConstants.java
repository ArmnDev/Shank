package com.mobiquel.shank.utils;

public final class AppConstants 
{
	public static final String SERVER_URL = "http://shank.co.in:8080/ApplianceCare/rest/service/";

	public static final String SUB_URL_REGISTER = "registerUser";
	public static final String SUB_URL_LOGIN = "login";
	public static final String SUB_URL_UPDATE_PROFILE = "updateUserProfile";
	public static final String SUB_GET_CITIZEN_INCENTIVES_FEED = "getIncentivesForUser";
	public static final String SUB_URL_GENERATE_AND_SEND_OTP = "generateOtp";
	public static final String SUB_URL_VERIFY_OTP = "verifyOtp";
	public static final String SUB_URL_RESEND_OTP = "resendOtp";
	public static final String SUB_URL_CHECK_IF_USER_EXISTS = "checkIfUserExists";
	public static final String REGISTER_PUSH_NOTIFICATION_SUB_URL = "registerPushNotificationId";
	public static final String USER_INCENTIVE_WALLET_BALANCE_SUB_URL = "getIncentiveWalletBalanceForUser";
	public static final String UPLOAD_IMAGE_SUB_URL = "uploadImage";
	public static final String REPORT_ISSUE_SUB_URL = "reportIssue";
	public static final int LONG_TOAST_KEY = 40000;
	public static final String GCM_SENDER_ID = "347211937621";
	public static final String INTERESTS_KEY = "INTERESTS_KEY";
	public static final String SUB_URL_LIST_OF_REPORT_VIOLATION = "getListOfReportedViolations";
	public static final String SUB_URL_INCENTIVES_FOR_USER = "getIncentivesForUser";
	public static final String SUB_URL_WALLET_BALANCE = "getIncentiveWalletBalanceForUser";
	public static final String SUB_URL_REPORT_VIOLATION = "reportViolation";
	public static final String SUB_URL_ADD_DESCRIPTION = "updateViolationDescription";
	public static final String SUB_URL_UPLOAD_APP_DUMP = "uploadAppRecordingDump";
	public static final String GET_OTP ="generateOtp" ;
	public static final String VERIFY_OTP ="verifyOtp" ;


	public static final class MESSAGES
	{
		public static final String ENABLE_INTERNET_SETTING_MESSAGE = "Please connect to internet";
		public static final String ERROR_FETCHING_DATA_MESSAGE = "Error fetching data, please try again later";
		public static final String ERROR_NO_DATA_AVAILABLE_MESSAGE = "No data available";
		public static final String NETWORK_ERROR_MESSAGE = "Network Error, please try again later";
		public static final String NO_DATA_AVAILABLE_MESSAGE = "No new updates available";
		public static final String NO_FIELD_BLANK_MESSAGE = "No field can be left blank";
		public static final String INVALID_HOOD_NAME = "Invalid hood name";
		public static final String ENTER_VALID_EMAIL = "Please enter a valid email address";
		public static final String ENTER_VALID_PHONE_NUMBER = "Please enter a valid mobile number";
		public static final String SIGNED_IN_SUCCESS = "is connected now";
		public static final String SIGNED_OUT_SUCCESS = "Signed out successfully";
		public static final String ADDED_TO_FAVORITES = "Added to favorites";
		public static final String REMOVED_FROM_FAVORITES = "Removed from favorites";
		public static final String LOGIN_TO_SUBMIT_REVIEW = "Please login to submit review";
		public static final String BLANK_COMMENT_SUBMIT = "Blank comment cannot be submitted!";
		public static final String COMMENT_LENGTH_EXCEED_SUBMIT = "Comment exceeding 20 word length limit!";
		public static final String IMPRESSION_SUBMISSION_LENGTH_EXCEED_SUBMIT = "Submission exceeding 20 word length limit!";
	}

	public static final class INTENT_KEYS
	{
		public static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
		public static final String KEY_USER_CONTACT_NUMBER = "KEY_USER_CONTACT_NUMBER";
		public static final String KEY_USER_NAME = "KEY_USER_NAME";
		public static final String KEY_PROFILE_IMAGE_URL = "KEY_PROFILE_IMAGE_URL";
	}
}