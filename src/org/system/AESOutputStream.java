package org.system;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

public class AESOutputStream extends OutputStream {

	private CipherOutputStream localCipherOutputStream;
	
	public AESOutputStream(OutputStream out) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(Hashing.sha256().hashBytes(OS.AESKey.getBytes("UTF-8")).asBytes(), "AES");
    	IvParameterSpec ivParameterSpec = new IvParameterSpec(BaseEncoding.base16().decode(OS.AESIV));
    	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    	cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    	localCipherOutputStream = new CipherOutputStream(out, cipher);
	}

	@Override
	public void write(int b) throws IOException {
		localCipherOutputStream.write(b);
	}

	@Override
	public void flush() throws IOException {
		localCipherOutputStream.flush();
	}

	@Override
	public void close() throws IOException {
		localCipherOutputStream.close();
	}
 
}