package jailer.core;

import org.apache.commons.codec.binary.Base64;

public class JailerEncryptionImpl implements JailerEncryption{

	@Override
	public byte[] encode(String str) {
		if(str == null) return null;
		
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		return encodedBytes;
	}

	@Override
	public String decoded(byte[] src) {
		if(src == null) return null;
		
		byte[] decodedBytes = Base64.decodeBase64(src);
		return new String(decodedBytes);
	}

}
