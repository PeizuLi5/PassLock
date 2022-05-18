package application;

import java.util.regex.Pattern;

public class AccountRequirementsUtils {
	/**
	 * Check if the user enters uppercase letter.
	 * 
	 * @param s - string to check
	 * @return True if string has upper case.
	 */
	public static boolean hasUpperCase(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isUpperCase(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the user enters lowercase letter.
	 * 
	 * @param s - string to check
	 * @return True if string has lower case.
	 */
	public static boolean hasLowerCase(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLowerCase(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the user enters special letter.
	 * 
	 * @param s - string to check
	 * @return True if string has special letter.
	 */
	public static boolean hasSpecial(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i)) && !Character.isLetter(s.charAt(i))
					&& !Character.isWhitespace(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the user enters a number letter.
	 * 
	 * @param s - string to check
	 * @return True if string has a number.
	 */
	public static boolean hasNumber(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the email meet the requirement on "exmaple@email_company.com".
	 * 
	 * @param email - string to check
	 * @return True if string is a valid email.
	 */
	public static boolean isValidEmail(String email) {
		final String REGEX = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		return Pattern.compile(REGEX).matcher(email).matches();
	}
}
