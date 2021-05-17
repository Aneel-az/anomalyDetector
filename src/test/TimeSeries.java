package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

public class TimeSeries {
	public final List<String> featuresNames = new ArrayList<>();
	public final List<ArrayList<Float>> featuresData = new ArrayList<>();

	public TimeSeries(String csvFileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFileName));
			String line;

			if ((line = br.readLine()) != null) {
				String[] namesArray = line.split(",");
				for (String s : namesArray) {
					this.featuresNames.add(s);
					this.featuresData.add(new ArrayList<>());
				}
			}
			while ((line = br.readLine()) != null) {
				String[] numbersArray = line.split(",");
				for (int i = 0; i < numbersArray.length; i++) {
					this.featuresData.get(i).add(Float.parseFloat(numbersArray[i]));
				}
			}
		} catch (IOException e) {
			System.out.println("IO error");
		}
	}
	public float[] getDataArray(int index) {
		List<Float> floatList = this.featuresData.get(index);
		float[] floatArray = new float[floatList.size()];
		int i = 0;
		for (Float f : floatList) {
			floatArray[i++] = (f != null ? f : Float.NaN);
		}
		return floatArray;
	}
	public String getName(int index) {
		return this.featuresNames.get(index);
	}
	public int getFeaturesSize() {
		return this.featuresNames.size();
	}
}
