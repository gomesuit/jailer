package jailer.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

public class JailerEncryptionImpl implements JailerEncryption{
	private static final Charset charset = StandardCharsets.UTF_8;

	@Override
	public byte[] encode(String str) {
		if(str == null) return null;
		
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes(charset));
		return encodedBytes;
	}

	@Override
	public String decoded(byte[] src) {
		if(src == null) return null;
		
		byte[] decodedBytes = Base64.decodeBase64(src);
		return new String(decodedBytes, charset);
	}

}
