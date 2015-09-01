package org.system;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.google.common.io.BaseEncoding;

public class RC4InputStream extends InputStream {

	private InputStream		in = null;
	private CipherInputStream localCipherInputStream;

	public RC4InputStream(InputStream in) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		this.in = in;
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(BaseEncoding.base64().decode(OS.RC4Key), "RC4");
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
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