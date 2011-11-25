package AnalyzedFrame;

import java.lang.Math;

import utility.ColorComponent;
import utility.Colour;
import utility.Index;
import utility.Vector2;


public class AnalyzedFrame {
	private final int FrameWidth = 320;
	private final int FrameHeight = 240;
	private final int MacroBlockSize = 4; // 4x4
	private final int MacroBlockSearchArea = 1; // (MacroBlockSize + MacroBlockSearchArea)x(MacroBlockSize + MacroBlockSearchArea) search area

	private int FrameIndex = -1;
	private int SceneIndex = -1;
	private float TotalMotion = -1;
	private Vector2 AverageMotionDirection = new Vector2();
	private Colour AverageColor = new Colour();
	private int SoundIntensity = -1;
	
	public AnalyzedFrame()
	{

	}
	
	
	public void AnalyzeFrame(byte[] frame)
	{
		for(int blockX = 0; blockX < (FrameWidth / MacroBlockSize); blockX += MacroBlockSize)
		{
			for(int blockY = 0; blockY < (FrameHeight / MacroBlockSize); blockY += MacroBlockSize)
			{
				AnalyzeMotionContent(frame, blockX, blockY);
				AnalyzeColorValue(frame, blockX, blockY);
			}
		}
	}
	
	public void AnalyzeMotionContent(byte[] frame, int blockX, int blockY)
	{
		int beginX = blockX * MacroBlockSize - MacroBlockSearchArea;
		int beginY = blockY * MacroBlockSize - MacroBlockSearchArea;
		int interationLimit = (1 + 2*MacroBlockSearchArea);
		
		int lowestXBlock=0;
		int lowestYBlock=0;
		int lowestBlockDifferenceSum=1000000;
		int currentBlockDifferenceSum;
		
		int targetIndex, originalIndex;
		
		Vector2 blockMotionVector = new Vector2();
		for(int searchTargetX = beginX >= 0 ? beginX : 0; searchTargetX < beginX + interationLimit && searchTargetX < FrameWidth; searchTargetX++)
		{
			for(int searchTargetY = beginY >= 0 ? beginY : 0; searchTargetY < beginY + interationLimit && searchTargetY < FrameHeight; searchTargetY++)
			{
				currentBlockDifferenceSum = 0;
				for(int pixelX = beginX + searchTargetX; pixelX < beginX + MacroBlockSize && pixelX < FrameWidth; pixelX++)
				{
					for(int pixelY = beginY + searchTargetY; pixelY < beginY + MacroBlockSize && pixelY < FrameHeight; pixelY++)
					{
						targetIndex = Index.FromXYtoIndex(pixelX, pixelY);
						originalIndex = Index.FromXYtoIndex(beginX+1, beginY+1);
						currentBlockDifferenceSum += Math.abs(frame[targetIndex] - frame[originalIndex]);
						currentBlockDifferenceSum += Math.abs(frame[targetIndex + ColorComponent.GREEN.Offset()] - frame[originalIndex + ColorComponent.GREEN.Offset()]);
						currentBlockDifferenceSum += Math.abs(frame[targetIndex + ColorComponent.BLUE.Offset()] - frame[originalIndex + ColorComponent.BLUE.Offset()]);
					}
				}
				if( currentBlockDifferenceSum < lowestBlockDifferenceSum)
				{
					lowestBlockDifferenceSum = currentBlockDifferenceSum;
					lowestXBlock = searchTargetX;
					lowestYBlock = searchTargetY;
				}
			}
		}
		
		blockMotionVector.X += lowestXBlock - (blockX * MacroBlockSize);
		blockMotionVector.Y += lowestYBlock - (blockY * MacroBlockSize);
		TotalMotion += blockMotionVector.Distance();
		AverageMotionDirection = Vector2.Add(AverageMotionDirection, blockMotionVector);
	}
	
	public void AnalyzeColorValue(byte[] frame, int blockX, int blockY)
	{
		int beginX = blockX * MacroBlockSize;
		int beginY = blockY * MacroBlockSize;
		int index;
		for(int pixelX = beginX; pixelX < beginX + MacroBlockSize; pixelX++)
		{
			for(int pixelY = beginY; pixelY < beginY + MacroBlockSize; pixelY++)
			{
				index = Index.FromXYtoIndex(pixelX, pixelY);
				 AverageColor.Add(frame[index],frame[index+ColorComponent.GREEN.Offset()],frame[index+ColorComponent.BLUE.Offset()]);
			}
		}
	}
}
