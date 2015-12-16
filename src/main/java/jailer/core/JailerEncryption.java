package jailer.core;

public interface JailerEncryption {
	public byte[] encode(String src);
	public String decoded(byte[] src);
}
