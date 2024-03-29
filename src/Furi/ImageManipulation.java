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
	static ArrayList<RedGreenBlue> rgbMulti = new ArrayList<RedGreenBlue>(); //this holds the RGB that the user selected for multi colors
	static int mcPosition; 
	
	static void SetColorForMultiColor(int i, Color c) {
		mcPosition = i;
		RedGreenBlue rgb = new RedGreenBlue();
		rgb.r = c.getRed();
		rgb.g = c.getGreen();
		rgb.b = c.getBlue();
		if(rgbMulti.size()<i)
		{
			rgbMulti.add(i-1, rgb);
		}
		else
		{
			rgbMulti.get(i-1).r =  rgb.r;
			rgbMulti.get(i-1).g =  rgb.g;
			rgbMulti.get(i-1).b =  rgb.b;
			
		}	
		Furi.OutPutThis("Color index " + i + " : " + rgb.toString());
	}
	
	
	//This tells us if the color is white or black usually. 
	//It is controlled by a radio button on the UI for white or black.
	static Boolean isBackgroundColor(int r, int g, int b)
	{
		if (Furi.rdoIgnoreWhite.isSelected())
		{
	    	if (r==255 && g==255 && b==255)
			{
			    		//it is already white. 
			    		//It has already been counted and should only be in totalpixels
			}
			{
				return true;
			}
		}
		else if (Furi.rdoIgnoreBlack.isSelected())
		{
			if (r==0&&g==0&&b==0)
			{
				return true;
			}		
		}
		return false;
	}
	
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

	static BufferedImage MakeIgnoredPixelsWhiteUsingRange(BufferedImage img, int[][] rgb, ArrayList<pixelData> DatatoSave, File fyl)
	{
		BufferedImage copy = new BufferedImage(1, 1, 1);
		if(img != null)
		{	
			
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		copy = deepCopyImage(img);
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    	//we deprecate this when we added the ability to ignore black
		    	 /*
		    	//we deprecate this when we added the ability to ignore black
		    	 /*
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}*/
		    	if (isBackgroundColor(r,g,b))
    			{
			    		//it is already white. 
			    		//It has already been counted and should only be in totalpixels
			        copy.setRGB(x, y, new Color(255,255,255).getRGB());		    		
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
		    		
		    		pixelData pix = new pixelData();
		  		  	pix.dblTotalPixels = dblTotalPixels;
		  		  	pix.coloredpixels = coloredpixels;
		  		  	pix.stainedpixels = stainedpixels;
		  		  	pix.signal = stainedpixels/dblTotalPixels;
		  		  	pix.x = x;
		  		  	pix.y = y;
			  		if(fyl != null)
			  		{
			  			pix.filename = fyl.getName();
			  			pix.foldername = fyl.getParent();
			  		}
			  		DatatoSave.add(pix);
		    	}
		    }// For x
		}// For y
		  System.out.println("Total:" + dblTotalPixels + " Colored: " + coloredpixels + " Stained:" + stainedpixels + " perc: " + stainedpixels/coloredpixels);
		  
		}
		return copy;
	}
	
	static BufferedImage MakeIgnoredPixelsWhite(BufferedImage img, int th, int R, int G, int B, ArrayList<pixelData> DatatoSave, File fyl)
	{
		BufferedImage copy = new BufferedImage(1, 1, 1);
		if(img != null)
		{	
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		copy = ImageManipulation.deepCopyImage(img);
		
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    	//we deprecate this when we added the ability to ignore black
		    	 /*
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}*/
		    	if (isBackgroundColor(r,g,b))
    			{
			    		//it is already white. 
			    		//It has already been counted and should only be in totalpixels
		    	    copy.setRGB(x, y, new Color(255,255,255).getRGB());
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
		    		pixelData pix = new pixelData();
			   		  //we should really have the current file without going back to the
			   		  //furi class
			   		if(fyl != null)
			   		{
			   			pix.filename = fyl.getName();
			   			pix.foldername = fyl.getParent();
			   		}
			   		pix.dblTotalPixels = dblTotalPixels;
			   		pix.coloredpixels = coloredpixels;
			   		pix.stainedpixels = stainedpixels;
			   		pix.signal = stainedpixels/coloredpixels;
					pix.filename = Furi.filename;
					pix.foldername = Furi.foldername;
			   		pix.x = x;
			   		pix.y = y;
	     			DatatoSave.add(pix);
		    	    System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    	}
		    }// For x
		}// For y
		  System.out.println("Total:" + dblTotalPixels + " Colored: " + coloredpixels + " Stained:" + stainedpixels + " perc: " + stainedpixels/coloredpixels );
		 
		}
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
		int r,g,b,pixel,sumR, sumG, sumB,count;
		sumR = 0;
		sumG = 0;
		sumB = 0;
		count = 0;
		if(img != null)
		{
			int w = img.getWidth();
			int h = img.getHeight();
			
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
		else
		{
			return new String[] {"0","0","0"};		
		}
	}
	static void automatefolder(ArrayList<File> arrFiles, int[][] rgb, int th, ArrayList<pixelData> DatatoSave) 
	{
			for(File fyl : arrFiles) {
				MakeIgnoredPixelsWhiteUsingRange(FiletoBufferedImage(fyl), rgb, DatatoSave, fyl);
			}
	}
	
	static int cellgroupid = 0;
	//this is for the green cells
	static BufferedImage MakeIgnoredPixelsWhiteCellCount(BufferedImage img, int th, int R, int G, int B, ArrayList<pixelData> DatatoSave)
	{
		cellgroupid = 0;
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = ImageManipulation.deepCopyImage(img);
		
		ArrayList<pixelData> GreenPixels = new ArrayList<pixelData>();
		
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
			    	
		    	//we deprecate this when we added the ability to ignore black
		    	 /*
		    	if (r==255&&g==255&&b==255)
    			{
		    		//it is already white. 
		    		//It has already been counted and should only be in totalpixels
    			}*/
		    	if (isBackgroundColor(r,g,b))
    			{
			    		//it is already white. 
			    		//It has already been counted and should only be in totalpixels
    			}
		    	else if ((r>R+th||r<R-th)||
	    			(g>G+th||g<G-th)||
		    		(b>B+th||b<B-th))
		    	{
		    		coloredpixels++;
		        }
		    	else
		    	{
		    		pixelData pixeldata = new pixelData();
		    		pixeldata.x = x;
		    		pixeldata.y = y;
		    		GreenPixels.add(pixeldata);
		    		
		    		coloredpixels++;
		    		stainedpixels++;
		    	    System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b);
		    	}
		    }// For x
		}// For y
		  
		ArrayList<pixelData> GoodPixels = new ArrayList<pixelData>();
		
		for (pixelData pd : GreenPixels) 
		{       
			//here we need to look around the pixel we are on to see if it is "surrounded" by other greens
			//we should be able to query the ArrayList, but for now let's create a method to do so.
			//easy, but we can make it better.
			CountPixelsThatAreCounted(GreenPixels);
			if(!pd.counted)
			{
				ArrayList<pixelData> GroupedPixels = FindGreenPixelsNearby(pd, GreenPixels);
				
				if(GroupedPixels.size()>=8)
				{ 	
					cellgroupid++;
					//add the pixel I am comparing. We know it is in a cell.
					//Furi.OutPutThis("Adding:" + String.valueOf(pd.x) + ":" + String.valueOf(pd.y));
					//GoodPixels.add(pd);	
					pd.cellgroupid = cellgroupid;
					pd.counted = true;
					//turn it red
					copy.setRGB(pd.x, pd.y, new Color(255,0,0).getRGB());
					
					for (pixelData pix : GroupedPixels) 
					{ 	
						int i = GreenPixels.indexOf(pix);
						GreenPixels.get(i).counted = true;
						GreenPixels.get(i).cellgroupid = cellgroupid;
						Furi.OutPutThis(String.valueOf(cellgroupid));
						Furi.OutPutThis("Adding:" + String.valueOf(pix.x) + ":" + String.valueOf(pix.y));
						GoodPixels.add(pix);	
						copy.setRGB(pix.x, pix.y, new Color(255,0,0).getRGB());
						pix.filename = Furi.filename;
						pix.foldername = Furi.foldername;
						pix.dblTotalPixels = dblTotalPixels;
				   		pix.coloredpixels = coloredpixels;
				   		pix.stainedpixels = stainedpixels;
				   		pix.signal = stainedpixels/coloredpixels;
						pix.filename = Furi.filename;
						pix.foldername = Furi.foldername;
				   		pix.x = pd.x;
				   		pix.y = pd.y;
		     			DatatoSave.add(pix);
					}
				}
			}
	    }
		Furi.OutPutThis("Good Pixels:" + String.valueOf(GoodPixels.size()));
		return copy;
	}
	
	private static ArrayList<pixelData> FindGreenPixelsNearby(pixelData pd, ArrayList<pixelData> GreenPixels) {
		int surroundingpixels = 0;
		
		ArrayList<pixelData> GroupedPixels = new ArrayList<pixelData>();
		
		for (pixelData pix : GreenPixels) 
		{       	
			if(!pix.counted && ((pix.x < pd.x +2 && pix.x > pd.x -2) &&
					(pix.y < pd.y +2 && pix.y > pd.y -2)))
			{
				GroupedPixels.add(pix);	
			}
	    }
		
		return GroupedPixels;
	}

	private static void CountPixelsThatAreCounted(ArrayList<pixelData> PixelArray)
	{
		int counted = 0;
		for (pixelData pd : PixelArray) 
		{
			if(pd.counted)
			{
				counted++;
			}
		}
		Furi.OutPutThis("Counted:" + String.valueOf(counted));
	}
	
	static BufferedImage MakeIgnoredPixelsWhiteCellCountMulti(BufferedImage img, int th, ArrayList<pixelData> DatatoSave, String filename, String foldername)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = ImageManipulation.deepCopyImage(img);
		coloredpixels = 0;
		stainedpixels = 0;
		ArrayList<pixelData> ColoredPixels = new ArrayList<pixelData>();
		
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	 dblTotalPixels++;
		    
		    	System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b);
		    	boolean thisisagoodpixel = false;	
		    	for (RedGreenBlue rgb: rgbMulti)
		    	{
		    		if (thisisagoodpixel == false)
		    		{
			    		if ((r<rgb.r+th&&r>rgb.r-th)&&
				    		(g<rgb.g+th&&g>rgb.g-th)&&
					    	(b<rgb.b+th&&b>rgb.b-th))
			    		{
			    			thisisagoodpixel = true;
				    		pixelData pixeldata = new pixelData();
				    		pixeldata.x = x;
				    		pixeldata.y = y;
				    		ColoredPixels.add(pixeldata);
				    		
				    		coloredpixels++;
				    		stainedpixels++;
				    	    System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + "Good");
				    	    System.out.println("Colored count:" + coloredpixels + ", Stained Count:" + stainedpixels);
			    		}
		    		}
		    	}		    	
		    	//still need this to turn the pixel white if it isn't found in the above loops
		    	if(thisisagoodpixel == false)
		    	{
		    		 copy.setRGB(x, y, new Color(255,255,255).getRGB());
		    		 
		    	}
		    }// For x
		}// For y
		ArrayList<pixelData> GoodPixels = new ArrayList<pixelData>();
		
			for (pixelData pd : ColoredPixels) 
			{       
				//here we need to look around the pixel we are on to see if it is "surrounded" by other greens
				//we should be able to query the ArrayList, but for now let's create a method to do so.
				//easy, but we can make it better.
				CountPixelsThatAreCounted(ColoredPixels);
				int j = 0;
				if(!pd.counted)
				{
					j++;
					Furi.OutPutThis(Integer.toString(j));
					ArrayList<pixelData> GroupedPixels = FindMultiPixelsNearby(pd, ColoredPixels);
					
					if(GroupedPixels.size()>=8)
					{ 	
						cellgroupid++;
						//add the pixel I am comparing. We know it is in a cell.
						//Furi.OutPutThis("Adding:" + String.valueOf(pd.x) + ":" + String.valueOf(pd.y));
						//GoodPixels.add(pd);	
						pd.cellgroupid = cellgroupid;
						pd.counted = true;
						//turn it red
						copy.setRGB(pd.x, pd.y, new Color(255,0,0).getRGB());
						
						for (pixelData pix : GroupedPixels) 
						{ 	
							int i = ColoredPixels.indexOf(pix);
							ColoredPixels.get(i).counted = true;
							ColoredPixels.get(i).cellgroupid = cellgroupid;
							Furi.OutPutThis(String.valueOf(cellgroupid));
							Furi.OutPutThis("Adding:" + String.valueOf(pix.x) + ":" + String.valueOf(pix.y));
							
							pix.filename = filename;
							pix.foldername = foldername;
							pix.x = pd.x;
							pix.y = pd.y;
					   		pix.dblTotalPixels = dblTotalPixels;
					   		pix.coloredpixels = coloredpixels;
					   		pix.stainedpixels = stainedpixels;
					   		pix.signal = stainedpixels/coloredpixels;
							pix.filename = Furi.filename;
							pix.foldername = Furi.foldername;
					   		GoodPixels.add(pix);	
							copy.setRGB(pix.x, pix.y, new Color(255,0,0).getRGB());
			     			DatatoSave.add(pix);
						}
					}
				}
		    }
			Furi.OutPutThis("Good Pixels:" + String.valueOf(GoodPixels.size()));
			DatatoSave = GoodPixels;
			return copy;
			
		}
	
	private static ArrayList<pixelData> FindMultiPixelsNearby(pixelData pd, ArrayList<pixelData> pix) 
	{
		int surroundingpixels = 0;
		ArrayList<pixelData> GroupedPixels = new ArrayList<pixelData>();
		
		for (pixelData px : pix) 
		{       	
			if(!px.counted && ((px.x < pd.x +2 && px.x > pd.x -2) &&
					(px.y < pd.y +2 && px.y > pd.y -2)))
			{
				GroupedPixels.add(px);	
			}
	    }
		return GroupedPixels;
	}

	
	
}
