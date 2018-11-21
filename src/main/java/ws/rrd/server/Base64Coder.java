package ws.rrd.server;

import java.util.Base64;

public class Base64Coder {

	public static byte[] encode(byte[] bytes) {
		 return Base64.getEncoder().encode(bytes);
	}

	public static byte[] decode(char[] charArray) {
		return Base64.getDecoder().decode(new String(charArray));
	}

}
