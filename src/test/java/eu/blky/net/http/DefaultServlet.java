package eu.blky.net.http;

  

import java.util.Base64;

import org.eclipse.jetty.util.resource.Resource;

public class DefaultServlet extends  org.eclipse.jetty.servlet.DefaultServlet{
	UrlFetchTestTest fetcher;
	public DefaultServlet(UrlFetchTestTest fetcher) {
		this.fetcher =  fetcher;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 	@Override
	public Resource getResource(String pathInContext) {
 		Resource retval = null;
 		if (pathInContext.startsWith("/l/")) {
 			String url = new String( Base64.getDecoder().decode( pathInContext.substring(3) ) )  ;
 			try {
				FetchObj dataTmp = fetcher.smartFetch(url);
				// store to cache 
				UrlFetchTestTest.cacheIt( url, dataTmp.getBytes(), dataTmp.getContentType()) ; 
				UrlFetchTestTest.cacheIt( pathInContext, dataTmp.getBytes(), dataTmp.getContentType()) ; 
				retval = new CacheResource(url) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 			
 		}else {
 			retval = new CacheResource(pathInContext) ;
 			
 		}
 		
 		return retval;
	}

}
