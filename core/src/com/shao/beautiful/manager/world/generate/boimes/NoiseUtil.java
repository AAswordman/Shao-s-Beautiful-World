package com.shao.beautiful.manager.world.generate.boimes;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.mithrilmania.blocktopograph.util.Noise;
import com.shao.beautiful.tools.MathPatch;

public class NoiseUtil {
	private Noise noise0;
	private RandomNoise noise1;
	private Noise noise2;
	private Noise noise3;
	private Noise noise4;
	private Noise noise5;
	private Random random;

	public NoiseUtil(int seed) {
		random = new Random(seed);
		this.noise0 = new Noise(random.nextInt());
		this.noise1 = new RandomNoise(random.nextInt());
		this.noise2 = new Noise(random.nextInt());
		this.noise3 = new Noise(random.nextInt());
		this.noise4 = new Noise(random.nextInt());
		this.noise5 = new Noise(random.nextInt());
	}

	public double noise0(double x, double y) {
		return noise0.noise(x, y);
	}

	public double noise1(double x, double y) {
		return noise1.noise(x, y);
	}

	public double noise2(double x, double y) {
		return noise2.noise(x, y);
	}

	public double noise3(double x, double y) {
		return noise3.noise(x, y);
	}

	public double noise4(double x, double y) {
		return noise4.noise(x, y);
	}

	public double noise5(double x, double y) {
		return noise5.noise(x, y);
	}

	public static class RandomNoise {

		private ArrayList<Float> randomArr;
		private int max;

		public RandomNoise(int seed, int max) {
			this.max = max;
			this.randomArr = new ArrayList<Float>(max);
			Random random = new Random(seed);
			for (int i = 0; i < max; i++) {
				float f=random.nextFloat();
				
				randomArr.add(i<0.2*max?f/10:f);
			}

		}

		public double noise(double x, double y) {
			int ix = (int) Math.floor(x);
			int iy = (int) Math.floor(y);
			
			
			double fx = x - ix;
			double fy = y - iy;

			//x=2;
			//iy=2;
			double a = point(ix, iy);
			double b = point(ix + 1, iy);
			double c = point(ix, iy + 1);
			double d = point(ix + 1, iy + 1);

			double ux = fx * fx * (3.0 - 2.0 * fx);
			double uy = fy * fy * (3.0 - 2.0 * fy);
			
			ux=Math.pow(fx, 5);
			uy=Math.pow(fy, 5);

			return MathPatch.lerp(a, b, ux) + (c - a) * uy * (1.0 - ux) + (d - b) * uy * ux;
		}

		public double point(int x, int y) {
			return randomArr.get((int) Math.floor(MathPatch.random(x, y)*randomArr.size()));
		}

		public RandomNoise(int seed) {
			this(seed,256);
		}

	}
	public Random getRandom() {
		return random;
	}
}
