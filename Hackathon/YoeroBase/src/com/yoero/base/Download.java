package com.yoero.base;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

import android.os.Environment;

// This class downloads a file from a URL.
public class Download extends Observable implements Runnable {

	// Max size of download buffer.
	private static final int MAX_BUFFER_SIZE = 10240;

	// These are the status names.
	public static final String STATUSES[] = { "Downloading", "Paused",
			"Complete", "Cancelled", "Error" };

	// These are the status codes.
	public static final int INIT = -1;
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	private URL url; // download URL
	private int size; // size of download in bytes
	private int downloaded; // number of bytes downloaded
	private int status; // current status of download

	private DataOutput file;

	public Download(DataOutput fos, URL url) {
		this(fos, url, true);
	}

	// Constructor for Download.
	public Download(DataOutput fos, URL url, boolean autostartDownload) {
		this.file = fos;
		this.url = url;
		size = -1;
		downloaded = 0;
		status = INIT;

		if (autostartDownload)
			download();
	}

	// Get this download's URL.
	public String getUrl() {
		return url.toString();
	}

	// Get this download's size.
	public int getSize() {
		return size;
	}

	// Get this download's progress.
	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	// Get this download's status.
	public int getStatus() {
		return status;
	}

	// Pause this download.
	public void pause() {
		status = PAUSED;
		stateChanged();
	}

	// Resume this download.
	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		download();
	}

	// Cancel this download.
	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}

	public Exception _error;

	// Mark this download as having an error.
	private void error(Exception e) {
		_error = e;
		status = ERROR;
		stateChanged();
	}

	// Start or resume downloading.
	public void download() {
		Thread thread = new Thread(this);
		thread.start();
	}

	// Get file name portion of URL.
	@SuppressWarnings("unused")
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return Environment.getExternalStorageDirectory() + fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	// Download file.
	public void run() {
		// RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

			// Connect to server.
			connection.connect();

			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2) {
				throw new Exception("Invalid response code: "
						+ connection.getResponseCode());
			}

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				throw new Exception("Invalid content length: " + contentLength);
			}

			/*
			 * Set the size for this download if it hasn't been already set.
			 */
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}

			// Open file and seek to the end of it.
			// file = new RandomAccessFile(getFileName(url), "rw");
			// file.seek(downloaded);

			status = DOWNLOADING;

			stream = connection.getInputStream();
			while (status == DOWNLOADING) {
				/*
				 * Size buffer according to how much of the file is left to
				 * download.
				 */
				byte buffer[];
				if (size - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[size - downloaded];
				}

				// Read from server into buffer.
				int read = stream.read(buffer);
				if (read == -1)
					break;

				// Write buffer to file.
				file.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
			}

			/*
			 * Change status to complete if this point was reached because
			 * downloading has finished.
			 */
			if (status == DOWNLOADING) {
				status = COMPLETE;
				stateChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(e);
		} finally {
			// Close file.
			if (file != null) {
				try {
					((Closeable) file).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Close connection to server.
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Notify observers that this download's status has changed.
	private void stateChanged() {
		setChanged();
		notifyObservers();
	}

	public int getDownloaded() {
		return downloaded;
	}

	public static String fetch (String url) {
	      URL u;
	      InputStream is = null;
	      BufferedReader dis;
	      String s;
         StringBuilder sb = new StringBuilder();

	      try {
	         u = new URL(url);

	         is = u.openStream();         // throws an IOException

	         dis = new BufferedReader(new InputStreamReader(is));
	         
	         while ((s = dis.readLine()) != null) {
	            sb.append(s);
	         }

	      } catch (MalformedURLException mue) {

	         System.out.println("Util/Download.java - Ouch - a MalformedURLException happened.");
	         //mue.printStackTrace();
	         //System.exit(1);

	      } catch (IOException ioe) {

	         System.out.println("Util/Download.java - Oops- an IOException happened.");
	         //ioe.printStackTrace();
	         //System.exit(1);

	      } catch (Exception ex) {

		     System.out.println("Util/Download.java - Oops- an Exception happened.");
		     //ex.printStackTrace();
		     //System.exit(1);

	      } finally {
	         try {
	        	 if (is != null) {
	        		 is.close();
	        	 }
	         } catch (IOException ioe) {
	            // just going to ignore this one
	         }

	      }

	      return sb.toString(); 
	   }  // end of main
}