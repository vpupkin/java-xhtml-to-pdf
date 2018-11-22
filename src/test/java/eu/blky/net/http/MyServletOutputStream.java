package eu.blky.net.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class MyServletOutputStream extends ServletOutputStream {

	private OutputStream wrapped2;
	public MyServletOutputStream(OutputStream wrapped2) {
		this.wrapped2 = wrapped2;
	}
	@Override
	public void write(int b) throws IOException {
		wrapped2.write(b);
	}
 

}
