package ws.rrd.server;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.util.HyperLinkUtil;

import eu.blky.css.BaseURLRegexpReplacer;
import eu.blky.css.ReplacerException;

public class LServlet {
	private static final Logger log = LoggerFactory.getLogger(LServlet.class.getName());
	public static final boolean TRACE = true;
	private static HyperLinkUtil handler = new HyperLinkUtil();
	public static final String SwapServletUrl = "TOSwapServletUrlDO";

	public static String calcBase() {
		return SwapServletUrl;
	}

	public static void testCreateFullLink(HTMLNode node, String swapServletUrl2, URL home) {
		handler.createFullNormalLink(node, swapServletUrl2, home);
		List<String> list = handler.scanSiteLink(node);
		if (TRACE)
			for (String ele : list)
				System_out_println(ele);
	}

	public static void testCreateNoScriptLink(HTMLNode node, String swapServletUrl2, URL home) {
		handler.createNoScriptLink(node, swapServletUrl2, home);
		if (TRACE) {
			// List<String> list = handler.scanScriptLink(node );
			// for(String ele : list)
			// System_out_println(ele);
		}
	}

	public static void testCreateScriptLink(HTMLNode node, String swapServletUrl2, URL home) {
		handler.createScriptLink(node, swapServletUrl2, home);
		if (TRACE) {
			// List<String> list = handler.scanScriptLink(node );
			// for(String ele : list)
			// System_out_println(ele);
		}
	}

	public static void testCreateStyleLink(HTMLNode node, String swapServletUrl2, URL home) {
		handler.createStyleLink(node, swapServletUrl2, home);
		if (TRACE) {
			// List<String> list = handler.scanScriptLink(node );
			// for(String ele : list)
			// System_out_println(ele);
		}
	}

	public static void testCreateMetaLink(HTMLNode node, String swapServletUrl2, URL home) {
		handler.createMetaLink(node, swapServletUrl2, home);
		if (TRACE) {
			List<String> list = handler.scanSiteLink(node);
			for (String ele : list)
				if (TRACE)
					System_out_println(ele);
		}
	}

	public static void System_out_println(String txt) {
		log.trace(txt);
	}
	
	public static String justifyCSS(String baseUrlPar, String cssInPar){
		// 1st pass:  url("xxx.css") >> url ( 'http://base.host.srv/css/xxx.css" )
		BaseURLRegexpReplacer repl = new BaseURLRegexpReplacer("  url  ( \'%1$2s\' )", baseUrlPar ); 
		String cssTmp = cssInPar; 
		String retval = cssTmp ;
	    try {
			retval = repl.replaceAll(BaseURLRegexpReplacer.URL_PATTERN,  retval );
		} catch (ReplacerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // 2nd pass: url ( 'http://base.host.srv/css/xxx.css" ) >>> URL ('https://swap-host-server.name/F/h_t_t_p_s_://base.host.srv/css/xxx.css')
	    String u_Tmp_u = SwapServletUrl.replace("/l/",undescoredProtocol(baseUrlPar));
		String strippedTmp  = stripFileName(  stripProtocol(baseUrlPar));
		BaseURLRegexpReplacer repl2 = new BaseURLRegexpReplacer("url(\'" +u_Tmp_u+ "%1$2s\')", strippedTmp  ); 
	    try {
			retval = repl2.replaceAll(BaseURLRegexpReplacer.URL_PATTERN,  retval );
		} catch (ReplacerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	    retval = retval.replace("F/h_t_t_p_s_://https://", "F/h_t_t_p_s_://");
	    retval = retval.replace("F/h_t_t_p_://http://", "F/h_t_t_p_://");
	    return retval;
	}
	private static String undescoredProtocol(String urlStr) {
		return urlStr.startsWith("https://")? "/F/h_t_t_p_s_://":"/F/h_t_t_p_://";
	}
	private static String stripProtocol(String urlStr) {
		return urlStr.startsWith("https://")? urlStr.substring("https://".length()):urlStr.substring("http://".length());
	}
	private static String stripFileName(String urlStr) {
		return urlStr.endsWith("/")? urlStr :urlStr.substring(0, urlStr.lastIndexOf("/"))+"/";
	}
}
