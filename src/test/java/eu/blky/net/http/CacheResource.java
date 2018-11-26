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
 
	
	private String pathInContext;
	private long lastModified = System.currentTimeMillis()/1000;
	private long length;
	private boolean exists = true;

	public CacheResource(String pathInContext) {
		this.pathInContext = pathInContext;
		Cache cachTmp = search4cache();
		Object o = cachTmp.get(this.pathInContext);
		if (o instanceof ws.rrd.server.LCacheEntry) {
			LCacheEntry ceTmp = (ws.rrd.server.LCacheEntry)o; 
			length = ceTmp.getBytes().length;
			this.is =  new ByteArrayInputStream( ceTmp.getBytes() );			
		}else if (o instanceof cc.co.llabor.cache.js.Item) {
			Item oTmp = (cc.co.llabor.cache.js.Item)o;
			length = oTmp.getValue().length();
			this.is = new ByteArrayInputStream(oTmp.toString().getBytes());
		}else {
			length = o.toString().getBytes().length ; //it.toString().getBytes()
			Object it = search4cache().get(this.pathInContext);
			this.is = new ByteArrayInputStream(it.toString().getBytes());
		}
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

	private Cache search4cache() {
		Cache cachTmp = null ;
		cachTmp = null !=  Manager.getCache("getCache@" + UrlFetchTestTest.class.getName()).get(pathInContext) ?  Manager.getCache("getCache@" + UrlFetchTestTest.class.getName()): null;
		cachTmp = null !=  Manager.getCache(CSStore.CSSSTORE).get(pathInContext)  ? Manager.getCache(CSStore.CSSSTORE):cachTmp;
		cachTmp = null !=  Manager.getCache(JSStore.SCRIPTSTORE).get(pathInContext) ?Manager.getCache(JSStore.SCRIPTSTORE):cachTmp;
		return cachTmp;
	}

	@Override
	public URL getURL() {
		 return null; 
	}
 
	private InputStream is;
	@Override
	public File getFile() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return this.pathInContext; 
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
