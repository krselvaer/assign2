package no.uis.dat630;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Main {

	public static void main(String[] args) throws Exception {

		Parser.wrapperClassForHtmlCleaning("train");
		Parser.testWrapperClassForHtmlCleaning("test");

		// convert the directory into a dataset
		TextDirectoryLoader loader = new TextDirectoryLoader();
		loader.setDirectory(new File("trainTxt//train"));

		Instances dataRaw = loader.getDataSet();

		StringToWordVector filter = new StringToWordVector();
		Tokenizer t = new WordTokenizer();
		File stopWordList = new File("stop_words.txt");
		filter.setTokenizer(t);
		filter.setStopwords(stopWordList);
		filter.setInputFormat(dataRaw);

		Instances trainDataFiltered = Filter.useFilter(dataRaw, filter);
		trainDataFiltered.setClassIndex(0);

		Instances testSet = Parser.loadTestData("testTxt//test");

		 NaiveBayes nbc = new NaiveBayes();
		 nbc.buildClassifier(trainDataFiltered);
		
//		LibSVM svm = new LibSVM();
//		svm.buildClassifier(trainDataFiltered);

		System.out.println ("Built!");
		
		Evaluation eval = new Evaluation(trainDataFiltered);
		Random rand = new Random(1); // using seed = 1
		int folds = 5;
		eval.crossValidateModel(nbc, trainDataFiltered, folds, rand);
		System.out.println(eval.toSummaryString());
		
		//predict(testSet, nbc);
		blabla(testSet, nbc);
		System.out.println("Done!");
	}

	public static void predict(Instances testSet, NaiveBayes nbc) {
		ArrayList<String> predictions = new ArrayList<String>();
		try {
			for (int i = 0; i < testSet.numInstances(); i++) {
				int label;

				label = (int) nbc.classifyInstance(testSet.instance(i));

				switch (label) {
				case 0:
//					testSet.instance(i).setClassValue(label);
					predictions.add((i + 1) + ",course");
					break;

				case 1:
//					testSet.instance(i).setClassValue(label);
					predictions.add((i + 1) + ",faculty");
					break;

				case 2:
//					testSet.instance(i).setClassValue(label);
					predictions.add((i + 1) + ",project");
					break;

				case 3:
//					testSet.instance(i).setClassValue(label);
					predictions.add((i + 1) + ",student");
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Whoops!!");
		}
		Parser.comparableData(predictions, "predictions.txt");
	}
	
	public static void blabla(Instances testSet, NaiveBayes nbc) {
		try {
		for (int i = 0; i < testSet.numInstances(); i++) {
			double[] preds = nbc.distributionForInstance(testSet.instance(i));
			for (double pred : preds)
				System.out.print(""+pred+", ");
			
			System.out.println("");
		}
		}catch(Exception e){
			System.out.println("error");
		}
	}
}
