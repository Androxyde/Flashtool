package org.flashtool.parsers.simpleusblogger;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class USBHeaderExtended {
	@Bin byte brmRequestType;
	@Bin byte bRequest;
	@Bin short wValue;
	@Bin short wIndex;
	@Bin short wLength;
}
