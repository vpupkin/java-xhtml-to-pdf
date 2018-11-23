package eu.blky.net.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.token.attribute.Attribute;
import org.xhtmlrenderer.simple.PDFRenderer;
 
import cc.co.llabor.cache.Manager;
import net.sf.jsr107cache.Cache;
import ws.rrd.server.LCacheEntry;
import ws.rrd.server.LServlet;
import cc.co.llabor.cache.MemoryFileItem; 
import cc.co.llabor.cache.css.CSStore;
import cc.co.llabor.cache.js.Item;
import cc.co.llabor.cache.js.JSStore; 

 
import static org.junit.Assert.*;

class UrlFetchTestTest {
	private static final String MYLOCALTESTENVIROMENT = "mylocaltestenviroment";
	private static final Logger log = LoggerFactory.getLogger(UrlFetchTest.class.getName());
	private static final boolean TRACE = true;
	private static final String CHARSET_PREFIX = "charset=";

	private String SwapServletUrl =
			"local".equals(System.getProperty(MYLOCALTESTENVIROMENT)) ? 
					"http://localhost:8888/l/" 			: 
					"https://rrdsaas.appspot.com/l/"; // prod

	static Server server ;
	private static URI serverUri;
	
	@AfterClass
	public static void stopJetty() {
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeAll
	static void  startServer() throws Exception {
		System.setProperty(UrlFetchTestTest.MYLOCALTESTENVIROMENT, "local");
		serverUri=new URI("http://localhost:8888");
		// Create Server
		server = new Server(8888);
		ServletContextHandler context = new ServletContextHandler();

		DefaultServlet ds = new DefaultServlet();
		ServletHolder defaultServ = new ServletHolder("default", ds);
		defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
		defaultServ.setInitParameter("dirAllowed", "true");
		context.addServlet(defaultServ, "/");
		server.setHandler(context);

		// Start Server
		server.start();
	}

	String timebaseTmp = "TMP" + System.currentTimeMillis();
	
    @Test
    public void testGet() throws Exception
    {
    	getCache().put("/index.htm", "yyyyxxxxxxxxxxxxxxxxxyyy");
        // Test GET
        HttpURLConnection http = (HttpURLConnection) serverUri.resolve("/index.htm").toURL().openConnection();
        http.connect();
        InputStream isTmp = http.getInputStream();
        
        assertEquals("Response Code", http.getResponseCode(), 200 );
        String htmldataTmp = IOUtils.toString(isTmp);
        assertTrue( "body", htmldataTmp.indexOf("xxx")>=0 );
        
        
    }	
    // initiate URL to fetch
    static String [] urls ={
			"https://en.wikipedia.org/wiki/Al-Samakiyya",
			"https://en.wikipedia.org/wiki/Ret_finger_protein_like_4B",
			"https://en.wikipedia.org/wiki/John_III,_Count_of_Dreux",
			"https://en.wikipedia.org/wiki/Pathein_Airport"
			
	};
	static String url = urls[(int) (urls.length*Math.random())];
	//		"https://en.wikipedia.org/wiki/Special:Random"; 
	// url = "http://xmlsoft.org/xmllint.html";
	// url = "https://habr.com/company/dataart/blog/430514/";
//	@BeforeAll
//	static void initURL() throws ClientProtocolException, IOException{ 
//		// plain fetch
//		UrlFetchTest f = new UrlFetchTest();
//		HttpResponse o = f.fetchGetResp(url); 
//		System.out.println(o.toString());
//		HttpEntity entity = o.getEntity();
//		System.out.println(entity);
//    }
	
	@Test
	void testXHTML() throws Exception { 
		// html fetch-> xmlhtml->file->pdf
		String theString = getAsXHTML(url);
		File xhtmlTempFile = File.createTempFile(timebaseTmp, ".xhtml");
		Writer xhtmlTmp = new FileWriter(xhtmlTempFile);
		IOUtils.write(theString, xhtmlTmp);
		xhtmlTmp.close();
		PDFRenderer.renderToPDF(xhtmlTempFile, "target/tmp/XHTML.pdf");
		// PDFRenderer.renderToPDF(url, pdf);

	}
	
	@Test
	void testPLAIN() throws Exception {
		UrlFetchTest htmlFetcherTmp = new UrlFetchTest();
		File createTempFile = File.createTempFile(timebaseTmp, ".html");
		Writer htmlTmp = new FileWriter(createTempFile);
		HttpResponse o = htmlFetcherTmp.fetchGetResp(url);
		HttpEntity entity = o.getEntity();
		String htmldataTmp = IOUtils.toString(entity.getContent());
		IOUtils.write(htmldataTmp, htmlTmp);
		System.out.println("plain html: " + htmldataTmp);
		htmlTmp.close();
		PDFRenderer.renderToPDF(url, "target/tmp/phtml.pdf"); 
	}	

	// public void doGetPost(HttpServletRequest req, HttpServletResponse resp)
	// throws IOException {
	public String getAsXHTML(String urlStr) throws Exception {
		ByteArrayOutputStream myBAOS = new ByteArrayOutputStream();
		HttpServletResponse resp = getHttpResponse(myBAOS);
		//
		StringBuilder targetUrl = null;
		//
		// ServletOutputStream outTmp = null;
		// String contextTypeStr = null ;
		// byte[] dataBuf =null;
		// HTMLDocument documentTmp = null;
		// String urlStr = null;
		// try {
		// StringBuffer requestURL = req.getRequestURL();
		StringBuffer requestURL = new StringBuffer(urlStr);
		// String rurlTmp = ""+req.getRequestURL()+"";
		// final String baseURL = System .getProperty("l.baseURL");
		// String decodedUrl = rurlTmp;
		// if (baseURL == null){
		// SwapServletUrl = rurlTmp.substring(0,
		// rurlTmp.indexOf(req.getServletPath()+"/") )+req.getServletPath()+"/";
		// decodedUrl = requestURL.substring( SwapServletUrl.length());
		// }else{// redefine server/host-based intra.PEGA001.fidu.com -> GLOBaserv.com
		// SwapServletUrl = baseURL;
		// decodedUrl = requestURL.substring( requestURL.lastIndexOf(getMYALIAS()) +
		// getMYALIAS().length() );
		//
		// }

		String[] decodedUrls;
		// int ind=0;
		// try{
		// if (TRACE){System_out_println("DECODE :"+decodedUrl);}
		// if (decodedUrl.indexOf("/")>0 &&
		// HyperLinkUtil.decode(decodedUrl).length()==0){
		// String []urls = decodedUrl.split("/");
		// decodedUrls = new String[urls.length];
		// for (String urlTmp:urls){
		// urlStr = HyperLinkUtil.decode(urlTmp);
		// decodedUrls[ind++]=urlStr;
		// }
		// }else{
		// urlStr = HyperLinkUtil.decode(decodedUrl);
		//
		// }
		// }catch(Throwable e){
		// if (TRACE){e.printStackTrace();}
		// outTmp = resp.getOutputStream();
		// PrintWriter pw = new PrintWriter(outTmp, true);
		// pw.println( requestURL);
		// pw.println( contextTypeStr);
		// pw.println( SwapServletUrl);
		// pw.println( decodedUrl);
		//
		// e.printStackTrace(pw);
		// }
		// fix ROOT-Panel-Request
		// if (isRootReq(req) ){
		// urlStr = req.getParameter(_U_R_L_) ;
		// }

		// normalize non-protocol-ADDRESS
		urlStr = ("" + urlStr).startsWith("http") ? urlStr : "http://" + urlStr;
		// checkBlack(decodedUrl, req);
		// checkBlack(urlStr, req);
		//
		// if (TRACE) System_out_println(_U_R_L_ + " := "+ urlStr);
		targetUrl = new StringBuilder(urlStr);

		// if ((targetUrl.length() > 0) && (req.getQueryString() != null) &&
		// (req.getQueryString().length() > 1)) {
		// if (targetUrl.toString().endsWith("?"))
		// targetUrl.append(String.format("%s", req.getQueryString()));
		// else
		// targetUrl.append(String.format("?%s", req.getQueryString()));
		//
		// urlStr = targetUrl.toString();
		// }

		// String[][] headsToResend = calcRequestHeaders(req);
		String[][] headsToResend = calcRequestHeaders();

		// if (!urlStr.startsWith("http")) {
		// // fix via REFeRER
		// try{
		// URL refURL = new URL ( HyperLinkUtil.decode(
		// req.getHeader("Referer").substring(SwapServletUrl.length()) ) );
		// urlStr = refURL.getProtocol() + "://"+refURL.getHost() +
		// "/"+(urlStr.startsWith("/")?"":refURL.getPath()+"/../")+urlStr;
		// }catch(Throwable e){}
		// }
		// urlStr = urlStr.replace(" ", "%20").replace("\t", "%090");
		// //
		// http://it-ru.de/forum/viewtopic.php?t=182374&amp;postdays=0&amp;postorder=asc&amp;start=15
		// urlStr = urlStr.replace("&amp;", "&");
		// HttpSession sessionTmp = req.getSession();
		//
		UrlFetchTest urlFetcherTmp = getInstance();
		// UrlFetchTest urlFetcherTmp = (UrlFetchTest)
		// sessionTmp.getAttribute("UrlFetcher");
		// if (urlFetcherTmp == null){
		// urlFetcherTmp = new UrlFetchTest(sessionTmp);
		// sessionTmp.setAttribute("UrlFetcher",urlFetcherTmp);
		// }

		HttpResponse xRespTmp = null;
		Cache getCache = getCache();

		ServletOutputStream outTmp;
		// if ("POST".equals(req.getMethod()) && !isRootReq(req)) {
		if ("POST".equals("notsupprted")) {
			List<MemoryFileItem> items = null;
			// if
			// (org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(req))
			// {
			// if (11==1) {
			//
			// MemoryFileItemFactory factory = MemoryFileItemFactory.getInstance();
			// org.apache.commons.fileupload.servlet.ServletFileUpload upload = new
			// org.apache.commons.fileupload.servlet.ServletFileUpload(
			// factory);
			// upload.setSizeMax(4 * 1024 * 1024); // 4 MB
			//
			// // Parse the request
			// items = upload.parseRequest(req);
			// }
			// Map parameterMap = req.getParameterMap();
			// xRespTmp = urlFetcherTmp.fetchPostResp(urlStr, headsToResend, parameterMap,
			// items);
			// // HOTFIX for Login-redirect
			// if (xRespTmp.getLastHeader("X-MOVED") != null) {
			// urlStr = "" + xRespTmp.getLastHeader("X-MOVED").getValue();
			// }
		} else { // GET
			// urlStr = checkGOTO(urlStr);
			try {
				String keyTmp = calcCackeKey(urlStr);
				Object dataTmp = null;
				try {
					dataTmp = getCache.get(keyTmp);
				} catch (Exception e) {
					e.printStackTrace();
					/* ignore any gae-limits, if any */}

				if (dataTmp == null) {
					xRespTmp = urlFetcherTmp.fetchGetResp(urlStr, headsToResend);

				} else if (dataTmp instanceof LCacheEntry
						&& ((LCacheEntry) dataTmp).getExpired() > System.currentTimeMillis()) {
					// write cached !
					LCacheEntry theItem = (LCacheEntry) dataTmp;
					resp.setContentType(theItem.getCxType());
					outTmp = resp.getOutputStream();
					outTmp.write(theItem.getBytes());
					return myBAOS.toString();
				} else {
					try {
						getCache.remove(urlStr);
						dataTmp = null;
						xRespTmp = urlFetcherTmp.fetchGetResp(urlStr, headsToResend);
					} catch (Exception e) {
						e.printStackTrace();
						/* ignore any gae-limits, if any */}
				}
			} catch (ClientProtocolException e) {
				log.error("URL{" + urlStr + "}::", e);
				// last try
				xRespTmp = urlFetcherTmp.fetchGetResp(urlStr + "/", headsToResend);
			}
		}
		final StatusLine statusLine = xRespTmp.getStatusLine();
		try {
			String wwwAuthTmp = xRespTmp.getHeaders("WWW-Authenticate")[0].getValue();
			wwwAuthTmp = wwwAuthTmp.lastIndexOf("\"") == wwwAuthTmp.length() - 1
					? wwwAuthTmp.substring(0, wwwAuthTmp.length() - 1) + "@" + targetUrl + "\""
					: wwwAuthTmp;
			if (statusLine.getStatusCode() == 401) {
				resp.setStatus(401);
				resp.setHeader("WWW-Authenticate", wwwAuthTmp);
				return myBAOS.toString();
			} else if (statusLine.getStatusCode() == 301) {
				resp.setStatus(301);
				resp.setHeader("WWW-Authenticate", wwwAuthTmp);
				return myBAOS.toString();
			} else if (statusLine.getStatusCode() == 302) {
				resp.setStatus(302);// xRespTmp.getAllHeaders()
				resp.setHeader("Location", requestURL.toString());
				return myBAOS.toString();
			} else if (statusLine.getStatusCode() == 303) {
				resp.setStatus(303);
				resp.setHeader("WWW-Authenticate", wwwAuthTmp);
				return myBAOS.toString();
			} else if (statusLine.getStatusCode() == 304) {
				resp.setStatus(304);
				resp.setHeader("WWW-Authenticate", wwwAuthTmp);
				return myBAOS.toString();
			} else if (statusLine.getStatusCode() == 305) {
				resp.setStatus(305);
				resp.setHeader("WWW-Authenticate", wwwAuthTmp);
				return myBAOS.toString();
			}
		} catch (Exception e) {
			if (TRACE)
				log.trace("WWW-Authenticate", e);
		}
		HttpEntity entity = xRespTmp.getEntity();
		String contextTypeStr = "" + entity.getContentType();
		String contextEncStr = "" + entity.getContentEncoding();
		contextEncStr = "null".equals(contextEncStr) ? getXEnc(xRespTmp) : contextEncStr;
		if ("null".equals("" + contextEncStr) && contextTypeStr.toLowerCase().startsWith("content-type: text/html")) {
			int encPos = contextTypeStr.toLowerCase().indexOf(CHARSET_PREFIX);
			if (encPos > 0) {

				contextEncStr = contextTypeStr.substring(encPos + CHARSET_PREFIX.length());
				contextEncStr = contextEncStr.toUpperCase();
				if (TRACE)
					log.warn(contextEncStr + "  }} ENC :=  {  " + contextTypeStr + " } ::: enc ::: " + contextEncStr
							+ "[" + urlStr + "]");

			} else {

				Header[] contextEncHeaders = xRespTmp.getHeaders("Content-Encoding");
				try {
					contextEncStr = contextEncHeaders[0].getValue();
				} catch (Throwable e) {
				}
				if (TRACE)
					log.warn("Content-Encoding[0]::== {" + contextEncStr + "  }");

			}
		} else {
			if (TRACE)
				log.warn("nonull::::" + contextEncStr + " ::::: " + contextTypeStr);
		}

		if (isCSS(contextTypeStr)) {
			setupResponseProperty(resp, xRespTmp);
			outTmp = performCSS(resp, contextTypeStr, urlStr, xRespTmp, entity, contextEncStr);
			return myBAOS.toString();
		} else if (isBinary(contextTypeStr)) {
			setupResponseProperty(resp, xRespTmp);
			outTmp = performBinary(resp, contextTypeStr, urlStr, xRespTmp, entity, contextEncStr);
			return myBAOS.toString();
		} else if (isScript(contextTypeStr)) {
			setupResponseProperty(resp, xRespTmp);
			outTmp = performScript(resp, contextTypeStr, urlStr, entity, contextEncStr);
			return myBAOS.toString();

		} else {

			String xEncTmp = getXEnc(xRespTmp);
			if (TRACE)
				log.warn("x---HTML--- x  contextTypeStr/contextEncStr:" + contextTypeStr + " : :  enc : : "
						+ contextEncStr + "[" + urlStr + "]   XXX::" + xEncTmp);
			if (TRACE)
				log.warn("=====!!!======" + contextTypeStr + "::::" + contextEncStr);
		}

		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		entity.writeTo(oaos);
		oaos.flush();
		oaos = unZIP(xRespTmp, contextEncStr, oaos);
		// contextEncStr = calcContextEnc(req, xRespTmp, oaos);
		String data = null;
		try {
			data = oaos.toString(contextEncStr);// xCSS.toUpperCase().substring( 12430)
		} catch (Exception e) {
			data = oaos.toString();
			oaos.toString("utf-8");
		} // data.substring( data.indexOf("&lt;") -100, data.indexOf("&lt;") +20);

		if ("null".equals("" + contextEncStr) && data.toLowerCase().indexOf("content=\"text/html") > 0)
			try {
				String contextText = "charset=";
				int lenTmp = contextText.length();
				int posTmp = data.toLowerCase().indexOf(contextText);
				int beginIndex = posTmp + lenTmp;
				int endIndex = beginIndex + data.toLowerCase().substring(beginIndex).indexOf("\"");
				contextEncStr = data.toLowerCase().substring(beginIndex, endIndex);
				contextEncStr = contextEncStr.toUpperCase();
				data = oaos.toString(contextEncStr);
			} catch (Throwable e) {
			}
		byte[] dataBuf;
		try {
			dataBuf = data.trim().getBytes(contextEncStr);// "utf-8"
		} catch (Exception e) {
			contextEncStr = null;
			dataBuf = data.trim().getBytes();// "ISO-8859-1"
		}
		HTMLParser2 parser2 = new HTMLParser2();
		HTMLDocument documentTmp;
		try {
			documentTmp = parser2.createDocument(dataBuf, contextEncStr);// "utf-8"
		} catch (Exception e) {
			if (TRACE)
				log.warn("createDocument EXCEPTION!" + e.getMessage() + " contextTypeStr||contextEncStr:["
						+ contextTypeStr + "||" + contextEncStr + "]  URL =:[" + urlStr + "]");
			documentTmp = parser2.createDocument(dataBuf, null);// "utf-8"
		}

		URL realURL = new URL(urlStr); // new String( oaos.toString(contextEncStr).getBytes(), contextEncStr)

		HTMLNode rootTmp = documentTmp.getRoot();
		// if ("true".equals(sessionTmp.getAttribute(BEAUTIFY))) {
		// rootTmp.setBeautify(true);
		// } else {
		// rootTmp.setBeautify(false);
		// }
		rootTmp.setBeautify(true);

		LServlet.testCreateFullLink(rootTmp, SwapServletUrl, realURL);
		// testCreateImageLink(documentTmp.getRoot(), SwapServletUrl, realURL);
		LServlet.testCreateMetaLink(rootTmp, SwapServletUrl, realURL);
		LServlet.testCreateScriptLink(rootTmp, SwapServletUrl, realURL);
		LServlet.testCreateStyleLink(rootTmp, SwapServletUrl, realURL);

		int beginIndex = contextTypeStr.toUpperCase().indexOf(" ") + 1;

		setupResponseProperty(resp, xRespTmp);
		String cxType = contextTypeStr.substring(beginIndex);
		resp.setContentType(cxType);
		if (!"null".equals("" + contextEncStr)) {
			resp.setCharacterEncoding(contextEncStr);
		}
		outTmp = resp.getOutputStream();
		String textValue = null;
		// wrap
		// try {
		// // documentTmp.getRoot().getChild(0).getByXPath("BODY");
		// HTMLNode headTmp = null;
		// HTMLNode baseTmp = null;
		// for (HTMLNode nodeTmp : documentTmp.getRoot().getChild(0).getChildren()) {
		// String nodeNameTmp = nodeTmp.getName().name();
		// if ("HEAD".equals(nodeNameTmp)) {
		// headTmp = nodeTmp;
		// }
		// if ("BASE".equals(nodeNameTmp)) {
		// baseTmp = nodeTmp;
		// break;
		// }
		// }
		// if (baseTmp != null) {// getTextValue()
		// System.out.println(baseTmp.getTextValue());
		// String avalTmp = baseTmp.getAttributes().get("href").getValue();
		// baseTmp.getAttributes().remove("href");
		// Attribute hrefTmp = new Attribute("href", "" +
		// HyperLinkUtil.encode(SwapServletUrl, avalTmp));
		// baseTmp.getAttributes().add(hrefTmp);
		// System.out.println(baseTmp.getTextValue());
		//
		// } else {
		// System.out.println(headTmp);
		// }
		// TODO remove it
		// HTMLNode bodyTmp = documentTmp.getRoot().getChild(0); // HEAD-modi!
		// HTMLDocument htmlTmp = buildToolbar(urlStr, parser2);
		// HTMLNode myIFrame = htmlTmp.getRoot().getChild(1).getChild(0);
		// bodyTmp.addChild(0, myIFrame);
		// } catch (Exception e) {
		// if (TRACE)
		// log.trace("wrap", e);
		// include(resp, "L.jspX");
		// }

		// HOTFIX for XML Parsing Error: prefix not bound to a namespace
		// <use xlink:href="https://habr...

		Attribute svgNS = new Attribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		documentTmp.getRoot().getChild(0).getParent().getAttributes().add(svgNS);

		// DuD :remove footer....
		try {
			// --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
			// // --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
			// // --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
			// // --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
			// // --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
			// // --1
			// documentTmp.getRoot().getChild(1).removeChild(
			// documentTmp.getRoot().getChild(1).getChild(documentTmp.getRoot().getChild(1).getChildren().size()-1)
			// );
		} catch (Throwable e) {
			e.printStackTrace();
		}

		textValue = renderDocument(documentTmp, contextEncStr);
		byte[] bytesTmp = null;
		if (!"null".equals("" + contextEncStr)) {
			bytesTmp = textValue.getBytes(contextEncStr);

		} else {
			bytesTmp = textValue.getBytes();
		}
		outTmp.write(bytesTmp);
		// and cache it! // String cxType = contextTypeStr.substring(beginIndex);
		cacheIt(urlStr, getCache, bytesTmp, cxType);

		// }catch( BlackListedException e){
		// goToGoooo(resp);
		//
		// } catch (java.lang.NoClassDefFoundError e) {
		// if (TRACE) System_out_println(contextTypeStr +" ===============
		// "+e.getMessage());e.printStackTrace();
		// if (TRACE) System_out_println(documentTmp);
		// } catch (Exception e) {
		// processException(req, resp, targetUrl, e);
		//
		// }

		return myBAOS.toString();
	}

	private UrlFetchTest getInstance() {
		UrlFetchTest retval = new UrlFetchTest();
		return retval;

	}

	private HttpServletResponse getHttpResponse(ByteArrayOutputStream oaosPar) {
		HttpServletResponse retval = new FakeHttpServletResponse(oaosPar);
		return retval;
	}

	private String[][] calcRequestHeaders() {
		String[][] retval = new String[][] {};
		return retval;
	}

	public static void System_out_print(String txt) {
		log.trace(txt);
	}

	public static void System_out_println(Object txt) {
		log.trace("" + txt);
	}

	public static void System_out_println(String txt) {
		log.trace(txt);
	}

	public static String calcCackeKey(String urlStr) {
		String key = urlStr;
		key = key.lastIndexOf("/") - key.indexOf("://") < 5 ? key + "/.!" : key;
		key = key.lastIndexOf("/") == key.length() - 1 ? key + ".!" : key;
		return key;
	}

	private static String getXEnc(HttpResponse respTmp) {
		String retval = null;
		try {
			retval = respTmp.getHeaders("Content-Encoding")[0].getValue();
		} catch (Exception e) {
			try {
				String retvalTmp = respTmp.getHeaders("Content-Type")[0].getValue();
				// for ex. [Content-Type: text/html; charset=windows-1251]
				int beginIndex = retvalTmp.toLowerCase().indexOf("charset=") + "charset=".length();

				retval = ""
						+ Charset.availableCharsets().get(retvalTmp.substring(beginIndex).toUpperCase()).displayName();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return retval;
	}

	static boolean isCSS(String contextTypeStr) {
		return "Content-Type: text/css".equalsIgnoreCase(contextTypeStr)
				|| ("" + contextTypeStr).toLowerCase().indexOf("text/css") > 0;
	}

	static boolean isBinary(String contextTypeStr) {
		return "null".equalsIgnoreCase(contextTypeStr) || "Content-Type: image/jpeg".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/png".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/x-icon".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: text/xml".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/gif".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/pdf".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/x-shockwave-flash".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/postscript".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/octet-stream".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/x-msexcel".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/tiff".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/ief".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: image/g3fax".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/x-shockwave-flash".equalsIgnoreCase(contextTypeStr)
				|| ("" + contextTypeStr).indexOf("application/") >= 0 || ("" + contextTypeStr).indexOf("text/xml") >= 0

		;
	}

	static boolean isScript(String contextTypeStr) {
		return

		"Content-Type: text/javascript".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/javascript".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/x-javascript".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/javascript; charset=utf-8".equalsIgnoreCase(contextTypeStr)
				|| "Content-Type: application/x-javascript; charset=utf-8".equalsIgnoreCase(contextTypeStr) ||
				// "content-type: text/html; charset=ISO8859-1".equalsIgnoreCase(
				// contextTypeStr) ||
				"content-type: text/javascript; charset=UTF-8".equalsIgnoreCase(contextTypeStr)
				|| ("" + contextTypeStr).toLowerCase().indexOf("text/javascript") >= 0
				|| ("" + contextTypeStr).toLowerCase().indexOf("application/javascript") >= 0
				|| ("" + contextTypeStr).toLowerCase().indexOf("application/x-javascript") >= 0

		;
	}

	static final String headersToSet[] = {
			// "Content-Type",
			"Content-Language",
			// "Content-Encoding",
			"Content-Disposition", // : attachment; filename=Personalakte.pdf
			"filename", "Date", "Last-Modified", "Accept", "Accept-Charset", "Accept-Language",
			// "Accept-Encoding",
			"Referer",
			// "Cookie",
			"Cache-Control", "User-Agent", "Cookie2",
			// "X-Powered-By", //: ASP.NET
			// "X-AspNet-Version",//: 2.0.50727
			"Expires", "TE", "Server",
			// "Set-Cookie",
			"Keep-Alive", "Authorization"

	};

	/**
	 * copy headers FROM:bPar TO: aPar
	 * 
	 * @author vipup
	 * @param aPar
	 * @param bPar
	 * @throws IOException
	 */
	protected static void setupResponseProperty(HttpServletResponse aPar, HttpResponse bPar) throws IOException {
		for (String headerName : headersToSet)
			for (Header next : bPar.getHeaders(headerName)) {
				String name = next.getName();
				String value = next.getValue();
				aPar.setHeader(name, value);
				log.debug("${}->{}", name, value);
			}

	}

	private ServletOutputStream performBinary(HttpServletResponse resp, String contextTypeStr, String urlStr,
			HttpResponse xRespTmp, HttpEntity entity, String contextEncPar) throws IOException {
		ServletOutputStream outTmp;
		if (!"null".equals(contextTypeStr)) {
			String contypeTmp = contextTypeStr.substring("Content-Type:".length());
			resp.setContentType(contypeTmp);
			setupResponseProperty(resp, xRespTmp);
		}
		// log.warning("HTML
		// contextTypeStr||contextEncStr:["+contextTypeStr+"||"+contextEncStr+"] URL
		// =:["+urlStr+"]");

		String contextEncStr = "" + entity.getContentEncoding();
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		entity.writeTo(oaos);
		oaos = unZIP(xRespTmp, contextEncStr, oaos);

		outTmp = resp.getOutputStream();
		oaos.writeTo(outTmp);
		outTmp.flush();
		outTmp.close();
		cacheIt(urlStr, getCache(), oaos.toByteArray(), contextTypeStr);
		return outTmp;
	}

	private ServletOutputStream performScript(HttpServletResponse resp, String contextTypeStr, String urlStr,
			HttpEntity entity, String contextEncPar) throws IOException {
		ServletOutputStream outTmp;
		if (TRACE)
			log.warn("JS contextTypeStr||contextEncStr:[" + contextTypeStr + "||" + contextEncPar + "]  URL =:["
					+ urlStr + "]");

		JSStore ssTmp = JSStore.getInstanse();
		Item scriptTmp = ssTmp.getByURL(urlStr);
		if (scriptTmp == null) {
			ByteArrayOutputStream oaos = new ByteArrayOutputStream();
			entity.writeTo(oaos);
			String jsToWrap = oaos.toString("UTF-8");
			// (new Element('li', { 'class': 'fav', 'html': ((empty && i==0) ? '' : ', ')
			// + '<a
			// href="hTTp://rrdsaas.appspot.com/F/h_t_t_p_://rrdsaas.appspot.com/l//HtTp/' +
			// temp.user.login + '.' + temp.base_short + '/favorites/tag/' + tag + '">' +
			// tag + '</a>'}
			String FServletURL = SwapServletUrl.replace("/l/", "/F/");
			jsToWrap = JSStore.performFormatJS(urlStr, jsToWrap);
			jsToWrap = jsToWrap.replace("http://", FServletURL + "h_t_t_p_://")
					.replace("HTTP://", FServletURL + "h_t_t_p_://").replace("HTTPS://", FServletURL + "h_t_t_p_s_://")
					.replace("https://", FServletURL + "h_t_t_p_s_://");

			scriptTmp = ssTmp.putOrCreate(urlStr, jsToWrap, urlStr);
		}
		resp.setContentType("application/javascript; charset=utf-8");
		outTmp = resp.getOutputStream();
		String scriptValueTmp = scriptTmp.getValue();
		byte[] bytesTmp = scriptValueTmp.getBytes("UTF-8");
		outTmp.write(bytesTmp);

		// and cache it! // String cxType = contextTypeStr.substring(beginIndex);
		cacheIt(urlStr, bytesTmp, contextTypeStr);
		outTmp.flush();
		return outTmp;
	}

	private ServletOutputStream performCSS(HttpServletResponse resp, String contextTypeStr, String urlStr,
			HttpResponse xRespTmp, HttpEntity entity, String contextEncPar) throws IOException {
		ServletOutputStream outTmp;

		if (TRACE)
			log.warn("CSS contextTypeStr / contextEncStr:{" + contextTypeStr + " / " + contextEncPar + "}, url== ["
					+ urlStr + "]");
		CSStore store = CSStore.getInstanse();
		cc.co.llabor.cache.css.Item itemTmp = store.getByURL(urlStr);
		String xCSS = null;
		if (itemTmp == null) {

			ByteArrayOutputStream oaos = new ByteArrayOutputStream();
			entity.writeTo(oaos);
			if (isGZip(xRespTmp)) {
				oaos = deZip(oaos);
				// contextEncStr = "ISO-8859-1";
			}
			xCSS = oaos.toString();
			xCSS = LServlet.justifyCSS(urlStr, xCSS);

			String refPar = urlStr;
			if (1 == 2)
				try {
					xRespTmp.getHeaders("Refferer")[0].getValue();
				} catch (Throwable e) {
				}
			store.putOrCreate(urlStr, xCSS, refPar);
		} else {
			xCSS = itemTmp.getValue();
		}

		resp.setContentType("text/css");
		outTmp = resp.getOutputStream();
		byte[] bytesTmp = xCSS.getBytes();
		outTmp.write(bytesTmp);
		outTmp.flush();
		// and cache it! // String cxType = contextTypeStr.substring(beginIndex);
		cacheIt(urlStr, bytesTmp, contextTypeStr);

		// store.putOrCreate(urlStr, xCSS, urlStr);
		return outTmp;
	}

	private static void cacheIt(String urlStr, byte[] bytesTmp, String contextTypeStr) {
		cacheIt(urlStr, getCache(), bytesTmp, contextTypeStr);
	}

	public static void cacheIt(String urlStr, Cache getCache, byte[] bytesTmp, String cxType) {
		String key = calcCackeKey(urlStr);
		try {
			LCacheEntry newData = new LCacheEntry(key, bytesTmp, cxType);
			getCache.put(key, newData);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static Cache getCache() {
		Cache getCache = Manager.getCache("getCache@" + UrlFetchTestTest.class.getName());
		return getCache;
	}

	private static boolean isGZip(HttpResponse xRespTmp) {
		boolean retval = false;
		try {
			retval = "gzip".equals(xRespTmp.getHeaders("Content-Encoding")[0].getValue());
		} catch (Throwable e) {
		}
		return retval;
	}

	private ByteArrayOutputStream deZip(ByteArrayOutputStream oaos) throws IOException {
		oaos.close();

		ByteArrayInputStream gzippeddata = new ByteArrayInputStream(oaos.toByteArray());
		GZIPInputStream zipin = new GZIPInputStream(gzippeddata);
		byte[] buf = new byte[16 * 1024]; // size can be
		int len;
		oaos = new ByteArrayOutputStream();
		try {
			while ((len = zipin.read(buf)) > 0) {
				oaos.write(buf, 0, len);
			}
		} catch (IOException e) {
			// System.out.println(new String(oaos.toByteArray()));
			if (oaos.size() > 0)
				return oaos;
		}
		return oaos;
	}

	private ByteArrayOutputStream unZIP(HttpResponse xRespTmp, String contextEncStr, ByteArrayOutputStream oaos)
			throws IOException {
		if ("gzip".equals(contextEncStr) || isGZip(xRespTmp)) {
			oaos = deZip(oaos);
		}
		return oaos;
	}

	/**
	 * @author vipup
	 * @param urlStr
	 * @param parser2
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	private HTMLDocument buildToolbar(String urlStr, HTMLParser2 parser2)
			throws IOException, URISyntaxException, Exception {
		// CACHING is disabled - TODO check possibility to modify only one ATTRIBUTE
		// from object.
		// at the moment much faster(for CPU as well as implementation) goes to build
		// new commonToolbar as modify Memory-cached-Obj.
		// if (1==2 && commonToolbar!=null){
		// HTMLNode urlTextTmp = commonToolbar.getRoot().getById("I01lOO10lOO11I");
		// urlTextTmp.getAttributes().get("value").setValue(urlStr );
		// urlTextTmp.setValue(urlTextTmp.getTextValue().toCharArray());
		// return commonToolbar;
		// }
		String strTmp = "<body><div  name='toolbar'>" + new String(getResourceAsBA("L.jspX")) + "</div></body>";
		strTmp = strTmp.replace("B8b8B8Bbbb888B", calcBase());
		// addressBAR
		String toURL = "l1lll1l1ll1l1lll1l1lll1l1ll1ll11lll111111l1l11ll1l1l1l1l1l11l1";
		String encodedURL = url2html(urlStr);
		strTmp = strTmp.replace(toURL, encodedURL);

		HTMLDocument htmlTmp = parser2.createDocument(strTmp);

		return htmlTmp;
	}

	public static byte[] getResourceAsBA(String namePar) throws IOException {
		InputStream in = UrlFetchTestTest.class.getClassLoader().getResourceAsStream(namePar);
		byte[] b = new byte[in.available()];
		in.read(b);
		return b;
	}

	private String url2html(String urlStr) throws URISyntaxException {
		URI uri = new URI(urlStr);
		return uri.toString();
	}

	public final String calcBase() {
		return SwapServletUrl.substring(0, SwapServletUrl.length() - 2);
	}

	private ServletOutputStream include(HttpServletResponse resp, String resourceName) {
		ServletOutputStream out = null;
		try {
			byte[] b = getResourceAsBA(resourceName);
			out = include(resp, b);
		} catch (IOException e) {
			log.trace("include:", e);
		}
		return out;

	}

	private ServletOutputStream include(HttpServletResponse resp, byte[] bytes) throws IOException {
		ServletOutputStream out = resp.getOutputStream();
		String newVal = new String(bytes);
		String L1111 = "l11010101010000101010100101lIll1l0O0l10ll1001l1l01ll001";
		newVal = newVal.replace(L1111, SwapServletUrl);
		String B8b8b = "B8b8B8Bbbb888B";
		newVal = newVal.replace(B8b8b, calcBase());
		bytes = newVal.getBytes();
		out.write(bytes);
		return out;
	}

	/**
	 * @author vipup
	 * @param documentTmp
	 * @param contextEncStr
	 * @return
	 */
	public String renderDocument(HTMLDocument documentTmp, String contextEncStr) {
		String textValue;
		if ("KOI8-R".equals(contextEncStr)) {
			textValue = documentTmp.getRoot().getTextValue();// getTextValue()
		} else {
			HTMLNode doctype = documentTmp.getDoctype();
			String sDoctype = (doctype == null ? "" : doctype.getTextValue());//
			// textValue = sDoctype + documentTmp.getRoot().getTextValue();
			textValue = documentTmp.getRoot().asXHTML();

		}
		return textValue;
	}

}
