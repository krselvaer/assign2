package no.uis.dat630;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Parser {
	
//	public static String parseHtmlPage (Attribute attribute) {
//		// Tokenize (Removing everything except words)
//		// Then remove stopwords
//		ArrayList<String> sfg = new ArrayList<String>();
//		
//		for (int i=0; i<attribute.numValues(); i++)
//			sfg.add(removeStopWords(TokenizerC.tokenize(attribute.value(i))));
//		
//		String finished = TokenizerC.tokenize(sfg.toString());
//		return finished;
//	}
	
	// Make this work, and make it my own
	public static String removeStopWords (String text) {
		
	    String[] words = text.split(" ");
	    
	    ArrayList<String> wordsList = new ArrayList<String>();
	    Set<String> stopWordsSet = new HashSet<String>();
	    
	    // Add words to the stopWordsSet from stop_words.txt
	    BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("stop_words.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                stopWordsSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

	    //If the word is not int in stopWordsSet add it to wordsList
	    for(String word : words)
	    {
	        String wordCompare = word.toUpperCase();
	        if(!stopWordsSet.contains(wordCompare))
	        {
	            wordsList.add(word);
	        }
	    }

	    String processed = null;
	    for (String word : wordsList) {
	    	if (processed == null)	
	    		processed = word;
	    	else 
	    		processed = text + " " + word;
	    }
		
		return processed;
	}
	
	public static void wrapperClassForHtmlCleaning (String rootDir) throws IOException {
		File rootPath = new File(rootDir);
		File[] rootFiles = rootPath.listFiles();
		for(int i=0; i<rootFiles.length; i++) {
			File path = new File(rootFiles[i].toString());
			File[] files = path.listFiles();
			for (int j=0; j<files.length; j++) {
				String temp = readFile(files[j].getPath(),StandardCharsets.UTF_8);
				temp = cleanTagPerservingLineBreaks(temp);
				temp = removeUrl(temp);
				temp = removeExtendedChars(temp);
				temp = TokenizerC.tokenize(temp);
				PrintWriter writer = new PrintWriter(rootDir+ "Txt\\" + files[j].getPath(), "UTF-8");
				writer.println(temp);
				writer.close();
			}
		}
	}
	
	public static void testWrapperClassForHtmlCleaning (String rootDir) throws IOException {
		File rootPath = new File(rootDir);
		File[] files = rootPath.listFiles();
		for(int i=0; i<files.length; i++) {
			String temp = readFile(files[i].getPath(),StandardCharsets.UTF_8);
			temp = cleanTagPerservingLineBreaks(temp);
			temp = removeUrl(temp);
			temp = removeExtendedChars(temp);
			temp = TokenizerC.tokenize(temp);
			PrintWriter writer = new PrintWriter(rootDir+ "Txt\\" + files[i].getPath(), "UTF-8");
			writer.println(temp);
			writer.close();
		}
	}
	
	public static Instances loadTestData (String path) throws IOException {
		File rootPath = new File(path);
		File[] files = rootPath.listFiles();
		
		
		FastVector fvNominalVal = new FastVector(4);
		fvNominalVal.addElement("course");
		fvNominalVal.addElement("faculty");
		fvNominalVal.addElement("project");
		fvNominalVal.addElement("student");
		
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		Attribute attribute2 = new Attribute("text",(FastVector) null);
		
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute2);
		fvWekaAttributes.addElement(attribute1);
		
		Instances testData = new Instances("Test relation", fvWekaAttributes, 1);
		testData.setClassIndex(1);
		for(int i=0; i<files.length; i++) {
			String temp = readFile(files[i].getPath(),StandardCharsets.UTF_8);
			Instance instance = new Instance(2);
			instance.setValue(attribute2, temp);
			testData.add(instance);
		}
		
		return testData;
	}
	
	public static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static String cleanTagPerservingLineBreaks(String html) {
	    String result = "";
	    if (html == null)
	        return html;
	    Document document = Jsoup.parse(html);
	    document.outputSettings(new Document.OutputSettings()
	            .prettyPrint(false));// makes html() preserve linebreaks and
	                                        // spacing
	    document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    result = document.html().replaceAll("\\\\n", "\n");
	    result = Jsoup.clean(result, "", Whitelist.none(),
	            new Document.OutputSettings().prettyPrint(false));
	    return result;
	}
	
	public static String removeUrl(String str) {
	    String regex = "\\b(https?|ftp|file|telnet|http|Unsure)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	    str = str.replaceAll(regex, "");
	    return str;
	}
	
	public static String removeExtendedChars(String str) {
	    return str.replaceAll("[^\\x00-\\x7F]", " ");
	}
	
	public static void comparableData(ArrayList<String> predictions, String outPath) {
		try {
			FileWriter writer = new FileWriter(outPath);
			writer.write("Id,Class");
			writer.write(System.lineSeparator());
			for (String result : predictions) {
				writer.write(result);
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (Exception e) {
			System.out.println("File error");
		}
	}
}
