package com.shao.beautiful.manager.world.generate.boimes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.tools.MathPatch;

import bms.helper.tools.HashDoubleKeyMap;

public class WorldBoime extends Boime {
	private HashDoubleKeyMap<Integer, Integer, Float[][]> landVec2Array;
	private HashDoubleKeyMap<Integer, Integer, Float[][]> meteoriteVec2Array;
	private ArrayList<Float> landLoadArray;
	private ArrayList<Float> meteoriteLoadArray;
	private ArrayList<Float> probability;

	public final static int partGenerateFactor = 512;
	public final static int landGenerateFactor = partGenerateFactor * 10;
	public final static int landMaxSummon = 3;
	public final static int landGenerateMaxRang = 2560;
	public final static int landGenerateMinRang = 512;
	public final static int partGenerateMaxRang = 256;
	public final static int partGenerateMinRang = 160;
	public final static int partMaxSummon = 20;

	public WorldBoime(NoiseUtil noiseUtil) {
		super(noiseUtil);

		landVec2Array = new HashDoubleKeyMap<Integer, Integer, Float[][]>();
		meteoriteVec2Array = new HashDoubleKeyMap<Integer, Integer, Float[][]>();

		int size = 512;
		landLoadArray = new ArrayList<Float>(size);
		meteoriteLoadArray = new ArrayList<Float>(size);
		probability = new ArrayList<Float>(size);

		Random random = noiseUtil.getRandom();
		for (int i = 0; i < size; i++) {
			landLoadArray.add(random.nextFloat());
		}
		for (int i = 0; i < size; i++) {
			meteoriteLoadArray.add(random.nextFloat());
		}
		for (int i = 0; i < size; i++) {
			probability.add(random.nextFloat());
		}
	}

	@Override
	public Boime getBoime(int x, int z, BoimeData data) {
		float high = (float) getNoise(x, z);
		//System.out.println(high);
		data.altitudeMutiplier = Math.max((high-3f) / 10f,0);
		//System.out.println(data.altitudeMutiplier);
		data.altitude= high-3;
		if (high <= 0) {
			return null;
		}

		float high1 = (float) getNoise(x + 1, z + 1) / high - 1;
		float high2 = (float) getNoise(x - 1, z - 1) / high - 1;
		float high3 = (float) getNoise(x - 1, z + 1) / high - 1;
		float high4 = (float) getNoise(x + 1, z - 1) / high - 1;

		float vx = high1 - high2 - high3 + high4;
		float vz = high1 - high2 + high3 - high4;

		float to = (float) Math.sqrt(vx * vx + vz * vz);

		data.oceanDirection[0] = vx / to;
		data.oceanDirection[1] = vz / to;
		data.precipitation = Math.min((10-high) / 10f,0);
		return null;
	}

	@Override
	public int getBoimeHeight(int x, int z, BoimeData data) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getNoise(double x, double z) {
		int landX = (int) Math.floor(x / landGenerateFactor);
		int landZ = (int) Math.floor(z / landGenerateFactor);
		
		int partX = (int) Math.floor(x / partGenerateFactor);
		int partZ = (int) Math.floor(z / partGenerateFactor);
		
		ArrayList<Float[]> landData =new ArrayList<Float[]>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				makeSureLand(landX+i, landZ+j);
				landData.addAll(Arrays.asList(landVec2Array.get(landX+i, landZ+j)));
			}
		}
		
		
		
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				makeSurePart(landData, partX+i, partZ+j, x, z);
			}
		}
		
		ArrayList<Float[]> partDataArray = new ArrayList<Float[]>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				partDataArray.addAll(Arrays.asList(meteoriteVec2Array.get(partX+i, partZ+j)));
			}
		}
		Random pointRandom = getPointRandom(partX, partZ, probability);

		float height = pointRandom.nextFloat() * 0f;
		for (int i = 0; i < partDataArray.size(); i++) {
			Float[] data = partDataArray.get(i);
			double distance = getDistance(x, z, data[0], data[1]);
			if (distance > data[2]) {
				continue;
			}

			height += Math.cos(Math.PI * distance / data[2]) + 1f;

		}
		//System.out.println(height);
		return height;
	}
	private void makeSureLand(int landX,int landZ) {
		if (!landVec2Array.containsKey(landX, landZ)) {
			Random pointRandom = getPointRandom(landX, landZ, landLoadArray);
			int summonNum = floor(landMaxSummon * pointRandom.nextFloat());
			Float[][] landData = new Float[summonNum][3];
			for (int i = 0; i < summonNum; i++) {
				landData[i][0] = landX * landGenerateFactor + pointRandom.nextFloat() * landGenerateFactor;
				landData[i][1] = landZ * landGenerateFactor + pointRandom.nextFloat() * landGenerateFactor;
				landData[i][2] = pointRandom.nextFloat() * (landGenerateMaxRang - landGenerateMinRang)
						+ landGenerateMinRang;
			}
			landVec2Array.put(landX, landZ, landData);
		}
	}
	private void makeSurePart(ArrayList<Float[]> landData,int partX,int partZ,double x,double z) {
		if (!meteoriteVec2Array.containsKey(partX, partZ)) {
			Random pointRandom = getPointRandom(partX, partZ, meteoriteLoadArray);

			float factor = pointRandom.nextFloat()/3f;
			for (int i = 0; i < landData.size(); i++) {
				Float[] data = landData.get(i);
				double distance = getDistance(x, z, data[0], data[1]);
				if (distance > data[2]) {
					continue;
				}

				factor += Math.cos(Math.PI * distance / data[2]) + 1f;

			}

			int summonNum = floor(partMaxSummon * (factor));
			Float[][] partData = new Float[summonNum][3];
			for (int i = 0; i < summonNum; i++) {
				partData[i][0] = partX * partGenerateFactor + pointRandom.nextFloat() * partGenerateFactor;
				partData[i][1] = partZ * partGenerateFactor + pointRandom.nextFloat() * partGenerateFactor;
				partData[i][2] = pointRandom.nextFloat() * (partGenerateMaxRang - partGenerateMinRang)
						+ partGenerateMinRang;
			}
			meteoriteVec2Array.put(partX, partZ, partData);
		}
	}
	private double getDistance(double x, double z, double x2, double y2) {
		return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(z - y2, 2));

	}

	private int floor(float f) {
		return (int) Math.floor(f);
	}

	private Random getPointRandom(int x, int z, ArrayList<Float> array) {
		return new Random((long) (Long.MAX_VALUE * array.get(floor(MathPatch.random(x, z) * array.size()))));
	}
}
