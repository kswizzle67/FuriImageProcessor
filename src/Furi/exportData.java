package Furi;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class exportData {

	public String FileName = "";
	public String FilePath = "";
	
	
	public static void exporttoCSV(){
		
		// listener? for window where they can type file name. 
		// and it sets it equal to namecsv
		
		
		// want a button to say "save to excel" and it saves current slider bar value.
		// https://examples.javacodegeeks.com/core-java/writeread-csv-files-in-java-example/
		// I don't know why type final won't work. 
		 
		
	
	}
	
	public static void exporttocsvfile(File csvfile, ArrayList<pixelData> DtoS) {
		
		String comma = ","; 
		String separator = "\n"; 
		
		String header = "X, Y, r, g, b, H, S, B, Percent Area";
		
		
		
		// x value, y value, r, g, b, h, s, b, %signal
		
		//calculate percent area and add it as last element
		// we need total number of pixels that fall within accepted
		// range divided by total pixels in image. Doesn't
		// matter that image was scaled down. 
		
		
		
		FileWriter fileWriter = null; 
			try {
			
		fileWriter = new FileWriter(csvfile);
			fileWriter.append(header.toString());
			fileWriter.append(separator); 
			
			for(pixelData pd : DtoS) {
				fileWriter.append(Integer.toString(pd.x));
				fileWriter.append(separator);
				fileWriter.append(Integer.toString(pd.y));
				fileWriter.append(separator);
				fileWriter.append(Integer.toString(pd.r));
				fileWriter.append(separator);
				fileWriter.append(Integer.toString(pd.g));
				fileWriter.append(separator);
				fileWriter.append(Integer.toString(pd.b));
				fileWriter.append(separator);
				fileWriter.append(Float.toString(pd.h));
				fileWriter.append(separator);
				fileWriter.append(Float.toString(pd.s));
				fileWriter.append(separator);
				fileWriter.append(Float.toString(pd.br));
				fileWriter.append(separator);
				
				
				
			}
			System.out.println("CSV successful!"); 
		}
		catch (Exception e) {
		}
	}
	
	
	
}
