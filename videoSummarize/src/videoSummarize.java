import java.io.*;
import java.lang.*;

import videoFiltering.GoThroughVideo;



/**
 * trigger analyzing 
 * 
 * 
 */

public class videoSummarize{
	
	 public static void main(String[] args) {
			// get the command line parameters
			if (args.length < 3) {
			    System.err.println("usage: java videoSummarize [videoInput.rgb] [audioInput.wav] [percentage]");
			    return;
			}
			String videofilename = args[0];
			String audiofilename = args[1];
			int percentage = Integer.parseInt(args[2]);
			
			if (percentage <= 0 || percentage >100){
				System.err.println("percentage parameter must not <=0 or >100");
				return;
			}

			RandomAccessFile videoinputStream;
			File videofile;
			try {
				videofile = new File(videofilename);
				videoinputStream = new RandomAccessFile(videofile, "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			    return;
			}
			
			FileInputStream audioinputStream;
			File audioFile;
			RandomAccessFile audioRAF;
			try {
				audioFile = new File(audiofilename);
				audioRAF = new RandomAccessFile(audioFile, "r");
				audioinputStream = new FileInputStream(audiofilename);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			    return;
			}
			
			
			GoThroughVideo gtv = new GoThroughVideo(videoinputStream, audioinputStream, audioRAF, percentage);
			gtv.filter();
			//videoinputStream.close(); not so important for this program
			
			
	 }
}