package utility;

public class Index {
	static private final int FrameWidth = 320;
	static private final int FrameHeight = 240;
	
	static public int FromIndexToX(int arrayIndex)
	{
		return (arrayIndex % (FrameWidth*FrameHeight)) % FrameWidth;	
	}
	
	static public int FromIndexToY(int arrayIndex)
	{
		return (arrayIndex % (FrameWidth*FrameHeight)) / FrameWidth;	
	}
	
	static public int FromXYtoIndex(int x, int y)
	{
		return x + (y * FrameWidth);	
	}
	
	static public int FromXYtoIndex(int x, int y, int offset)
	{
		return offset + x + (y * FrameWidth);	
	}

}
