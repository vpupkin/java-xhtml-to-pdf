package eu.blky.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class myRBC implements ReadableByteChannel {

	private CacheResource cacheItem;
	private InputStream in;
	private boolean isOpen = true;
	private long contentLength;

	public myRBC(CacheResource cacheResource) throws IOException {
		this.cacheItem = cacheResource;
		
		this.in = cacheItem.getInputStream();
		this.contentLength=this.in.available();
	}

	@Override
	public void close() throws IOException {
		isOpen  = false; 
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	long cacheIndex =0;

	@Override
	public int read(ByteBuffer dst) throws IOException {
		cacheIndex++;
//		if (contentLength<cacheIndex) {
//			return   in.read(); // the same...
//		}
		return in.read();
	}

}
