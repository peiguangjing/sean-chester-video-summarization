package shot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import utility.Index;

import AnalyzedFrame.AnalyzedFrame;

public class Shot {
	private final int MinShotLength = 2*24;
	//Larger values correspond to less weight in the final overall score.

	//Motion weight is a weight on the total motion vector distances for a frame.  The idea here is that
	//frames with more motion are more important and shots with more motion are more important.
	private final int MotionWeight = 100;
	//Color weight is a weight on the sum of the color components of the average color for a frame.  The idea
	//here is that shots that are more saturated/brighter (thus having higher RGB values) are more important.
	private final int AverageColorWeight = 100;
	private final int ColorDifferenceWeight = 1000; 
	
	private int ShotIndex = 0;
	private float ShotImportance = 0.0f;
	
	private long FrameLowerIndex = 0;
	private long FrameUpperIndex = 0;
	
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
	
	public void CalculateShotImportance(Vector<AnalyzedFrame> frames)
	{
		//Apply weights given the information stored in each AnalyzedFrame in the sequence from FrameLowerIndex to FrameUpperIndex
		double motionContribution = 0.0f;
		double colorContribution = 0.0f;
		double colorDifferenceContribution = 0.0f;
		for(int index = (int)FrameLowerIndex; index <= FrameUpperIndex; index++)
		{
			motionContribution += frames.elementAt(index).TotalMotion();
			colorContribution += frames.elementAt(index).TotalColor();
		}
		motionContribution /= MotionWeight;
		colorContribution /= AverageColorWeight;
		colorDifferenceContribution /= ColorDifferenceWeight;
		ShotImportance =(float) (motionContribution + colorContribution + colorDifferenceContribution);
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
	
	public void OutputShot(RandomAccessFile source, FileOutputStream output, float partialTime)
	{
		int framesToPull = (int)((partialTime / ShotTime()) * (FrameUpperIndex - FrameLowerIndex + 1));
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
}
