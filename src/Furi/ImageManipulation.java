package Furi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ImageManipulation {
	
	
	
	
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
		    if(w>600||h<600)//see if the image is already small.
		    {
			    if(w>h)
			    {
			    	reduceby = (double)w/(double)600;
			    }
			    else
			    {
			    	reduceby = (double)h/(double)600;			    }
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
		    	if (
		    			(r>rgb[0][0]
		    			||r<rgb[0][1])
		    			||
		    			(g>rgb[1][0]
		    			||g<rgb[1][1])
		    			||
		    			(b>rgb[2][0]
    					||b<rgb[2][1]))
		    	{
			        copy.setRGB(x, y, new Color(255,255,255).getRGB());
		        }
		    	else
		    	{
		    		float hsb[] = Color.RGBtoHSB(r, g, b, null);
			        System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    		// pixel with stain
		    		pixelData pix = new pixelData();
		    		pix.x = x;
		    		pix.y = y;
		    		pix.r = r;
		    		pix.g = g;
		    		pix.b = b;
		    		pix.h = hsb[0];
		    		pix.s = hsb[1];
		    		pix.br = hsb[2];
		    		
		    		DatatoSave.add(pix);
		    		
		    	}
		    }// For x
		}// For y

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
		    	if ((r>R+th||r<R-th)||
	    			(g>G+th||g<G-th)||
		    		(b>B+th||b<B-th))
		    	{
			        copy.setRGB(x, y, new Color(255,255,255).getRGB());
		        }
		    	else
		    	{
		    		float hsb[] = Color.RGBtoHSB(r, g, b, null);
			        System.out.println("X:" + x + " y:" + y + " r:" + r + " g:" + g + " b:" + b + " H:" + hsb[0] + " s:" + hsb[1] + " b:" + hsb[2]);
		    	}
		    }// For x
		}// For y

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
}
