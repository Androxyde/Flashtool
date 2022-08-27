package org.flashtool.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessBuilderWrapper {

    private StringWriter infos;
    private StringWriter errors;
    private int status;
    private boolean print = false;

    public ProcessBuilderWrapper(File directory, List<String> command) throws Exception {
    	run(directory, command);
    }

    public void run(File directory, List<String> command) throws Exception {
    	infos = new StringWriter();
        errors = new StringWriter();
        ProcessBuilder pb = new ProcessBuilder(command);     
        if(directory != null)
            pb.directory(directory);
        Process process = pb.start();
        StreamBoozer seInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(infos, true));
        StreamBoozer seError = new StreamBoozer(process.getErrorStream(), new PrintWriter(errors, true));
        seInfo.start();
        seError.start();
        status = process.waitFor();
        seInfo.join();
        seError.join();
    }
    
    public ProcessBuilderWrapper(List<String> command) throws Exception {
    	run(null, command);
    }

    public ProcessBuilderWrapper(String[] command, boolean print) throws Exception {
    	this.print = print;
    	List<String> cmd = new ArrayList<String>();
		for (int i=0;i<command.length;i++)
			cmd.add(command[i]);
		run(null, cmd);
    }

    public String getStdErr() {
        return errors.toString();
    }


    public String getStdOut() {
        return infos.toString();
    }

    public RunOutputs getOutputs() {
		return new RunOutputs(infos.toString(), errors.toString());
	}

    public int getStatus() {
        return status;
    }

    class StreamBoozer extends Thread {

        private InputStream in;
        private PrintWriter pw;

        StreamBoozer(InputStream in, PrintWriter pw) {
            this.in = in;
            this.pw = pw;
        }

        @Override
        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ( (line = br.readLine()) != null) {
                	if (line.trim().replaceAll("\n", "").length()>0) {
                		line = line.replaceAll("\n", "");
	                	if (print)
	                		log.info(line);
	                	else
	                		log.debug(line);
	                    pw.println(line);
                	}
                }
            }
            catch (Exception e) {}
            finally {
                try {
                    br.close();
                } catch (IOException e) {}
            }
        }

    }

    public void kill() {
    	
    }
}