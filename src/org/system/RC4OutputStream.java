package org.system;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.google.common.io.BaseEncoding;

public class RC4OutputStream extends OutputStream {

	private CipherOutputStream localCipherOutputStream;
	
	public RC4OutputStream(OutputStream out) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(BaseEncoding.base64().decode(OS.RC4Key), "RC4");
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
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