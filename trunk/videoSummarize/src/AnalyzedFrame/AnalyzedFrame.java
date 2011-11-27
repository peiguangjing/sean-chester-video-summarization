package AnalyzedFrame;

import histogram.Histogram;

import java.lang.Math;

import utility.ColorComponent;
import utility.Colour;
import utility.Index;
import utility.Vector2;


public class AnalyzedFrame {
	private final int FrameWidth = 320;
	private final int FrameHeight = 240;
	private final int MacroBlockSize = 4; // 4x4
	private final int MacroBlockSearchArea = 2; // (MacroBlockSize + MacroBlockSearchArea)x(MacroBlockSize + MacroBlockSearchArea) search area

	private int FrameIndex = -1;
	private int SceneIndex = -1;
	private float TotalMotion = -1;
	private Vector2 AverageMotionDirection = new Vector2();
	private Colour AverageColor = new Colour(); // Corresponds to low frequency
	private Colour ColorDifferenceSum = new Colour(); // Corresponds to high frequency
	private int SoundIntensity = -1;
	private Histogram GrayScaleHistogram = new Histogram(64);
	
	public AnalyzedFrame()
	{

	}
	
	
	public void AnalyzeFrame(short[] frame)
	{
		for(int blockX = 0; blockX < (FrameWidth / MacroBlockSize); blockX += MacroBlockSize)
		{
			for(int blockY = 0; blockY < (FrameHeight / MacroBlockSize); blockY += MacroBlockSize)
			{
				AnalyzeMotionContent(frame, blockX, blockY);
				AnalyzeColorValue(frame, blockX, blockY);
			}
		}
		AverageColor.Divide(FrameHeight*FrameWidth);
	}
	
	public void AnalyzeMotionContent(short[] frame, int blockX, int blockY)
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
				for(int pixelX = (beginX + searchTargetX) >= 0 ? beginX + searchTargetX : 0; pixelX < beginX + MacroBlockSize && pixelX < FrameWidth; pixelX++)
				{
					for(int pixelY = (beginY + searchTargetY) >= 0 ? beginY + searchTargetY : 0; pixelY < beginY + MacroBlockSize && pixelY < FrameHeight; pixelY++)
					{
						targetIndex = Index.FromXYtoIndex(pixelX, pixelY);
						originalIndex = Index.FromXYtoIndex(beginX+MacroBlockSearchArea, beginY+MacroBlockSearchArea);
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
	
	public void AnalyzeColorValue(short[] frame, int blockX, int blockY)
	{
		int beginX = blockX * MacroBlockSize;
		int beginY = blockY * MacroBlockSize;
		int index;
		Colour color = new Colour();
		Colour lastColor = new Colour();
		for(int pixelX = beginX; pixelX < beginX + MacroBlockSize; pixelX++)
		{
			for(int pixelY = beginY; pixelY < beginY + MacroBlockSize; pixelY++)
			{
				index = Index.FromXYtoIndex(pixelX, pixelY);
				color.R = frame[index]; color.G = frame[index+ColorComponent.GREEN.Offset()]; color.B = frame[index+ColorComponent.BLUE.Offset()];
				AverageColor.Add(color);
				GrayScaleHistogram.ProcessPixel(color);
				if( pixelY != beginY)
				{
					lastColor.Subtract(color);
					lastColor.Abs();
					ColorDifferenceSum.Add(lastColor);
				}
				lastColor.Set(color);
			}
		}
	}
	
	public Histogram GetHistogram()
	{
		return GrayScaleHistogram;
	}
	
	public Boolean ShotBoundaryDetection(AnalyzedFrame other)
	{
		return GrayScaleHistogram.ShotBoundary(other.GetHistogram());
	}
	
	public float TotalMotion()
	{
		return TotalMotion;
	}
	
	public float TotalColor()
	{
		return AverageColor.R + AverageColor.G + AverageColor.B;
	}
	
	public float TotalColorDifference()
	{
		return ColorDifferenceSum.R + ColorDifferenceSum.G + ColorDifferenceSum.B;
	}
}
