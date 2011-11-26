import java.io.*;
import java.lang.*;

import image.GoThroughImage;
import sound.playWave.PlaySound;
import sound.playWave.PlayWaveException;

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
				System.err.println("percentage parameter must not <=0 and >100");
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
			
			GoThroughImage gti = new GoThroughImage(videoinputStream, percentage);
			gti.filter();
			//videoinputStream.close(); not so important for this program
			
			
			/*
			// opens the inputStream
			FileInputStream audioinputStream;
			try {
			    audioinputStream = new FileInputStream(audiofilename);
			} catch (FileNotFoundException e) {
			    e.printStackTrace();
			    return;
			}

			// initializes the playSound Object
			PlaySound playSound = new PlaySound(audioinputStream);

			// plays the sound
			try {
			    playSound.play();
			} catch (PlayWaveException e) {
			    e.printStackTrace();
			    return;
			}
			*/
	 }
}