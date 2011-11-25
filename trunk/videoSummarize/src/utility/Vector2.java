package utility;
import java.lang.Math;
public class Vector2 {
	public float X = 0;
	public float Y = 0;
	
	public Vector2()
	{
	}
	
	public Vector2(float incX, float incY)
	{
		X = incX;
		Y = incY;
	}
	
	static public Vector2 Add(Vector2 vecOne, Vector2 vecTwo)
	{	
		return new Vector2(vecOne.X + vecTwo.X, vecOne.Y + vecTwo.Y);
	}
	
	public double Distance()
	{
		return Math.sqrt(X * X + Y*Y);
	}
}
