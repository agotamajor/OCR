import java.io.*;
import java.util.*;

public class Main {
	static double EukTav(Integer[] vektor1, Integer[] vektor2) {
		double value = 0.0;
		for (int i = 0; i < vektor1.length; i++) {
			value += (vektor1[i] - vektor2[i]) * (vektor1[i] - vektor2[i]);
		}
		return value;
	}
	
	static double EukTavDouble(Integer[] vektor1, Double[] vektor2) {
		double value = 0.0;
		for (int i = 0; i < vektor1.length; i++) {
			value += (vektor1[i] - vektor2[i]) * (vektor1[i] - vektor2[i]);
		}
		return Math.sqrt(value);
	}
	
	static double lastKey(Map<Double, ArrayList<Integer>> data) {
		double value = 0.0;
		for (Map.Entry<Double, ArrayList<Integer>> kv : data.entrySet()) {
			value = kv.getKey();
		}
		return value;
	}
	
	static Integer kNN(Integer[] vektor, Map<Integer, ArrayList<Integer[]>> data) {
		Map<Double, ArrayList<Integer>> kNN = new TreeMap<Double, ArrayList<Integer>>();
		int i = 0;
		for (Map.Entry<Integer, ArrayList<Integer[]>> kv : data.entrySet()) {

			for (int j = 0; j < kv.getValue().size(); j++) {
				double value = EukTav(kv.getValue().get(j), vektor);
				if (i < 100) {
					if (!kNN.containsKey(value)) {
						kNN.put(value, new ArrayList<Integer>());
					}
					kNN.get(value).add(kv.getKey());
				} else {
					double last = lastKey(kNN);
					if (value < last) {
						kNN.remove(last);
						if (!kNN.containsKey(value)) {
							kNN.put(value, new ArrayList<Integer>());
						}
						
						kNN.get(value).add(kv.getKey());
					}
				}
				i++;
			}
		}
		int key[] = {0,0,0,0,0,0,0,0,0,0};
		for (Map.Entry<Double, ArrayList<Integer>> kv : kNN.entrySet()) {
			for (i = 0; i < kv.getValue().size(); i++) {
				key[kv.getValue().get(i)]++;
			}
		}
		Integer index = 0;
		int max = 0;
		for (i = 0; i < 10; i++) {
			if (max < key[i]) {
				max = key[i];
				index = i;
			}
		}
		return index;
	}
	
	static Map<Integer, Double[]> centralizalas(Map<Integer, ArrayList<Integer[]>> data) {
		Map<Integer, Double[]> centralizalt = new TreeMap<Integer, Double[]>();
		
		for (Map.Entry<Integer, ArrayList<Integer[]>> kv : data.entrySet()) {
			Double[] vektor = new Double[64];
			for (int i = 0; i < 64; i++) {
				vektor[i] = 0.0;
			}
			for (int i = 0; i < kv.getValue().size(); i++) {
				for (int j = 0; j < 64; j++) {
					vektor[j] += kv.getValue().get(i)[j];
				}
			}	
			for (int j = 0; j < 64; j++) {
				vektor[j] /= kv.getValue().size();
			}
			centralizalt.put(kv.getKey(), vektor);
		}
		return centralizalt;
	}
	
