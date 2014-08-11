package org.system;

import gui.SinEditor;

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
	long mDownloaded = 0;
	String strLastModified = "";
	private static Logger logger = Logger.getLogger(URLDownloader.class);
	
	public void Download(String strurl, String filedest) throws IOException {
		// Setup connection.
        URL url = new URL(strurl);
        URLConnection connection = url.openConnection();
        
        File fdest = new File(filedest);
        long downloaded = fdest.length();
        if (downloaded > 0) {
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            connection.setRequestProperty("If-Range", strLastModified);
            connection.connect();
        }
        else {
            connection.connect();
            downloaded = 0;
            mFileLength = connection.getContentLength();
            logger.info("File length : "+mFileLength);
            strLastModified = connection.getHeaderField("Last-Modified");
        }
        Map<String, List<String>> map = connection.getHeaderFields();

        // Setup streams and buffers.
        int chunk = 131072;
        BufferedInputStream input = new BufferedInputStream(connection.getInputStream(), chunk);
        RandomAccessFile outFile = new RandomAccessFile(fdest, "rw");
        if (downloaded > 0)  
            outFile.seek(downloaded);
        
        byte data[] = new byte[chunk];
        LogProgress.initProgress(100);
        long i=0;
        // Download file.
        for (int count=0; (count=input.read(data, 0, chunk)) != -1; i++) { 
            outFile.write(data, 0, count);
            downloaded += count;
            
            LogProgress.updateProgressValue((int) (downloaded * 100 / mFileLength));
            if (downloaded >= mFileLength)
                break;
        }
        logger.info("Real parts count : "+i);
        // Close streams.
        outFile.close();
        input.close();
	}
}
