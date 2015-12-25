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

/*
 * Q-value (distortion) reperesentation for 
 * set l and m lengths
 */
class QValuePairs {
	
	private Map<Integer, Map<Integer, Double>> map = null;
	private int L;
	private int M;
	
	public QValuePairs(int l, int m, boolean init) {
		
		map = new HashMap<Integer, Map<Integer, Double>>();
		L = l;
		M = m;
		double initVal = 0.0;
		if (init) 
			initVal = 1.0 / (L + 1);
		
		for (int j = 1; j <= M; j++) {
			map.put(j, new HashMap<Integer, Double>());
			for (int i = 0; i <= L; i++) {
				// initial q(i|j,l,m) value
				map.get(j).put(i, initVal);
			}
		}
	}

	public double getValue(int i, int j) {
		if (!map.containsKey(i)) {
			return 0.0;
		}
		if (!map.get(i).containsKey(j)) {
			return 0.0;
		}
		return map.get(i).get(j);
	}
}




public class IBM_Model2 { 
	
	// TODO use arraylist or linkedlist, need to consider carefully
	private List<String> _rawNativeData = null;
	private List<String> _rawForeignData = null;
	
	// Number of different words
	private Map<String, Set<String>> _wcInTrans = null;
	
	private Map<String, Map<String, Double>> _tvalues = null;
	private Map<Integer, Map<Integer, QValuePairs>> _qvalues = null;
	
	
	
	private Map<String, Map<String, Double>> _cvalues = null;
	private Map<String, Double> _ecvalues = null;
	private Map<Integer, Map<Integer, QValuePairs>> _qcvalues = null;
	private Map<Integer, Map<Integer, Map<Integer, Double>>> _qecvalues = null;
	
	/**
	 * To create an IBM_Model1, you need to input two parallel corpus files
	 * and an iteration value
	 * @param native_corpus_fn
	 * @param foreign_corpus_fn
	 * @param numIter
	 */
	public IBM_Model2(String native_corpus_fn, 
					  String foreign_corpus_fn,
					  String tmodelFile) {
		try {
			Scanner nativeSc  = new Scanner(new File(native_corpus_fn), "utf-8");
			Scanner foreignSc = new Scanner(new File(foreign_corpus_fn), "utf-8");
			
			_getRawData(nativeSc, foreignSc);
			_loadTModel(tmodelFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private void _loadTModel(String tmodelFile) {
		try {
			Scanner sc = new Scanner(new File(tmodelFile), "utf-8");
			_tvalues = new HashMap<String, Map<String, Double>>();
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] vals = line.split("\\s");
				System.out.println(line);
				String e = vals[0];
				String f = vals[1];
				double tval = Double.parseDouble(vals[2]);
				if (!_tvalues.containsKey(e)) {
					_tvalues.put(e, new HashMap<String, Double>());
				}
				_tvalues.get(e).put(f, tval);
			} 
			
			System.out.println("Load model from " + tmodelFile + " done.");
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
		int l = nativeWords.length;
		int m = foreignWords.length;
		
		// Calculate q(j|i,l,m)
		double qval = _getQvalue(j, i, l, m);
		
		String f_i = foreignWords[i - 1];
		String e_j = (j == 0) ? "NULL" : nativeWords[j - 1];
		
		// Calculate t(f|e)
		double tfe = _getTvalue(e_j, f_i);
		
		// Calculate t(f|e) * q(j|i, l,m) with all possible e's in current sentence
		double sumqtfes = 0.0;
		for (int q = 0; q < nativeWords.length; q++) {
			String e_q = (q == 0) ? "NULL" : nativeWords[q - 1];
			sumqtfes += (_getTvalue(e_q, f_i) * _getQvalue(q, i, l, m));
		}
		
		return (tfe * qval) / sumqtfes;
	}
	
	private double _getQvalue(int j, int i, int l, int m) {
		if (!_qvalues.containsKey(l)) {
			return 0.0;
		}
		if (!_qvalues.get(l).containsKey(m)) {
			return 0.0;
		}
		return _qvalues.get(l).get(m).getValue(i, j);
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
		_qvalues   = new HashMap<Integer, Map<Integer, QValuePairs>>();
		while (nativeSc.hasNextLine() && 
			   foreignSc.hasNextLine()) {
			String nativeLine  = nativeSc.nextLine();
			String foreignLine = foreignSc.nextLine(); 
			
			if (nativeLine.trim().isEmpty() || foreignLine.trim().isEmpty()) {
				continue;
			}
			_rawNativeData.add(nativeLine);
			_rawForeignData.add(foreignLine);
			
			String[] nativeWords  = nativeLine.split("\\s");
			String[] foreignWords = foreignLine.split("\\s");
			
			// Initialize the q-values
			int m = foreignWords.length;
			int l = nativeWords.length;
			if (!_qvalues.containsKey(l)) {
				_qvalues.put(l, new HashMap<Integer, QValuePairs>());
			}
			if (!_qvalues.get(l).containsKey(m)) {
				_qvalues.get(l).put(m, new QValuePairs(l, m, true));
			}
			
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
