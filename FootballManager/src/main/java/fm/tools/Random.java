package fm.tools;

import java.util.ArrayList;
import java.util.List;

public class Random {
	public static java.util.Random rng = new java.util.Random();
	
	public static List<Integer> intsWithSum(int n, int sum) {
		List<Integer> list = new ArrayList<>();
		
		List<Double> temp = new ArrayList<>();
		double dsum = 0d;
		
		for(int i = 0; i < n; i++) {
			double x = rng.nextDouble();
			temp.add(x);
			dsum += x;
		}
		
		double factor = (sum - n) / dsum;
		int isum = 0;
		
		for(Double d : temp) {
			int x = (int)(d.doubleValue() * factor) + 1;
			list.add(x);
			isum += x;
		}
		
		for(int i = 0; i < sum - isum; i++) {
			int rand = rng.nextInt(list.size());
			list.set(rand, list.get(rand) + 1);
		}
		
		return list;
	}
}
