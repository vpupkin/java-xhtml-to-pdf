package eu.blky.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream; 
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.resource.Resource;

import cc.co.llabor.cache.Manager;
import cc.co.llabor.cache.css.CSStore;
import cc.co.llabor.cache.js.Item;
import cc.co.llabor.cache.js.JSStore; 
import net.sf.jsr107cache.Cache;
import ws.rrd.server.LCacheEntry;

public class CacheResource extends Resource { 
 
	private InputStream is;
	private String name = System.currentTimeMillis()+".xhtml";	
	private String pathInContext;
	private long lastModified = System.currentTimeMillis()/1000;
	private long length;
	private boolean exists = true;

	public CacheResource(String pathPar) {
		this.pathInContext =pathPar; //  UrlFetchTestTest.FETCH_URL+"/"+
		name+= pathInContext ;
		Object o =  UrlFetchTestTest.getCached(pathInContext);
		if (o instanceof cc.co.llabor.cache.css.Item){
			cc.co.llabor.cache.css.Item it = (cc.co.llabor.cache.css.Item) o;
			length = it.getValue().length();
			this.is = new ByteArrayInputStream(it.toString().getBytes());
		}else if (o instanceof ws.rrd.server.LCacheEntry) {
			LCacheEntry ceTmp = (ws.rrd.server.LCacheEntry)o; 
			length = ceTmp.getBytes().length;
			this.is =  new ByteArrayInputStream( ceTmp.getBytes() );			
		}else if (o instanceof cc.co.llabor.cache.js.Item) {
			Item oTmp = (cc.co.llabor.cache.js.Item)o;
			length = oTmp.getValue().length();
			this.is = new ByteArrayInputStream(oTmp.toString().getBytes());
		}else {
			length = o.toString().getBytes().length ; 
			this.is = new ByteArrayInputStream(o.toString().getBytes());
		}
	}

	public CacheResource(String url, byte[] bytesTmp, String contentType) {
		this.pathInContext = url;
		UrlFetchTestTest.cacheIt( url, bytesTmp, contentType) ;
	}

	@Override
	public boolean isContainedIn(Resource r) throws MalformedURLException {
		return false; 
	}

	@Override
	public void close() {
		// 
	}

	@Override
	public boolean exists() { 
		 
		return  exists  ; 
	}

	@Override
	public boolean isDirectory() {
		return false; 
	}

	@Override
	public long lastModified() {
		return lastModified ; 
	}

	@Override
	public long length() {
		return length;
	}

 
	@Override
	public URL getURL() {
		 return null; 
	}
 

	@Override
	public File getFile() throws IOException {
		return null;
	}

	@Override
	public String getName() {
	 
		return name; 
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return is; 
	}

	@Override
	public ReadableByteChannel getReadableByteChannel() throws IOException { 
		return null; 
	}

	@Override
	public boolean delete() throws SecurityException {
		return false; 
	}

	@Override
	public boolean renameTo(Resource dest) throws SecurityException {
		return false; 
	}

	@Override
	public String[] list() {
		return null; 
	}

	@Override
	public Resource addPath(String path) throws IOException, MalformedURLException {
		return null; 
	}

}
