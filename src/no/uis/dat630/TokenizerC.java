package no.uis.dat630;

public class TokenizerC {

	public void tokenize (String text) {
		String withoutHtml = removeHtmlTags(text);
		String temp = withoutHtml.replaceAll("\\s", " ");
		temp.replaceAll("[^a-zA-Z0-9 ]+", "");
		
	}
	
	private static String removeHtmlTags (String text) {
		
		 String newstr = text.replaceAll("\\<.*?\\>", "");
		 return newstr;
	}
}
