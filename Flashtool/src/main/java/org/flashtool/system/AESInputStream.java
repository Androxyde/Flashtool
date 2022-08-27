package org.flashtool.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AESInputStream extends InputStream {

	private InputStream		in = null;
	private CipherInputStream localCipherInputStream;

	public AESInputStream(InputStream in) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
		this.in = in;
        SecretKeySpec secretKeySpec = new SecretKeySpec(Hashing.sha256().hashBytes(OS.AESKey.getBytes("UTF-8")).asBytes(), "AES");
    	IvParameterSpec ivParameterSpec = new IvParameterSpec(BaseEncoding.base16().decode(OS.AESIV));
    	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    	cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        localCipherInputStream = new CipherInputStream(in, cipher);
	}

	@Override
	public int read() throws IOException {
		return localCipherInputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return localCipherInputStream.read(b);
	}

	@Override
	public boolean markSupported() {
		return localCipherInputStream.markSupported();
	}

}