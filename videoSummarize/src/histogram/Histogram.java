package histogram;

import utility.Colour;

public class Histogram {
	private final int Threshold = 2000;//6450;
	private int[] Bins;
	private int BinCount;
	
	public Histogram(int binCount)
	{
		BinCount = binCount;
		Bins = new int[BinCount];
	}
	
	public void ProcessPixel(Colour incPixel)
	{
		//Convert to YUV from RGB space (http://en.wikipedia.org/wiki/YUV#Conversion_to.2Ffrom_RGB)
		int Y = (int) ( 0.299f * incPixel.R + 0.587f * incPixel.G + 0.114f * incPixel.B );
		
		//Y can range from 0 - 255.  Split this up into BinCount bins
		int index = (BinCount * Y) / 256;
		Bins[index]++;
	}
	
	public int CountInBin(int index)
	{
		return (index >= 0 && index < BinCount) ? Bins[index] : 0;
		
	}
	
	public int BinWiseDifference(Histogram other)
	{
		int otherIndex = 0;
		int totalAbsDifference = 0;
		for(int numInBin : Bins)
		{
			totalAbsDifference += Math.abs(other.CountInBin(otherIndex++) - numInBin);
		}
		return totalAbsDifference;
	}
	
	public Boolean ShotBoundary(Histogram other)
	{
		return Threshold < BinWiseDifference(other);
	}
}
