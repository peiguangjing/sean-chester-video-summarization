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
	
	public void Subtract(float r, float g, float b)
	{
		R -= r;
		G -= g;
		B -= b;
	}
	
	public void Add(Colour other)
	{
		Add(other.R,other.G,other.B);
	}
	
	public void Divide(float divisor)
	{
		R /= divisor;
		G /= divisor;
		B /= divisor;
	}
	
	public void Set(Colour other)
	{
		R = other.R;
		G = other.G;
		B = other.B;
	}
	
	public void Subtract(Colour other)
	{
		Subtract(other.R,other.G,other.B);
	}
	
	public void Abs()
	{
		R = Math.abs(R);
		G = Math.abs(G);
		B = Math.abs(B);
	}
	
	static public Colour Add(Colour colourOne, Colour colourTwo)
	{
		return new Colour(colourOne.R + colourTwo.R, colourOne.G + colourTwo.G, colourOne.B + colourTwo.B);
	}
}
