package image;

import java.io.*;
import java.util.*; 

import AnalyzedFrame.AnalyzedFrame;

public class GoThroughImage{
	
	private FileInputStream imageStream;
	private final int width = 320;
	private final int height = 240;
	private final int singleImageSize = 3*this.width*this.height; //how many bytes per picture
	private int IMAGEBUFFERSIZE = 120; //cache how many images
	private int percentage = 50;
	private Vector AnalyzedFrames = new Vector();
	private byte[][] bytesBuffer;
	
	public GoThroughImage (FileInputStream imageStream, int percentage) {
		this.imageStream = imageStream;
		this.percentage = percentage;
		this.bytesBuffer = new byte[this.IMAGEBUFFERSIZE][this.singleImageSize];
	}
	
	public void filter () {
		AnalyzedFrame currentFrame;
		try {
			OutputStream out = null;
			for (int numRead = 0, frameIndex = 0; numRead != -1; frameIndex += this.IMAGEBUFFERSIZE) {
				int offset = 0;
				int bufferIndex = 0;
				while (offset < (this.singleImageSize*this.IMAGEBUFFERSIZE) && (numRead=this.imageStream.read(bytesBuffer[bufferIndex], offset, this.bytesBuffer.length-offset)) > 0) {
					offset += numRead;
					bufferIndex++;
				}
				
				//Process this chunk of video
				for(int image=0; image < IMAGEBUFFERSIZE; image++)
				{
					currentFrame = new AnalyzedFrame();
					currentFrame.AnalyzeFrame(bytesBuffer[image]);
					AnalyzedFrames.add(currentFrame);
				}
			}
			//buffer filled, now select every other image, no weighting algorithm yet
			File outvideo = new File("outvideo.rgb");
			for (int i = 0; i < this.IMAGEBUFFERSIZE; i+=60) {     //<-----plug any filters here inside 
				//dump new image series to the output file
				out = new FileOutputStream(outvideo, true);	//append to this output file
				//out.write(this.bytesBuffer, this.singleImageSize*i, this.singleImageSize);
			}
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}