package utility;

public class Colour {
	public float R = 0.0f;
	public float G = 0.0f;
	public float B = 0.0f;
	
	public Colour()
	{
	
	}
	
	public Colour(float r, float g, float b)
	{
		R = r;
		G = g;
		B = b;
	}
	
	public void Add(float r, float g, float b)
	{
		R += r;
		G += g;
		B += b;
	}
	
	static public Colour Add(Colour colourOne, Colour colourTwo)
	{
		return new Colour(colourOne.R + colourTwo.R, colourOne.G + colourTwo.G, colourOne.B + colourTwo.B);
	}
}
