package eu.blky.net.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class MyServletOutputStream extends ServletOutputStream {

	private OutputStream wrapped2;
	public MyServletOutputStream(OutputStream wrapped2) {
		this.wrapped2 = wrapped2;
	}
	@Override
	public void write(int b) throws IOException {
		wrapped2.write(b);
	}
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
			if (1 == 1) 
				throw new RuntimeException(
							"autogenerated from i1 return not checked value since Nov 22, 2018, 4:36:40 PM ;)!");
			else
				/*return*/ return false; 
	}
	@Override
	public void setWriteListener(WriteListener arg0) {
		// TODO Auto-generated method stub
			if (1 == 1) 
				throw new RuntimeException(
							"autogenerated from i1 return not checked value since Nov 22, 2018, 4:36:40 PM ;)!");
  
	}
 

}
