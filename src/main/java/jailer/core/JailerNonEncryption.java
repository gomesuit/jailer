package jailer.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JailerNonEncryption implements JailerEncryption{
	private static final Charset charset = StandardCharsets.UTF_8;

	@Override
	public byte[] encode(String str) {
		if(str == null) return null;
		
		byte[] encodedBytes = str.getBytes(charset);
		return encodedBytes;
	}

	@Override
	public String decoded(byte[] src) {
		if(src == null) return null;
		
		return new String(src, charset);
	}

}
