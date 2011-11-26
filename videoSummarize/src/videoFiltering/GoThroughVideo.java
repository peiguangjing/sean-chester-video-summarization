package videoFiltering;

import java.io.*;
import java.util.*; 

import shot.Shot;
import utility.ShotComparator;

import AnalyzedFrame.AnalyzedFrame;

import sound.WaveUtility.WaveUtility;

public class GoThroughVideo{
	
	private RandomAccessFile imageStream;
	private FileInputStream audioStream;
	private final int width = 320;
	private final int height = 240;
	private final int singleImageSize = 3*this.width*this.height; //how many bytes per picture
	private int bufferSeconds = 5; 
	private int framesPerSecond = 24;
	private int IMAGEBUFFERSIZE = this.bufferSeconds * this.framesPerSecond; //cache how many images
	private int percentage = 20;
	private Vector<AnalyzedFrame> AnalyzedFrames = new Vector<AnalyzedFrame>();
	private Vector<Shot> Shots = new Vector<Shot>();
	private Vector<Shot> FinalSummary = new Vector<Shot>();
	private PriorityQueue<Shot> ShotPriorityQueue = new PriorityQueue<Shot>(20, new ShotComparator());
	private int LastShotStartIndex = 0;
	private short[][] bytesBuffer;
	private float sourceLength = 0.0f;
	
	public GoThroughVideo (RandomAccessFile imageStream, FileInputStream audioStream, int percentage) {
		this.imageStream = imageStream;
		this.percentage = percentage;
		this.audioStream = audioStream;
		this.bytesBuffer = new short[this.IMAGEBUFFERSIZE][this.singleImageSize];
	}
	
	public void filter () {
		
		AnalyzedFrame currentFrame;
		try {
			FileOutputStream out = null;
			byte[] frameBuffer = new byte[this.singleImageSize];
			int frameIndex = 0;
			int innerFrameIndex = 0;
			float currentSummaryLength = 0.0f;
			
			for (int numRead = 0; numRead != -1;) {
				int offset = 0;
				int bufferIndex = 0;
				while (offset < (this.singleImageSize*this.IMAGEBUFFERSIZE) && (numRead=this.imageStream.read(frameBuffer, offset,frameBuffer.length-offset)) > 0) {
					offset += numRead;
					innerFrameIndex = 0;
					for(byte b : frameBuffer)
					{
						bytesBuffer[bufferIndex][innerFrameIndex++] =(short) ( (b < 0) ? 256 + b : b );
					}
					
					bufferIndex++;
				}
				
				if( bufferIndex > 0)
				{
					//Process this chunk of video
					for(int image=0; image < IMAGEBUFFERSIZE; image++, frameIndex++)
					{
						currentFrame = new AnalyzedFrame();
						currentFrame.AnalyzeFrame(bytesBuffer[image]);
						if( (frameIndex+image)>0 && currentFrame.ShotBoundaryDetection(AnalyzedFrames.lastElement())) //Difference between old and new frame is such that we have a new shot
						{
							if(  frameIndex + image - LastShotStartIndex > 5)
							{
								Shots.add(new Shot(LastShotStartIndex, frameIndex + image));
							}
							LastShotStartIndex = frameIndex + image + 1;
						}
						AnalyzedFrames.add(currentFrame);
					}
				}
			}
			
			sourceLength = frameIndex * (1.0f/24.0f);
			
			//Cull shots with less than five frames (assuming these are false positives/parts of gradual transitions)
			Iterator<Shot> iter = Shots.iterator();
			while(iter.hasNext())
			{
				if(iter.next().Cull())
				{
					iter.remove();
				}
			}
			
			//At this point we have our vector of analyzed frames as well as shots.  Now we need to assign importance.
			//Iterate through shots to calculate importance
			for(Shot currentShot : Shots)
			{
				currentShot.CalculateShotImportance(AnalyzedFrames);
				ShotPriorityQueue.add(currentShot);
			}
			
			//Now we have a collection of shots ordered by their importance
			Shot currentShot;
			do
			{
				//Head of queue is most important remaining shot
				currentShot = ShotPriorityQueue.remove();
				//Add to the current summary length
				currentSummaryLength += currentShot.ShotTime();
				//Add shot to our final summary
				FinalSummary.add(currentShot);
			}while( sourceLength < currentSummaryLength );
			

			//buffer filled, now select every other image, no weighting algorithm yet
			File outvideo = new File("outvideo.rgb");
			out = new FileOutputStream(outvideo, true);	//append to this output file
			for (int shotIndex = 0; shotIndex < FinalSummary.size(); shotIndex++) 
			{
				if(shotIndex != FinalSummary.size() - 1)
				{
					FinalSummary.elementAt(shotIndex).OutputShot(imageStream, out);
				}
				else // last element
				{
					FinalSummary.elementAt(shotIndex).OutputShot(imageStream, out, currentSummaryLength - sourceLength);
				}
				//out.write(this.bytesBuffer, this.singleImageSize*i, this.singleImageSize);
			}
			out.close();
			
			WaveUtility wu = new WaveUtility(audioStream, this.bufferSeconds);
			double soundLevel;
			int end = 0;
			int counter = 0;
			do {
				counter++;
				end = wu.readInBuffer();
				soundLevel = wu.computeSoundLevel();
				System.out.println(counter + " soundLevel of the period is: " + soundLevel);
			} while ( end == 0 );
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}