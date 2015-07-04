package org.simpleusblogger;

import com.igormaznitsa.jbbp.mapper.Bin;

public class USBHeaderExtended {
	@Bin byte brmRequestType;
	@Bin byte bRequest;
	@Bin short wValue;
	@Bin short wIndex;
	@Bin short wLength;
}
