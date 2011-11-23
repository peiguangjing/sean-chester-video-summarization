package image;

import java.io.*;

public class GoThroughImage{
	
	private FileInputStream imageStream;
	private final int width = 320;
	private final int height = 240;
	private final int singleImageSize = 3*this.width*this.height; //how many bytes per picture
	private int IMAGEBUFFERSIZE = 120; //cache how many images
	private int percentage = 50;
	private byte[] bytesBuffer;
	
	public GoThroughImage (FileInputStream imageStream, int percentage) {
		this.imageStream = imageStream;
		this.percentage = percentage;
		this.bytesBuffer = new byte[this.singleImageSize*this.IMAGEBUFFERSIZE];
	}
	
	public void filter () {
		try {
			OutputStream out = null;
			for (int numRead = 0; numRead != -1;) {
				int offset = 0;
				while (offset < this.bytesBuffer.length && (numRead=this.imageStream.read(bytesBuffer, offset, this.bytesBuffer.length-offset)) > 0) {
					offset += numRead;
				}
				
				//buffer filled, now select every other image, no weighting algorithm yet
				File outvideo = new File("outvideo.rgb");
				for (int i = 0; i < this.IMAGEBUFFERSIZE; i+=60) {     //<-----plug any filters here inside 
					//dump new image series to the output file
					out = new FileOutputStream(outvideo, true);	//append to this output file
					out.write(this.bytesBuffer, this.singleImageSize*i, this.singleImageSize);
				}
					
			}
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}