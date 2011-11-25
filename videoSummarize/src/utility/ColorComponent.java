package utility;

public enum ColorComponent {
	RED(0),
	GREEN(320*240),
	BLUE(320*240*2);
	
	private final int offset;
	ColorComponent(int value)
	{
		offset = value;
	}
	
	public int Offset()
	{
		return offset;
	}
}
