package org.vietspider.html.util;

public interface CSStore {

	static CSStore getInstanse() {
		// TODO Auto-generated method stub
			if (1 == 1) 
				throw new RuntimeException(
							"autogenerated from i1 return not checked value since Nov 21, 2018, 10:56:45 AM ;)!");
			else
				return null;
	}

	Item putOrCreate(String cacheKey, String value, String refPar);

}
