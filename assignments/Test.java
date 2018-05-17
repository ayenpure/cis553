package assignments;



public class Test {
	
	public static double GetBaseEntropy(double positive, double negative)
	{
		double total = positive + negative;
		double factor1 = positive == 0 ? 0 : positive / total;
		double factor2 = negative == 0 ? 0 : negative / total;
		return -(factor1*(factor1 == 0 ? 0 : Math.log(factor1)) + factor2*(factor2 == 0 ? 0 : Math.log(factor2))) / Math.log(2.0);
	}
	
	public static double GetEntropy(double positive, double negative, double cardinality)
	{
		double total = positive + negative;
		double factor1 = positive == 0 ? 0 : positive / total;
		double factor2 = negative == 0 ? 0 : negative / total;
		return -((factor1*(factor1 == 0 ? 0 : Math.log(factor1)) + factor2*(factor2 == 0 ? 0 : Math.log(factor2))) / Math.log(2.0))*(total/cardinality);
	}
	
	public static double GetBaseEntropy(double arg1, double arg2, double arg3)
	{
		double total = arg1 + arg2 + arg3;
		double factor1 = arg1 == 0 ? 0 : arg1 / total;
		double factor2 = arg2 == 0 ? 0 : arg2 / total;
		double factor3 = arg3 == 0 ? 0 : arg3 / total;
		return -(factor1*(factor1 == 0 ? 0 : Math.log(factor1)) + factor2*(factor2 == 0 ? 0 : Math.log(factor2)) + factor3*(factor3 == 0 ? 0 : Math.log(factor3))) / Math.log(2.0);
	}
	
	public static double GetEntropy(double arg1, double arg2, double arg3, double cardinality)
	{
		double total = arg1 + arg2 + arg3;
		double factor1 = arg1 == 0 ? 0 : arg1 / total;
		double factor2 = arg2 == 0 ? 0 : arg2 / total;
		double factor3 = arg3 == 0 ? 0 : arg3 / total;
		return -(factor1*(factor1 == 0 ? 0 : Math.log(factor1)) + factor2*(factor2 == 0 ? 0 : Math.log(factor2)) + factor3*(factor3 == 0 ? 0 : Math.log(factor3)))*(total/cardinality);
	}
	
	public static void main(String[] args) {
		double cardinality = 120;
		double eR = GetBaseEntropy(20,20,20);
		System.out.println("Entropy : " + eR);
		
		double eAR, eGR, eIR; 
		
		eAR = GetEntropy(20, 20, 36, cardinality) + GetEntropy(0, 20, 2, cardinality) + GetEntropy(20, 0, 2, cardinality); 
		
		eGR = GetEntropy(20, 10, 20, cardinality) + GetEntropy(20, 30, 20, cardinality);
		
		eIR = GetEntropy(0, 5, 18, cardinality) + GetEntropy(32, 30, 20, cardinality) + GetEntropy(8, 5, 2, cardinality);
		
		double gAR, gGR, gIR;
		gAR = eR - eAR;
		gGR = eR - eGR;
		gIR = eR - eIR;
		System.out.println("Gain Age : " + gAR);
		System.out.println("Gain Gender : " + gGR);
		System.out.println("Gain Income : " + gIR);
		
		
		double eRI1 = GetBaseEntropy(20, 20, 36);
		double eRI2 = GetBaseEntropy(0, 20, 2); 
		double eRI3 = GetBaseEntropy(20, 0, 2);
		
		System.out.println("Entropy Age < 30 : " + eRI1);
		System.out.println("Entropy Age 30 - 60 : " + eRI2);
		System.out.println("Entroy Age > 60 : " + eRI3);
		
		System.out.println("********************** Phase 2.1 *************************");
		cardinality = 76;
		{
			double eAG = GetEntropy(16, 5, 18, cardinality) + GetEntropy(4, 15, 18, cardinality);
			double eAI = GetEntropy(0, 0, 18, cardinality) + GetEntropy(16, 15, 18, cardinality) + GetEntropy(4, 5, 0, cardinality);
			
			double gAG = eRI1 - eAG;
			double gAI = eRI1 - eAI;
			System.out.println("Gain Gender : " + gAG);
			System.out.println("Gain Income : " + gAI);
		}
		
		System.out.println("********************** Phase 2.2 *************************");		
		cardinality = 22;
		{
			double eAG = GetEntropy(0, 5, 0, cardinality) + GetEntropy(0, 15, 2, cardinality);
			double eAI = GetEntropy(0, 5, 0, cardinality) + GetEntropy(0, 15, 2, cardinality) + GetEntropy(0, 0, 0, cardinality);
			
			double gAG = eRI1 - eAG;
			double gAI = eRI1 - eAI;
			System.out.println("Gain Gender : " + gAG);
			System.out.println("Gain Income : " + gAI);
			//Same, Skip
		}
		
		System.out.println("********************** Phase 2.3 *************************");
		cardinality = 22;
		{
			double eAG = GetEntropy(4, 0, 2, cardinality) + GetEntropy(16, 0, 0, cardinality);
			double eAI = GetEntropy(0, 0, 0, cardinality) + GetEntropy(16, 0, 0, cardinality) + GetEntropy(4, 0, 2, cardinality);
			
			double gAG = eRI1 - eAG;
			double gAI = eRI1 - eAI;
			System.out.println("Gain Gender : " + gAG);
			System.out.println("Gain Income : " + gAI);
			//Same, Skip
		}
		
		double eI1 = GetBaseEntropy(0, 0, 18);
		double eI2 = GetBaseEntropy(16, 15, 18);
		double eI3 = GetBaseEntropy(4, 5, 0);
		
		System.out.println("Entropy Income  < 60 : " + eI1);
		System.out.println("Entropy Income  60 - 100 : " + eI2);
		System.out.println("Entropy Income  > 100 : " + eI3);
	}
}
