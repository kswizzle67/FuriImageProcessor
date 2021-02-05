package Furi;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class exportData {

	public String FileName = "";
	public String FilePath = "";
	
	
	public static void exporttoCSV(){
		

	}
	
	public static void exporttocsvfile(String csvfile, ArrayList<pixelData> DtoS, ArrayList<pixelData> GoodPix, ArrayList<RedGreenBlue> GoodMultPixels) {
		
		String comma = ","; 
		String separator = "\n"; 
		
		String header = "FileName, FolderName, TotalPixels, ColoredPixels, StainedPixels, Signal (Stained/Colored), Cell Count (Single Stain), Cell Count (Multi Stain)";
		
		FileWriter fileWriter = null; 
			try {
			
			fileWriter = new FileWriter(csvfile);
			fileWriter.append(header.toString());
			fileWriter.append(separator); 
			
			for(pixelData pd : DtoS) {
				fileWriter.append(pd.filename);
				fileWriter.append(comma);
				fileWriter.append(pd.foldername);
				fileWriter.append(comma);
				fileWriter.append(Double.toString(pd.dblTotalPixels));
				fileWriter.append(comma);
				fileWriter.append(Double.toString(pd.coloredpixels));
				fileWriter.append(comma);
				fileWriter.append(Double.toString(pd.stainedpixels));
				fileWriter.append(comma);
				fileWriter.append(Double.toString(pd.signal));
				fileWriter.append(separator);
				fileWriter.append(Boolean.toString(pixelData.counted));
				fileWriter.append(separator);
				fileWriter.append(Boolean.toString(RedGreenBlue.counted));
				fileWriter.append(separator);
			}
			fileWriter.close();
			
			System.out.println("CSV successful!"); 
		}
		catch (Exception e) {
		}
	}
	
	
	
}
