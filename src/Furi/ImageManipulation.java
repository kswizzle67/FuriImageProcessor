package Furi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.ImageIO;


public class ImageManipulation {
	
	static double dblTotalPixels,coloredpixels, stainedpixels;
	
	
	//List<xy> arrxy = new ArrayList<xy>;
	// Need to know how to make a type "xy". 
	
	
	
	static BufferedImage FiletoBufferedImage(File in) 
	{
		try {
			return ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	static double getReduceBy(BufferedImage img)
	{
		  int w = img.getWidth();
		    int h = img.getHeight();
		    double reduceby = 1;
		    if(w>300||h<300)//see if the image is already small.
		    {
			    if(w>h)
			    {
			    	reduceby = (double)w/(double)300;
			    }
			    else
			    {
			    	reduceby = (double)h/(double)300;			    }
		    }
		    return reduceby;
	}
	
	//https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	static BufferedImage deepCopyImage(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}

	static BufferedImage MakeIgnoredPixelsWhiteUsingRange(BufferedImage img, int[][] rgb, ArrayList<pixelData> DatatoSave)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = deepCopyImage(img);
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}
		    	else if((r>rgb[0][0]
		    			||r<rgb[0][1])
		    			||
		    			(g>rgb[1][0]
		    			||g<rgb[1][1])
		    			||
		    			(b>rgb[2][0]
    					||b<rgb[2][1]))
		    	{
		    		coloredpixels++;
			        copy.setRGB(x, y, new Color(255,255,255).getRGB());
		        }
		    	else
		    	{
		    		float hsb[] = Color.RGBtoHSB(r, g, b, null);
			        System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    		// pixel with stain
		    		
		    		
		    		coloredpixels++;
		    		stainedpixels++;
		    	}
		    }// For x
		}// For y
		  System.out.println("Total:" + dblTotalPixels + " Colored: " + coloredpixels + " Stained:" + stainedpixels + " perc: " + stainedpixels/coloredpixels);
		  pixelData pix = new pixelData();
		  pix.dblTotalPixels = dblTotalPixels;
		  pix.coloredpixels = coloredpixels;
		  pix.stainedpixels = stainedpixels;
		  pix.signal = stainedpixels/coloredpixels;
		  //we should really have the current file without going back to the
		  //furi class
		  File fyl = Furi.arrFiles.get(Furi.intCurrentFile);
		  pix.filename = fyl.getName();
		  pix.foldername = fyl.getParent();
		  DatatoSave.add(pix);
		return copy;
	}
	static BufferedImage MakeIgnoredPixelsWhite(BufferedImage img, int th, int R, int G, int B, ArrayList<pixelData> DatatoSave)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = ImageManipulation.deepCopyImage(img);
		
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}
		    	else if ((r>R+th||r<R-th)||
	    			(g>G+th||g<G-th)||
		    		(b>B+th||b<B-th))
		    	{
		    		coloredpixels++;
			        copy.setRGB(x, y, new Color(255,255,255).getRGB());
		        }
		    	else
		    	{
		    		float hsb[] = Color.RGBtoHSB(r, g, b, null);
		    		
		    		coloredpixels++;
		    		stainedpixels++;
		    	    System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    	}
		    }// For x
		}// For y
		  System.out.println("Total:" + dblTotalPixels + " Colored: " + coloredpixels + " Stained:" + stainedpixels + " perc: " + stainedpixels/coloredpixels );
		  pixelData pix = new pixelData();
		  //we should really have the current file without going back to the
		  //furi class
		  File fyl = Furi.arrFiles.get(Furi.intCurrentFile);
		  pix.filename = fyl.getName();
		  pix.foldername = fyl.getParent();
		  pix.dblTotalPixels = dblTotalPixels;
		  pix.coloredpixels = coloredpixels;
		  pix.stainedpixels = stainedpixels;
		  pix.signal = stainedpixels/coloredpixels;
		  
  			DatatoSave.add(pix);
		return copy;
	}
	static BufferedImage ActuallyChangeSaturation(BufferedImage img, int saturationchange, int R, int B, int G)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = ImageManipulation.deepCopyImage(img);
		float satchange;
		satchange = (float)(saturationchange*.01);
        System.out.println("Start! change:" + satchange);
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	if (r<R&&g<G&&b<B)
		    	{
			        float hsb[] = Color.RGBtoHSB(r, g, b, null);
			        if(x%10==0 && y%100==0)
			        {
				        System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
				    }
			        copy.setRGB(x, y, Color.HSBtoRGB(hsb[0], satchange, hsb[2]));
		        }
		    }// For x
		}// For y
        System.out.println("End! change:" + satchange);
		return copy;
	}
	static String[] ProcessImage(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel,sumR, sumG, sumB,count;
		sumR = 0;
		sumG = 0;
		sumB = 0;
		count = 0;
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 float hsb[] = Color.RGBtoHSB(r, g, b, null);

		    	if (r<255&&g<255&&b<255&&hsb[1]>.30)
		    	{
		    		System.out.print(hsb[1]>.30);
		    		System.out.println(" s =" + hsb[1]);
			       //Let's try just the sum of them...
		    		sumR += r;
		    		sumG += g;
		    		sumB += b;
		    		count++;
		        }
		    }// For x
		}// For y
		return new String[] {
				Integer.toString(sumR/count),
				Integer.toString(sumG/count),
				Integer.toString(sumB/count)};
		
	}
	static void automatefolder(ArrayList<File> arrFiles, int[][] rgb, int th, ArrayList<pixelData> DatatoSave) 
	{
		
			for(File fyl : arrFiles) {
				
			MakeIgnoredPixelsWhiteUsingRange(FiletoBufferedImage(fyl), rgb, DatatoSave);
			
			}
	}
	
	//this is for the green cells
	static BufferedImage MakeIgnoredPixelsWhiteCellCount(BufferedImage img, int th, int R, int G, int B, ArrayList<pixelData> DatatoSave)
	{
		
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = ImageManipulation.deepCopyImage(img);
		
		ArrayList<pixelData> GreenPixels = new ArrayList<pixelData>();
		
		//int[] yarr = {h};
		//System.out.println(yarr);
		//int[] xarr = {w};
		//System.out.println(xarr);
		//make x y arrays
		
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    	// int[] arrx = {x};
		    	// int[] arry = {y};
		    	 System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b);
			    	
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}
		    	else if ((r>R+th||r<R-th)||
	    			(g>G+th||g<G-th)||
		    		(b>B+th||b<B-th))
		    	{
		    		//assign the x y coord to array (2D array)
		    		//int[][] arrxy = new int[x][y];
		    		//this is a colored pixel, but it isn't a green one.
		    		//we really may not need to capture this, but it was already there so we kept it.
		    		coloredpixels++;
			        //copy.setRGB(x, y, new Color(255,255,255).getRGB());
		        }
		    	else
		    	{
		    		//this is a green pixel. 
		    		//it doesn't mean it is a "cell" it means it is a single ixel
		    		float hsb[] = Color.RGBtoHSB(r, g, b, null);
		    		//create a pixelData object to store the green cell location.
		    		//TODO: anything else here?
		    		pixelData pixeldata = new pixelData();
		    		pixeldata.x = x;
		    		pixeldata.y = y;
		    		GreenPixels.add(pixeldata);
		    		
		    		coloredpixels++;
		    		stainedpixels++;
		    	    System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    	}
		    }// For x
		}// For y
		  //System.out.println("Total:" + dblTotalPixels + " Colored: " + coloredpixels + " Stained:" + stainedpixels + " perc: " + stainedpixels/coloredpixels );
		  //pixelData pix = new pixelData();
		  //we should really have the current file without going back to the
		  //furi class
		  /*File fyl = Furi.arrFiles.get(Furi.intCurrentFile);
		  pix.filename = fyl.getName();
		  pix.foldername = fyl.getParent();
		  pix.dblTotalPixels = dblTotalPixels;
		  pix.coloredpixels = coloredpixels;
		  pix.stainedpixels = stainedpixels;
		  pix.signal = stainedpixels/coloredpixels;
		  
  			DatatoSave.add(pix);*/
		//we should be adjusting the pixels here I assume in the COPY image that we return..
		//So return is last which gives us the option to adjust the cell colors like we discussed.
		
		
		ArrayList<pixelData> GoodPixels = new ArrayList<pixelData>();
		
		for (pixelData pd : GreenPixels) 
		{       
			//here we need to look around the pixel we are on to see if it is "surrounded" by other greens
			//we should be able to query the ArrayList, but for now let's create a method to do so.
			//easy, but we can make it better.
			
			if(FindGreenPixelsNearby(pd, GreenPixels)>=3)
			{
				GoodPixels.add(pd);	
				copy.setRGB(pd.x, pd.y, new Color(255,0,0).getRGB());
			}
	    }
		//here I can adjust "copy" to turn the green pixels "red" or whatever...
		// I think the thing is I have two sets of pixels.
		//for now I will loop again..
		//we really do need to learn how to query an arraylist...
		//wait. I think I can do this above..
		
		
		/*for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 //so find this x,y in the green pixels arraylist. 
		    }
		}
		*/
		return copy;
	}
	private static int FindGreenPixelsNearby(pixelData pd, ArrayList<pixelData> GreenPixels) {
		int surroundingpixels = 0;
		
		//woah this is wasteful
		for (pixelData pix : GreenPixels) 
		{       	
			if((pix.x < pd.x +3 && pix.x > pd.x -3) &&
					(pix.y < pd.y +3 && pix.y > pd.y -3))
			{
				//we have another pixel within +-3
				surroundingpixels++;
			}
	    }	
		return surroundingpixels;
	}

	
}
