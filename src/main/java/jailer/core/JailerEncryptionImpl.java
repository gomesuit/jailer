package jailer.core;

import org.apache.commons.codec.binary.Base64;

public class JailerEncryptionImpl implements JailerEncryption{

	@Override
	public String encode(String str) {
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		return new String(encodedBytes);
	}

	@Override
	public String decoded(String str) {
		byte[] decodedBytes = Base64.decodeBase64(str);
		return new String(decodedBytes);
	}

}
