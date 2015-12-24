package evaluation;

import model.IBM_Model1;

public class Test {
	public static void main(String[] args) {
		IBM_Model1 model1 = new IBM_Model1("data/corpus.en", "data/corpus.es");
		model1.TrainModel(5);
	}

}
