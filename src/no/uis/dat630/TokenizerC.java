package no.uis.dat630;

public class TokenizerC {

	public static String tokenize (String text) {
		String temp = text.replaceAll("\\s", " ");
		temp = temp.replaceAll("[^a-zA-Z0-9 ]+", "").toLowerCase();
		return temp;
		
	}
	
	private static String removeHtmlTags (String text) {
		
		 String newstr = text.replaceAll("\\<.*?\\>", "");
		 return newstr;
	}
}