	static Integer Centroid(Integer[] vektor, Map<Integer, Double[]> data) {
		Integer index = 10;
		double min = 100000000.0;
		for (Map.Entry<Integer, Double[]> kv : data.entrySet()) {
			//System.out.println(kv.getKey());
			for (int i = 0; i < 64; i++) {
				//System.out.print(kv.getValue()[i] + " ");
			}
			//System.out.println();
			double tav = EukTavDouble(vektor, kv.getValue());
			// System.out.print(tav + " ");
			if (min > tav) {
				min = tav;
				index = kv.getKey();
				//System.out.print(min);
			}
			//System.out.println();
		}
		return index;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		File train = new File("../optdigits.tra");
		Scanner trainReader = new Scanner(train);
		
		Map<Integer, ArrayList<Integer[]>> trainData = new TreeMap<Integer, ArrayList<Integer[]>>();
		int[] szam = {0,0,0,0,0,0,0,0,0,0};
		
		
		
		while (trainReader.hasNextLine()) {
			String file = trainReader.nextLine();
			String[] tokens = file.split(",");
			Integer num = Integer.parseInt(tokens[tokens.length-1]);
			Integer[] vektor = new Integer[tokens.length - 1];
			for (int i = 0; i < tokens.length - 1; i++) {
				vektor[i] = Integer.parseInt(tokens[i]);
			}
			if (!trainData.containsKey(num)) {
				trainData.put(num, new ArrayList<Integer[]>());
			} 
			trainData.get(num).add(vektor);
			
		}
		trainReader.close();
		
		for (Map.Entry<Integer, ArrayList<Integer[]>> kv : trainData.entrySet()) {
			szam[kv.getKey()] = kv.getValue().size();
			System.out.println(szam[kv.getKey()]);
		}
		
		//train
		Scanner kNNTrainReader = new Scanner(train);
		double[] kNNhelytelenTrain = {0,0,0,0,0,0,0,0,0,0};
		double[] centroidHelytelenTrain = {0,0,0,0,0,0,0,0,0,0};
		
		Map<Integer, Double[]> centralizalt = centralizalas(trainData);
		
		for (Map.Entry<Integer, Double[]> kv : centralizalt.entrySet()) {
			//System.out.println(kv.getKey());
			for (int i = 0; i < 64; i++) {
				//System.out.print(kv.getValue()[i] + " ");
			}
			//System.out.println();
		}
		
		while (kNNTrainReader.hasNext()) {
			String file = kNNTrainReader.nextLine();
			String[] tokens = file.split(",");
			Integer num = Integer.parseInt(tokens[tokens.length-1]);
			Integer[] vektor = new Integer[tokens.length - 1];
			for (int i = 0; i < tokens.length - 1; i++) {
				vektor[i] = Integer.parseInt(tokens[i]);
			}
			if (kNN(vektor, trainData) != num) {
				kNNhelytelenTrain[num]++;
			}
			if (Centroid(vektor, centralizalt) != num) {
				centroidHelytelenTrain[num]++;
			}
		}
		kNNTrainReader.close();
		System.out.println("kNN train: ");
		for (int i = 0; i < 10; i++) {
			System.out.println(i + ": " + kNNhelytelenTrain[i]/szam[i]);
		}
		System.out.println("centroid train: ");
		for (int i = 0; i < 10; i++) {
			System.out.println(i + ": " + centroidHelytelenTrain[i]/szam[i]);
		}
		
		//teszt
		File teszt = new File("../optdigits.tes");
		Scanner TesztReader = new Scanner(teszt);
		double[] kNNhelytelenTeszt = {0,0,0,0,0,0,0,0,0,0};
		double[] centroidHelytelenTeszt = {0,0,0,0,0,0,0,0,0,0};
		while (TesztReader.hasNext()) {
			String file = TesztReader.nextLine();
			String[] tokens = file.split(",");
			Integer num = Integer.parseInt(tokens[tokens.length-1]);
			Integer[] vektor = new Integer[tokens.length - 1];
			for (int i = 0; i < tokens.length - 1; i++) {
				vektor[i] = Integer.parseInt(tokens[i]);
			}
			if (kNN(vektor, trainData) != num) {
				kNNhelytelenTeszt[num]++;
			}
			if (Centroid(vektor, centralizalt) != num) {
				centroidHelytelenTeszt[num]++;
			}
		}
		TesztReader.close();
		System.out.println("kNN teszt: ");
		for (int i = 0; i < 10; i++) {
			System.out.println(i + ": " + kNNhelytelenTeszt[i]/szam[i]);
		}
		System.out.println("centroid teszt: ");
		for (int i = 0; i < 10; i++) {
			System.out.println(i + ": " + centroidHelytelenTeszt[i]/szam[i]);
		}
	}
}
