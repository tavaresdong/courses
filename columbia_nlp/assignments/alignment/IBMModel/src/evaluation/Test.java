package evaluation;

import model.IBM_Model1;
import model.IBM_Model2;

public class Test {
	public static void main(String[] args) {
		
//		IBM_Model1 model1 = new IBM_Model1("data/tmodel");
//		model1.TrainModel(5);
//		model1.doAlign("data/dev.en", "data/dev.es", "data/test2.key");
		
//		TestModel1();
		TestModel2();
	}
	
	public static void TestModel1() {
		IBM_Model1 model1 = new IBM_Model1("data/corpus.en", "data/corpus.es");
		model1.TrainModel(5);
		model1.outputTValues("data/tmodel");
		model1.doAlign("data/dev.en", "data/dev.es", "data/test.key");


	}
	
	public static void TestModel2() {
		IBM_Model2 model2 = new IBM_Model2("data/corpus.en", "data/corpus.es", "data/tmodel");
		model2.TrainModel(5);
		model2.doAlign("data/dev.en", "data/dev.es", "data/test2.key");
	}

}
