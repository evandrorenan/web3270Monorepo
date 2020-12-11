package br.com.evandrorenan.web3270scripts.util;

import java.util.Calendar;
import java.util.Date;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public final class Web3270ScriptsUtils {
	
	private Web3270ScriptsUtils(){}
	private static final String REGEX_MATCHES_EVERYTHING_STARTING_FROM_FIRST_SPACE = "\\s+.*";
	
	public static String getMessage(String propertyName, MessageSource messageSource) {
		return messageSource.getMessage(propertyName, (Object[]) null, LocaleContextHolder.getLocale());
	}

	public static Date date(int year, int month, int date, int hour, int minutes, int seconds) {
		Calendar working = Calendar.getInstance();
		working.set(year, month, date, hour, minutes, seconds);
		return working.getTime();
	}

	public static boolean isNumeric(String str) {
		
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static String substringByLength(String text, int start, int length) {
	    return text.substring(start, Math.min(start + length, text.length()));
	}
	
	public static String nextWordAfter(String text, String wordAfter ) {
		
		int j = text.toUpperCase().indexOf(wordAfter.toUpperCase());
		if (j < 0 ) {
			return "";
		}
		
		return text.toUpperCase().substring(j + wordAfter.length() + 1).replaceAll(REGEX_MATCHES_EVERYTHING_STARTING_FROM_FIRST_SPACE, "");
	}
}