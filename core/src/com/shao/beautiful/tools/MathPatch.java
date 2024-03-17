package com.shao.beautiful.tools;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import static java.lang.Math.*;

import java.util.Random;

public class MathPatch {
	public static float floatAccuracyFix(float f) {
		return Math.round(f * 100000l) / 100000f;
	}

	public static Vector3 vec3AccuracyFix(Vector3 v) {
		return v.set(floatAccuracyFix(v.x), floatAccuracyFix(v.y), floatAccuracyFix(v.z));
	}

	public static float toRad(double deg) {
		return (float) (deg / 180f * Math.PI);
	}

	public static double sin(float deg) {
		return Math.sin(toRad(deg));
	}

	public static double cos(double d) {
		return Math.cos(toRad(d));
	}

	public static double tan(double deg) {
		return Math.tan(toRad(deg));
	}

	public static Vector3 limit(Vector3 a, Vector3 b, Vector3 c) {
		b.x = limit(a.x, b.x, c.x);
		b.y = limit(a.y, b.y, c.y);
		b.z = limit(a.z, b.z, c.z);
		return b;
	}

	public static float limit(float a, float b, float c) {
		if (a > c) {
			return limit(c, b, a);
		}
		return Math.min(c, Math.max(a, b));
	}

	public static float mod(float a, float b) {
		return (float) (a - Math.floor(a / b) * (b));

	}

	public static float random(long x, long y) {
		Random rx = new Random(x);
		Random ry = new Random(y);
		ry.nextFloat();
		rx.nextFloat();
		ry.nextFloat();
		return new Random((long) (Long.MAX_VALUE * ((rx.nextFloat() + ry.nextFloat()) / 2))).nextFloat();
	}

	public static double lerp(double fromValue, double toValue,double progress) {
		return fromValue + (toValue - fromValue) * progress;

	}
}
