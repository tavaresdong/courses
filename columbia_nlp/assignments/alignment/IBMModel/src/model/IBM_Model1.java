/**
 * @author Dong Yuchen
 * IBM-Model1
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class IBM_Model1 { 
	
	// TODO use arraylist or linkedlist, need to consider carefully
	private List<String> _rawNativeData = null;
	private List<String> _rawForeignData = null;
	
	// Number of different words
	private Map<String, Set<String>> _wcInTrans = null;
	
	private Map<String, Map<String, Double>> _tvalues = null;
	private Map<String, Map<String, Double>> _cvalues = null;
	private Map<String, Double> _ecvalues = null;
	
	/**
	 * To create an IBM_Model1, you need to input two parallel corpus files
	 * and an iteration value
	 * @param native_corpus_fn
	 * @param foreign_corpus_fn
	 * @param numIter
	 */
	public IBM_Model1(String native_corpus_fn, 
					  String foreign_corpus_fn) {
		try {
			Scanner nativeSc  = new Scanner(new File(native_corpus_fn), "utf-8");
			Scanner foreignSc = new Scanner(new File(foreign_corpus_fn), "utf-8");
			
			_getRawData(nativeSc, foreignSc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public IBM_Model1(String tvalueFile) {
		try {
			Scanner sc = new Scanner(new File(tvalueFile), "utf-8");
			_tvalues = new HashMap<String, Map<String, Double>>();
			
			while (sc.hasNextLine()) {
				String[] vals = sc.nextLine().split("\\s");
				String e = vals[0];
				String f = vals[1];
				double tval = Double.parseDouble(vals[2]);
				if (!_tvalues.containsKey(e)) {
					_tvalues.put(e, new HashMap<String, Double>());
				}
				_tvalues.get(e).put(f, tval);
			} 
			
			System.out.println("Load model from " + tvalueFile + " done.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Train the model using EM algorithm and iterate 
	 * numIter rounds
	 * @param numIter
	 * @return
	 */
	public boolean TrainModel(int numIter) {
		
		for (int iter = 0; iter < numIter; iter++) {
			_setCountsToZero();
			for (int k = 0; k < _rawNativeData.size(); k++) {
				String[] nativeWords  = _rawNativeData.get(k).split("\\s");
				String[] foreignWords = _rawForeignData.get(k).split("\\s");
				for (int i = 1; i <= foreignWords.length; i++) {
					for (int j = 0; j <= nativeWords.length; j++) {
						
						// Note the index should be subtracted by 1
						// -1 means the special native word "NULL"
						double delta = _calculateDelta(foreignWords, nativeWords, i, j);
						_updateCvalue(foreignWords, nativeWords, i, j, delta);
						_updateEcvalue(nativeWords, j, delta);
						
					}
				}
			}
			
			// Update _tvalues
			for (String e : _tvalues.keySet()) {
				for (String f : _tvalues.get(e).keySet()) {
					double cef = _cvalues.get(e).get(f);
					double ce  = _ecvalues.get(e);
					_tvalues.get(e).put(f, cef / ce);
				}
			}
			System.out.println("Iteration : " + iter + " is done.");
		}
		
		System.out.println("Training Done");
		return true;
	}
	
	public void doAlign(String nativeFile, String foreignFile, String outFile) {
		Scanner nativeSc  = null;
		Scanner foreignSc = null;
		PrintWriter pw    = null;

		try {
			nativeSc  = new Scanner(new File(nativeFile), "utf-8");
			foreignSc = new Scanner(new File(foreignFile), "utf-8");
			pw    = new PrintWriter(new File(outFile), "utf-8");
			int sentenceInd = 0;
			while (nativeSc.hasNextLine() && foreignSc.hasNextLine()) {
				String esenten = nativeSc.nextLine();
				String fsenten = foreignSc.nextLine();
				sentenceInd++;
				
				String[] foreignWords = fsenten.split("\\s");
				String[] nativeWords  = esenten.split("\\s");
				
				// Calculate ai (alignment i)
				for (int i = 0; i < foreignWords.length; i++) {
					String f = foreignWords[i];
					int ind = -1;
					double maxtval = Double.MIN_VALUE;
					for (int j = -1; j < nativeWords.length; j++) {
						String e = j == -1 ? "NULL" : nativeWords[j];
						if (!_tvalues.containsKey(e)) {
							continue;
						} else {
							if (!_tvalues.get(e).containsKey(f)) {
								continue;
							} else if (_tvalues.get(e).get(f) > maxtval){
								maxtval = _tvalues.get(e).get(f);
								ind = j;
							}
						}
					}
					if (ind == -1) {
						System.out.println("Sentence " + sentenceInd +  "Word:" + f + " is aligned to NULL");
						continue;
					}
					else {
						// Print value
//						System.out.println(sentenceInd + " " + (ind + 1) + " " + (i + 1));
						pw.println(sentenceInd + " " + (ind + 1) + " " + (i + 1));
					}
					
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			pw.close();
		}

	}
	
	
	/**
	 * Output the Trained t-values to a file with the format:
	 * english-word foreign-word t-value
	 * @param outFile
	 */
	public void outputTValues(String outFile) {
		try {
			PrintWriter pw = new PrintWriter(new File(outFile), "utf-8");
			for (String e : _tvalues.keySet()) {
				for (String f : _tvalues.get(e).keySet()) {
					pw.println(e + " " + f + " " + _tvalues.get(e).get(f));
				}
			}
			System.out.println("Done writing t-values to file: " + outFile);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void _updateEcvalue(String[] nativeWords, int j, double delta) {
		String e_j = (j == 0) ? "NULL" : nativeWords[j - 1];
		double ce = _getECvalue(e_j);
		_ecvalues.put(e_j, ce + delta);
	}

	private double _getECvalue(String e) {
		if (!_ecvalues.containsKey(e))
			_ecvalues.put(e, 0.0);
		return _ecvalues.get(e);
	}

	private void _updateCvalue(String[] foreignWords, String[] nativeWords, 
						int i, int j, double delta) {
		String f_i = foreignWords[i - 1];
		String e_j = (j == 0) ? "NULL" : nativeWords[j - 1];
		
		double cfe = _getCvalue(e_j, f_i);
		
		_cvalues.get(e_j).put(f_i, cfe + delta);
		
	}

	private double _getCvalue(String e, String f) {
		if (!_cvalues.containsKey(e))
			_cvalues.put(e, new HashMap<String, Double>());
		if (!_cvalues.get(e).containsKey(f)) {
			_cvalues.get(e).put(f, 0.0);
		}
		
		return _cvalues.get(e).get(f);
	}

	// Calculate the delta value in the kth corpus line with ith foreign and jth native
	private double _calculateDelta(String[] foreignWords, String[] nativeWords, 
					int i, int j) {
		String f_i = foreignWords[i - 1];
		String e_j = (j == 0) ? "NULL" : nativeWords[j - 1];
		
		// Calculate t(f|e)
		double tfe = _getTvalue(e_j, f_i);
		
		// Calculate t(f|e) with all possible e's in current sentence
		double sumtfes = 0.0;
		for (int q = 0; q < nativeWords.length; q++) {
			String e_q = (q == 0) ? "NULL" : nativeWords[q - 1];
			sumtfes += _getTvalue(e_q, f_i);
		}
		
		return tfe / sumtfes;
	}
	
	private double _getTvalue(String e, String f) {
		if (!_tvalues.containsKey(e))
			_tvalues.put(e, new HashMap<String, Double>());
		if (!_tvalues.get(e).containsKey(f)) {
			// Init value as 1/n(e)
			double sz = _wcInTrans.get(e).size() + 1;
			_tvalues.get(e).put(f, 1.0 / sz);
		}
		
		return _tvalues.get(e).get(f);
	}

	private void _setCountsToZero() {
		_cvalues  = new HashMap<String, Map<String, Double>>();
		_ecvalues = new HashMap<String, Double>();
	}

	// Input corpus data, and generate the initial t values
	private void _getRawData(Scanner nativeSc, Scanner foreignSc) {
		_rawNativeData  = new ArrayList<String>();
		_rawForeignData = new ArrayList<String>();
		_wcInTrans = new HashMap<String, Set<String>>();
		_wcInTrans.put("NULL", new HashSet<String>());
		_tvalues   = new HashMap<String, Map<String, Double>>();
		while (nativeSc.hasNextLine() && 
			   foreignSc.hasNextLine()) {
			String nativeLine  = nativeSc.nextLine();
			String foreignLine = foreignSc.nextLine(); 
			_rawNativeData.add(nativeLine);
			_rawForeignData.add(foreignLine);
			
			String[] nativeWords  = nativeLine.split("\\s");
			String[] foreignWords = foreignLine.split("\\s");
			
			// Accumulate the n(e) count
			for (String e : nativeWords) {
				Set<String> diffTransWords = _wcInTrans.containsKey(e) ? 
									_wcInTrans.get(e) : new HashSet<String>();
				Set<String> nullDiffTransWords = _wcInTrans.get("NULL");
				for (String f : foreignWords) {
					diffTransWords.add(f);
					
					// the native(english) word NULL can be mapped to any word in f
					nullDiffTransWords.add(f);
				}
				_wcInTrans.put(e, diffTransWords);
			}
		}
		
	}

}
