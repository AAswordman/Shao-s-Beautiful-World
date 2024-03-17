package com.shao.beautiful.manager.world.generate.boimes;

public abstract class Boime {
	public NoiseUtil noise;

	public Boime(NoiseUtil noiseUtil) {
		this.noise = noiseUtil;
	}

	abstract public Boime getBoime(int x, int z, BoimeData data);

	protected int getBoimeColor(int x, int z, BoimeData data) {
		return 0xffffffff;
	}

	abstract public int getBoimeHeight(int x, int z, BoimeData data);
}
