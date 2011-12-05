package shot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.Vector;

import utility.Index;

import AnalyzedFrame.AnalyzedFrame;

import sound.WaveUtility.WaveUtility;

public class Shot {
	private final int MinShotLength = 2*24;
	//Larger values correspond to less weight in the final overall score.

	//Motion weight is a weight on the total motion vector distances for a frame.  The idea here is that
	//frames with more motion are more important and shots with more motion are more important.
	
	//Based on cursory statistics this weight should produce values in the range [1,25]
	private final int MotionWeight = 40000;
	//Color weight is a weight on the sum of the color components of the average color for a frame.  The idea
	//here is that shots that are more saturated/brighter (thus having higher RGB values) are more important.
	
	//Based on cursory statistics this weight should produce values in the range [0.1,3]
	private final int AverageColorWeight = 10000;
	
	//Based on cursory statistics this weight should produce values in the range [0.6,10]
	private final int ColorDifferenceWeight = 10000000; 
	
	//Based on cursory statistics this weight should produce values in the range [0.01,20]
	private final int AudioWeight = 1000;
	
	private int ShotIndex = 0;
	private float ShotImportance = 0.0f;
	
	private long FrameLowerIndex = 0;
	private long FrameUpperIndex = 0;
	
	private final static int framesPerSecond = 24;
	
	public Shot()
	{
		
	}
	
	public Shot( int lowerBound, int upperBound )
	{
		FrameLowerIndex = lowerBound;
		FrameUpperIndex = upperBound;
	}
	
	public Boolean Cull()
	{
		return (FrameUpperIndex-FrameLowerIndex) <= MinShotLength;
	}
	
	public long StartFrame()
	{
		return FrameLowerIndex;
	
	}
	
	public float GetShotImportance()
	{
		return ShotImportance;
	}
	
	public float ShotTime()
	{
		return (FrameUpperIndex - FrameLowerIndex + 1) * (1.0f/24.0f);
	}
	
	public void SetShotBounds( int lowerBound, int upperBound)
	{
		FrameLowerIndex = lowerBound;
		FrameUpperIndex = upperBound;
	}
	
	public void CalculateShotImportance(Vector<AnalyzedFrame> frames, WaveUtility wur, Writer output)
	{
		//Apply weights given the information stored in each AnalyzedFrame in the sequence from FrameLowerIndex to FrameUpperIndex
		double motionContribution = 0.0f;
		double colorContribution = 0.0f;
		double colorDifferenceContribution = 0.0f;
		double avgSoundLevel = 0;
		
		for(int index = (int)FrameLowerIndex; index <= FrameUpperIndex; index++)
		{
			motionContribution += frames.elementAt(index).TotalMotion();
			colorContribution += frames.elementAt(index).TotalColor();
			colorDifferenceContribution += frames.elementAt(index).TotalColorDifference();
		}
		avgSoundLevel = wur.computeSoundLevelPeriod((double)FrameLowerIndex/framesPerSecond, (double)FrameUpperIndex/framesPerSecond);
		
		/*
		try {
			output.write("motion: "+Double.toString(motionContribution)+", avgColor: "+Double.toString(colorContribution)+", colorDifferenceContribution: "+Double.toString(colorDifferenceContribution)+", avgSoundLevel: "+Double.toString(avgSoundLevel)+"\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		motionContribution /= MotionWeight;
		colorContribution /= AverageColorWeight;
		colorDifferenceContribution /= ColorDifferenceWeight;
		avgSoundLevel /= AudioWeight;
		
		//how much should the sound weigh? the number ranges from 100 to 5600 for the terminator one
		
		ShotImportance =(float) (motionContribution + colorContribution + colorDifferenceContribution + avgSoundLevel);
		
		
	}
	
	public void OutputShot(RandomAccessFile source, FileOutputStream output)
	{
		int framesToPull =(int)( FrameUpperIndex - FrameLowerIndex + 1 );
		long seekOffset = 0;
		byte[] buffer = new byte[320*240*3];
		try {
			seekOffset = Index.FrameIndexToBytes(FrameLowerIndex);
			source.seek(seekOffset);
			for(int i = 0; i < framesToPull; i++)
			{
				source.read(buffer);
				output.write(buffer);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	
	public void OutputShot(RandomAccessFile source, FileOutputStream output, float partialTime)
	{
		// TODO: maybe (1 - framesToPull) is needed? 
		//int framesToPull = (int)((partialTime / ShotTime()) * (FrameUpperIndex - FrameLowerIndex + 1));
		int framesToPull = (int)((1 - partialTime / ShotTime()) * (FrameUpperIndex - FrameLowerIndex + 1));
		byte[] buffer = new byte[320*240*3];
		try {
			source.seek(Index.FrameIndexToBytes(FrameLowerIndex));
			for(int i = 0; i < framesToPull; i++)
			{
				source.read(buffer);
				output.write(buffer);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/	
	public void trimTime(float partialTime) {
		double rate = (1 - partialTime/ShotTime());
		FrameUpperIndex = (long) (FrameLowerIndex + rate * (FrameUpperIndex - FrameLowerIndex));
	}
	
	public void OutputSoundToBuffer(WaveUtility wur) {
		wur.readInBufferRandomPeriod((double)FrameLowerIndex/framesPerSecond, (double)FrameUpperIndex/framesPerSecond);
		wur.appendToOutputBuffer(wur.getBuffer());
	}
	
/*	
	public void OutputSoundToBufferPartial(WaveUtility wur, double overFlowSeconds) {
		double rate = (1 - overFlowSeconds/ShotTime());
		System.out.println("OutputSoundToBufferPartial rate is: " + rate);
		wur.readInBufferRandomPeriod((double)FrameLowerIndex/framesPerSecond, (double)(FrameLowerIndex + rate * (FrameUpperIndex - FrameLowerIndex))/framesPerSecond);
		wur.appendToOutputBuffer(wur.getBuffer());
	}
*/
	
}
