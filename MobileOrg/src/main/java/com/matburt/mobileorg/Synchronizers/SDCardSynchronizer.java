package com.matburt.mobileorg.Synchronizers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class SDCardSynchronizer implements SynchronizerInterface {	

	private String remoteIndexPath;
	private String remotePath;

    public SDCardSynchronizer(Context context) {
		this.remoteIndexPath = PreferenceManager.getDefaultSharedPreferences(
				context).getString("indexFilePath", "");
	
		this.remotePath = new File(remoteIndexPath) + "/";
	}
    

    public boolean isConfigured() {
        if (remoteIndexPath.equals(""))
            return false;
        return true;
    }

	@Override
	public void putRemoteFile(String filename, String contents) throws IOException {
		String outfilePath = this.remotePath + filename;
		
		File file = new File(outfilePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(contents);
		writer.close();
	}

	@Override
	public void putRemoteFile(String filename, InputStream contents) throws IOException {
		String outfilePath = this.remotePath + filename;

		final int bufSize = 8192;
		int bytesRead = 0;
		byte[] buffer = new byte[bufSize];

		File file = new File(outfilePath);
		FileOutputStream fos = new FileOutputStream(file, false);

		while ( (bytesRead = contents.read(buffer, 0, bufSize)) >= 0 ) {
			fos.write(buffer, 0, bytesRead);
		}
		fos.close();
	}

	@Override
	public BufferedReader getRemoteFile(String filename) throws FileNotFoundException {
		return new BufferedReader(
				new InputStreamReader(
						getRemoteFileStream(filename)));
	}

	@Override
	public InputStream getRemoteFileStream(String filename) throws FileNotFoundException {
		String filePath = this.remotePath + filename;
		File file = new File(filePath);

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(this.getClass().getSimpleName(), "Error Create new file", e);
			}
		}

		FileInputStream fileIS = new FileInputStream(file);

		return fileIS;
	}

	@Override
	public void postSynchronize() {		
	}


	@Override
	public boolean isConnectable() {
		return true;
	}
}