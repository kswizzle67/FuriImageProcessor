package Furi;

public class RedGreenBlue {

	public int cellgroupid;
	public int r,g,b;
	public int x,y;
	public static boolean counted;
	
	public RedGreenBlue() {
			r=0;
			g=0;
			b=0;
			
	}
	
	public String toString()
	{
		return "r:" + String.valueOf(r) + " " + "g:" + String.valueOf(g) + " " + "b:" + String.valueOf(b) ;
		
	}
}
