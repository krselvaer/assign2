package no.uis.dat630;

import java.io.File;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class Main {

	public static void main(String[] args) throws Exception {
		{

//			Parser.wrapperClassForHtmlCleaning("train");

//			Parser.testWrapperClassForHtmlCleaning("test");
			
			// convert the directory into a dataset
			TextDirectoryLoader loader = new TextDirectoryLoader();
			loader.setDirectory(new File("trainTxt"));
			
			Instances dataRaw = loader.getDataSet();

			// apply the StringToWordVector
			// (see the source code of setOptions(String[]) method of the filter
			// if you want to know which command-line option corresponds to
			// which
			// bean property)
			StringToWordVector filter = new StringToWordVector();
			Tokenizer t = new WordTokenizer();
			File stopWordList = new File("stop_words.txt");
			filter.setTokenizer(t);
			filter.setStopwords(stopWordList);
			filter.setInputFormat(dataRaw);

			Instances dataFiltered = Filter.useFilter(dataRaw, filter);

			// System.out.println("\n\nFiltered data:\n\n" + dataFiltered);

			loader.setDirectory(new File("testTxt"));
			Instances testSet = loader.getDataSet();
			Instances testDataFiltered = Filter.useFilter(testSet, filter);
			
			
			// train J48 and output model
			NaiveBayes nbc = new NaiveBayes();
			//nbc.buildClassifier(dataFiltered);
			
			Evaluation eval = new Evaluation(dataFiltered);
			Random rand = new Random(1);  // using seed = 1
			int folds = 5;
			eval.crossValidateModel(nbc, dataFiltered, folds, rand);
			System.out.println(eval.toSummaryString());
			
//			Evaluation eval = new Evaluation(dataFiltered);
//			eval.evaluateModel(nbc, testDataFiltered);
//			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
			
//			Classifier svm = new LibSVM();
//			svm.buildClassifier(dataFiltered);
//
//			System.out.println("\n\nClassifier model:\n\n" + nbc);
		}
	}

}
