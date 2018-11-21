package eu.blky.net.http;

import java.util.List;
import java.util.Map;

import org.htmlparser.http.Cookie;

public interface HttpSession {

	Map<String, List<Cookie>> getAttribute(String cookiesStore);

	void setAttribute(String cookiesStore, Map<String, List<Cookie>> mCookieJar);

}
