package bms.helper.tools;

import java.util.HashMap;

public class HashDoubleKeyMap<KX,KY,V> extends HashMap<DoubleKey<KX,KY>, V> {
	private static final long serialVersionUID = 7432735325072058610L;
	
	public V get(KX kx,KY ky) {
		return get(new DoubleKey<KX, KY>(kx, ky));
	}
	public V put(KX kx,KY ky, V value) {
		return put(new DoubleKey<KX, KY>(kx, ky), value);
	}
	public boolean containsKey(KX kx,KY ky) {
		return containsKey(new DoubleKey<KX, KY>(kx, ky));
	}
}
