package evaluation;

import model.IBM_Model1;

public class Test {
	public static void main(String[] args) {
//		IBM_Model1 model1 = new IBM_Model1("data/corpus.en", "data/corpus.es");
//		model1.TrainModel(5);
//		model1.outputTValues("data/model");
//		IBM_Model1 model1 = new IBM_Model1("data/model");
//		model1.doAlign("data/dev.en", "data/dev.es", "data/test.key");
//		model1.doAlign("data/test.en", "data/test.es", "data/alignment_test.p1.out");
		
		IBM_Model1 model1 = new IBM_Model1("data/test.ch.txt", "data/test.en.txt");
		model1.TrainModel(5);
		model1.doAlign("data/test.ch.txt", "data/test.en.txt", "data/result");
	}

}
