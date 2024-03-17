package bms.helper.tools;

public class DoubleKey<KX, KY> {
	private KX kx;
	private KY ky;

	public DoubleKey(KX kx, KY ky) {
		this.kx = kx;
		this.ky = ky;
	}
	@Override
	public int hashCode() {
		return kx.hashCode()/2+ky.hashCode()/2;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DoubleKey) {
			DoubleKey<KX, KY> key = (DoubleKey<KX, KY>) obj;
			if (key.kx == null || key.ky == null) {
				return key.kx == kx && key.ky == ky;
			}
			return key.kx.equals(kx) && key.ky.equals(ky);
		}
		return false;
	}
}
