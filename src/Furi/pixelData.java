package Furi;

public class pixelData {
	// x value, y value, r, g, b, h, s, br, %signal
	public String filename, foldername; 
	public double dblTotalPixels, coloredpixels, stainedpixels; 
	public double signal; 
	
	public pixelData() {
		// TODO Auto-generated constructor stub
		signal = 0.0; 
		filename = "";
		foldername = "";
		dblTotalPixels = 0;
		coloredpixels = 0;
		stainedpixels = 0;
	}

}

