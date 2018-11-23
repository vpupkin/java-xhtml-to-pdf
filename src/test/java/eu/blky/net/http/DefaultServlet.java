package eu.blky.net.http;

  

import org.eclipse.jetty.util.resource.Resource;

public class DefaultServlet extends  org.eclipse.jetty.servlet.DefaultServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 	@Override
	public Resource getResource(String pathInContext) {
 		Resource retval = new CacheResource(pathInContext) ;
 		//return super.getResource(pathInContext);
 		return retval;
	}

}
