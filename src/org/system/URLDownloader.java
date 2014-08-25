package org.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.logger.LogProgress;

public class URLDownloader {

	long mFileLength = 0;
	long mDownloaded = -1;
	String strLastModified = "";
	BufferedInputStream input = null;
	boolean canceled = false;
	private static Logger logger = Logger.getLogger(URLDownloader.class);
	
	public long Download(String strurl, String filedest, long seek) throws IOException {
		// Setup connection.
        URL url = new URL(strurl);
        URLConnection connection = url.openConnection();
        
        File fdest = new File(filedest);
        connection.connect();
        mDownloaded = 0;
        mFileLength = connection.getContentLength();
        strLastModified = connection.getHeaderField("Last-Modified");
        Map<String, List<String>> map = connection.getHeaderFields();

        // Setup streams and buffers.
        int chunk = 131072;
        input = new BufferedInputStream(connection.getInputStream(), chunk);
        RandomAccessFile outFile = new RandomAccessFile(fdest, "rw");
        outFile.seek(seek);
        
        byte data[] = new byte[chunk];
        LogProgress.initProgress(100);
        long i=0;
        // Download file.
        for (int count=0; (count=input.read(data, 0, chunk)) != -1; i++) { 
            outFile.write(data, 0, count);
            mDownloaded += count;
            
            LogProgress.updateProgressValue((int) (mDownloaded * 100 / mFileLength));
            if (mDownloaded >= mFileLength)
                break;
            if (canceled) break;
        }
        // Close streams.
        outFile.close();
        input.close();
        if (canceled) throw new IOException("Job canceled");
        return mDownloaded;
	}
	
	public void Cancel() {
		canceled = true;
	}
}