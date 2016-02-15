package com.lixy.ftapi.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.queue.listener.MailListener;
import com.lixy.ftapi.type.StatusType;

public class ValidationUtils {
	
	private static final String MOBILE_PHONE_SMS_PATTERN = "^\\+?0905(\\d{9})$";
	private static final String MOBILE_PHONE_SMS_PATTERN_NONSEC_1 = "^05(\\d{9})$";
	private static final String MOBILE_PHONE_SMS_PATTERN_NONSEC_2 = "^905(\\d{9})$";
	private static final String MOBILE_PHONE_SMS_PATTERN_NONSEC_3 = "^5(\\d{9})$";
	
	private static String MAIL_ADDRESS_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{1,10}$"; // NOSONAR
	private static Logger logger = LogManager.getLogger(MailListener.class);
	
	private ValidationUtils(){
		//make singleton
	}

	public static boolean isValidMobilePhoneForSMS(String phoneNo) { // NOSONAR
		boolean valid;

		if (Util.isNullOrEmpty(phoneNo))
			return false;

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN);
		if (valid)
			return true;

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_1);
		if (valid)
			return true;

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_2);
		if (valid)
			return true;

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_3);
		if (valid)
			return true;

		return false;
	}

	public static GResponse isValidMobilePhoneForSMSWS(String phoneNo) { // NOSONAR
		boolean valid;
		GResponse resp = new GResponse();

		if (Util.isNullOrEmpty(phoneNo)) {
			resp.setStatus(StatusType.FAIL);
			return resp;
		}

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN);
		if (valid) {
			return resp;
		}

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_1);
		if (valid) {
			phoneNo = "+09" + phoneNo; // NOSONAR
			resp.setRespMessage(phoneNo);
			return resp;
		}

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_2);
		if (valid) {
			phoneNo = "+0" + phoneNo; // NOSONAR
			resp.setRespMessage(phoneNo);
			return resp;
		}

		valid = phoneNo.matches(MOBILE_PHONE_SMS_PATTERN_NONSEC_3);
		if (valid) {
			phoneNo = "+090" + phoneNo; // NOSONAR
			resp.setRespMessage(phoneNo);
			return resp;
		}

		resp.setStatus(StatusType.FAIL);
		return resp;
	}

	public static boolean isValidMailAddress(String mail) {
		return mail.matches(MAIL_ADDRESS_PATTERN);
	}

	public static boolean validateValueCompare(String object, String value1) {
		if (object.equals(value1))
			return true;
		return false;
	}

	public static boolean validateValueCompare(String object, String value1, String value2) {
		if (object.equals(value1) || object.equals(value2))
			return true;
		return false;
	}

	public static boolean isDouble(String doubleInput) {

		try {
			if ("".equals(doubleInput))
				return true;

			new BigDecimal(doubleInput); // NOSONAR

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	public static boolean isBigInteger(String integerInput) {

		try {
			if ("".equals(integerInput))
				return true;

			new BigInteger(integerInput); // NOSONAR

			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	public static boolean hasAllRepeatingCharacters(String inputString) {

		String firstCharacter = String.valueOf(inputString.charAt(0));
		int count = org.springframework.util.StringUtils.countOccurrencesOf(inputString, firstCharacter);

		return count == inputString.length();
	}

	public static boolean hasSuccessiveAscCharacters(String inputString) { // ardisik
																			// artan

		int successiveCounter = 0;

		for (int i = 0; i < (inputString.length() - 1); i++) {
			int currentDigit = Integer.parseInt(String.valueOf(inputString.charAt(i)));
			int nextDigit = Integer.parseInt(String.valueOf(inputString.charAt(i + 1)));

			if (nextDigit == (currentDigit + 1))
				successiveCounter++;
		}
		
		return successiveCounter == (inputString.length() - 1);
	}

	public static boolean hasSuccessiveDescCharacters(String inputString) { // ardisik
																			// azalan

		int successiveCounter = 0;

		for (int i = 0; i < (inputString.length() - 1); i++) {
			int currentDigit = Integer.parseInt(String.valueOf(inputString.charAt(i)));
			int nextDigit = Integer.parseInt(String.valueOf(inputString.charAt(i + 1)));

			if (nextDigit == (currentDigit - 1))
				successiveCounter++;
		}
		
		return successiveCounter == (inputString.length() - 1);
	}

	public static boolean isSuitablePhoneNumber(String telNo) {

		if (telNo.length() != 10)
			return false;

		if (hasAllRepeatingCharacters(telNo))
			return false;

		if (hasSuccessiveAscCharacters(telNo))
			return false;

		if (hasSuccessiveDescCharacters(telNo))
			return false;

		return true;
	}

	public static boolean isSuitableIrtibatName(String irtibatName) {
		if (hasAllRepeatingCharacters(irtibatName))
			return false;

		return true;
	}

	public static boolean isSuitableEmail(String email) {

		if (StringUtils.contains(email, "@") && StringUtils.contains(email, "."))
			return true;

		return false;
	}

	public static boolean isSuitableTimingAmount(String newTimingAmount) { // NOSONAR
		if (isDouble(newTimingAmount)) {
			if (StringUtils.contains(newTimingAmount, ".")) {
				String[] numberArray = newTimingAmount.split("\\.");
				String decimal = numberArray[0];
				if (isBigInteger(decimal)) {
					BigInteger decimalInt = new BigInteger(decimal);
					if (decimalInt.signum() == 1 && decimalInt.compareTo(BigInteger.valueOf(100)) == -1) // NOSONAR
						return true;
					
					
				}
			} else {
				if (isBigInteger(newTimingAmount)) {
					BigInteger decimalInt = new BigInteger(newTimingAmount);
					if (decimalInt.signum() == 1 && decimalInt.compareTo(BigInteger.valueOf(100)) == -1) // NOSONAR
						return true;
				}
			}
		}

		return false;

	}

}
