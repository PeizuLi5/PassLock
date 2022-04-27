package application;

import java.util.regex.Pattern;

public class AccountRequirementsUtils {
	public static boolean hasUpperCase(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(Character.isUpperCase(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasLowerCase(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(Character.isLowerCase(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasSpecial(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(!Character.isDigit(s.charAt(i))
	           && !Character.isLetter(s.charAt(i))
	           && !Character.isWhitespace(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasNumber(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidEmail(String email) {
		final String REGEX = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		return Pattern.compile(REGEX).matcher(email).matches();
	}
}
