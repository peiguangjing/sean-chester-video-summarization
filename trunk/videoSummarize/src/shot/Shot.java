package shot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import utility.Index;

import AnalyzedFrame.AnalyzedFrame;

public class Shot {
	private final int MinShotLength = 5*24;
	//Larger values correspond to less weight in the final overall score.

	//Motion weight is a weight on the total motion vector distances for a frame.  The idea here is that
	//frames with more motion are more important and shots with more motion are more important.
	private final int MotionWeight = 100;
	//Color weight is a weight on the sum of the color components of the average color for a frame.  The idea
	//here is that shots that are more saturated/brighter (thus having higher RGB values) are more important.
	private final int ColorWeight = 1000; 
	
	private int ShotIndex = 0;
	private float ShotImportance = 0.0f;
	
	private int FrameLowerIndex = 0;
	private int FrameUpperIndex = 0;
	
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
		float motionContribution = 0.0f;
		float colorContribution = 0.0f;
		for(int index = FrameLowerIndex; index <= FrameUpperIndex; index++)
		{
			motionContribution += frames.elementAt(index).TotalMotion();
			colorContribution += frames.elementAt(index).TotalColor();
		}
		motionContribution /= MotionWeight;
		colorContribution /= ColorWeight;
		ShotImportance = motionContribution + colorContribution;;
	}
	
	public void OutputShot(RandomAccessFile source, FileOutputStream output)
	{
		byte[] buffer = new byte[(FrameUpperIndex - FrameLowerIndex + 1)*320*240*3];
		try {
			source.read(buffer, Index.FrameIndexToBytes(FrameLowerIndex), buffer.length);
			output.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void OutputShot(RandomAccessFile source, FileOutputStream output, float partialTime)
	{
		int framesToPull = (int)((partialTime / ShotTime()) * (FrameUpperIndex - FrameLowerIndex + 1));
		byte[] buffer = new byte[framesToPull*320*240*3];
		try {
			source.read(buffer, Index.FrameIndexToBytes(FrameLowerIndex), buffer.length);
			output.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
