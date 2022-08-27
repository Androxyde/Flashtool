package org.flashtool.flashsystem;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.flashtool.parsers.sin.SinFileException;
import org.flashtool.parsers.ta.TAUnit;

public interface Flasher {

	public boolean flashmode();
	
	public boolean open(boolean simulate);

	public boolean open();
	
	public void flash() throws X10FlashException, IOException;
	
	public void close();
	
	public String getPhoneProperty(String property);
	
	public Bundle getBundle();
	
	public TAUnit readTA(int partition, int unit) throws X10FlashException, IOException;
	
	public void writeTA(int partition, TAUnit unit) throws X10FlashException, IOException;
	
	public void sendLoader() throws FileNotFoundException, IOException, X10FlashException, SinFileException ;
	
	public void backupTA();
	
	public String getCurrentDevice();
	
	public String getSerial();
	
	public String getIMEI();
	
	public String getRootingStatus();
	
	public void setFlashState(boolean ongoing) throws IOException,X10FlashException ;

}