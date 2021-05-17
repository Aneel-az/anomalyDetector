package test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	final float LIMIT = 0.9f;
	public List<CorrelatedFeatures> correlatedFeatures = new ArrayList<>();
	@Override
	public void learnNormal(TimeSeries ts) {
		int featuresNumber = ts.getFeaturesSize();
		for (int i=0; i < featuresNumber - 1; i++){
			float[] xFeatureData = ts.getDataArray(i);
			float limitPerson = LIMIT;
			for (int j=i+1; j < featuresNumber; j++) {
				float[] yFeatureData = ts.getDataArray(j);
				float currentPerson = abs(StatLib.pearson(xFeatureData, yFeatureData));
				if (currentPerson > limitPerson){
					limitPerson = currentPerson;
					Point[] points = cordsToPoints(xFeatureData, yFeatureData);
					Line line = StatLib.linear_reg(points);
					float threshold = get_threshold(points, line);
					this.correlatedFeatures.add(new CorrelatedFeatures(ts.getName(i), ts.getName(j), currentPerson, line,threshold));
				}
			}
		}
	}

	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> result = new ArrayList<>();
		int l = ts.featuresData.get(0).size();
		for (int i=0; i < l; i++) {
			for(CorrelatedFeatures cf : this.correlatedFeatures){
				int firstFeatureIndex = ts.featuresNames.indexOf(cf.feature1);
				int secondFeatureIndex = ts.featuresNames.indexOf(cf.feature2);
				Point p = new Point(ts.featuresData.get(firstFeatureIndex).get(i),
									ts.featuresData.get(secondFeatureIndex).get(i));
				if (StatLib.dev(p, cf.lin_reg) > cf.threshold){
					AnomalyReport report = new AnomalyReport(cf.feature1 + "-" + cf.feature2, i+1);
					result.add(report);
				}
			}
		}
		return result;
	}

	public List<CorrelatedFeatures> getNormalModel(){
		return this.correlatedFeatures;
	}
	private Point[] cordsToPoints(float[] xCords, float[] yCords) {
		int length = xCords.length;
		Point[] points = new Point[length];
		for (int i=0; i < length; i++) {
			points[i] = new Point(xCords[i], yCords[i]);
		}
		return points;
	}

	private float get_threshold(Point[] points, Line line) {
		float biggestDeviation = 0;
		for (Point p : points) {
			float currentDeviation = StatLib.dev(p, line);
			if (currentDeviation >= biggestDeviation) {
				biggestDeviation = currentDeviation;
			}
		}
		return biggestDeviation * 1.1f;
	}
}
