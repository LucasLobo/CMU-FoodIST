package com.grpc.server.util;

import java.util.ArrayList;

public class LinearRegression {

	int n = 0;
	int sum_x = 0;
	int sum_y = 0;
	int sum_xy = 0;
	int sum_xx = 0;
	double SS_xx = 0;
	double mean_y = 0;

	double m = 0d;
	double b = 0d;

	public void addDataPoint(Integer x, Integer y) {
		n += 1;
		sum_x += x;
		sum_y += y;
		sum_xy += x*y;
		sum_xx += x*x;

		estimateCoefficients();
	}

	private void estimateCoefficients() {
		double mean_x = ((double)sum_x)/n;
		mean_y = ((double)sum_y)/n;
		double SS_xy = sum_xy - n*mean_x*mean_y;
		SS_xx = sum_xx - n*mean_x*mean_x;

		m = SS_xy/SS_xx;
		b = mean_y - m*mean_x;
	}

	public double predict(Integer value) {
		if (n < 1) return -1d;
		if (SS_xx == 0) return mean_y;
		return b + m*value;
	}


}
