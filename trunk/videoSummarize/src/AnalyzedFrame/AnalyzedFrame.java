package AnalyzedFrame;

import histogram.Histogram;

import java.io.IOException;
import java.io.Writer;
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
	private float TotalMotion = 0;
	private Vector2 AverageMotionDirection = new Vector2();
	private Colour AverageColor = new Colour(); // Corresponds to low frequency
	private Colour ColorDifferenceSum = new Colour(); // Corresponds to high frequency
	private int SoundIntensity = -1;
	private Histogram GrayScaleHistogram = new Histogram(64);
	
	public AnalyzedFrame()
	{

	}
	
	
	public void AnalyzeFrame(short[] frame,short[] lastFrame)
	{
		for(int blockX = 0; blockX < (FrameWidth / MacroBlockSize); blockX ++)
		{
			for(int blockY = 0; blockY < (FrameHeight / MacroBlockSize); blockY ++)
			{
				AnalyzeMotionContent(frame, lastFrame, blockX, blockY);
				AnalyzeColorValue(frame, blockX, blockY);
			}
		}
		AverageColor.Divide(FrameHeight*FrameWidth);
		
	}
	
	public void AnalyzeMotionContent(short[] frame, short[] lastFrame,int blockX, int blockY)
	{
		int originalX = blockX * MacroBlockSize;
		int originalY = blockY * MacroBlockSize;
		int beginX = originalX - MacroBlockSearchArea;
		int beginY = originalY - MacroBlockSearchArea;
		
		
		int interationLimit = (1 + 2*MacroBlockSearchArea);
		
		int lowestXBlock=0;
		int lowestYBlock=0;
		int lowestBlockDifferenceSum=100000000;
		int currentBlockDifferenceSum;
		
		int targetIndex, originalIndex;
		
		Vector2 blockMotionVector = new Vector2();
		for(int searchTargetX = beginX >= 0 ? beginX : 0; (searchTargetX < beginX + interationLimit) && searchTargetX < FrameWidth; searchTargetX++)
		{
			for(int searchTargetY = beginY >= 0 ? beginY : 0; (searchTargetY < beginY + interationLimit) && searchTargetY < FrameHeight; searchTargetY++)
			{
				currentBlockDifferenceSum = 0;
				for(int pixelX = searchTargetX, offsetX = 0; offsetX < MacroBlockSize && pixelX < FrameWidth; pixelX++,offsetX++)
				{
					for(int pixelY = searchTargetY, offsetY = 0; offsetY < MacroBlockSize && pixelY < FrameHeight; pixelY++,offsetY++)
					{
						targetIndex = Index.FromXYtoIndex(pixelX, pixelY);
						originalIndex = Index.FromXYtoIndex(originalX+offsetX, originalY+offsetY);
						currentBlockDifferenceSum += Math.abs(lastFrame[targetIndex] - frame[originalIndex]);
						currentBlockDifferenceSum += Math.abs(lastFrame[targetIndex + ColorComponent.GREEN.Offset()] - frame[originalIndex + ColorComponent.GREEN.Offset()]);
						currentBlockDifferenceSum += Math.abs(lastFrame[targetIndex + ColorComponent.BLUE.Offset()] - frame[originalIndex + ColorComponent.BLUE.Offset()]);
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
		
		blockMotionVector.X = originalX - lowestXBlock;
		blockMotionVector.Y = originalY - lowestYBlock;
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
