package org.flashtool.system;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunOutputs {

	String stdout;
	String stderr;
	
	public RunOutputs(String out, String err) {
		stdout = out;
		stderr = err;
	}
	
	public String getStdOut() {
		return stdout;
	}
	
	public String getStdErr() {
		return stderr;
	}
}
