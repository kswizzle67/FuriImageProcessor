package Furi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class fileManipulation {

	static File csvfile;
	
	public static void SaveFile(String FileLocation, File fyl) throws IOException
	{
		//TODO: we need to add the saturation to this image.
		//Right now it is not affecting the SAVED image.
		File out = new File(FileLocation + "/" + fyl.getName());
		ImageIO.write(ImageIO.read(fyl), "TIFF", out);
	}
	
	public static ArrayList<File> folderopener() throws IOException{
		JFileChooser FolderChooser = new JFileChooser();
		 ArrayList<File> arrFiles = new ArrayList<File>();
		FolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int op = FolderChooser.showOpenDialog(FolderChooser);
        if(op == JFileChooser.APPROVE_OPTION){
           File folder = FolderChooser.getSelectedFile();
           // how to look for name of first file in folder without user having to tell us by clicking.
           // to open that, display button for next, save, and a slider bar for changing
           // then to look for next file and open that

           File[] allfiles = folder.listFiles();

           for(int i = 0; i < allfiles.length; i ++) {
        	   String name = allfiles[i].getName();
        	   //name = brainscan123.tif
        	   if (name.substring(name.length()-3).equals("tif"))
        	   {
        		    arrFiles.add(allfiles[i]);
        	   }
           }//end for i
        }
      
        return arrFiles;
        
	}
	public static File fileopener(JFrame FramePicture) throws IOException {

		JFileChooser J = new JFileChooser();

		J.setCurrentDirectory(new File(System.getProperty("user.home")));

		if (J.showOpenDialog(FramePicture) == JFileChooser.APPROVE_OPTION) {
		    return J.getSelectedFile();
		    //return ImageIO.read(selectedFile);
		}
		else
		{
			return null;
		}
	}
	
	public static String FindDestinationToSaveTo()
	{
		JFileChooser FolderChooser = new JFileChooser();

		FolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int op = FolderChooser.showOpenDialog(FolderChooser);
        if(op == JFileChooser.APPROVE_OPTION){
        	File folder =  FolderChooser.getSelectedFile();
        	File csvfile = folder; 
        	return folder.getAbsolutePath();
        }
        else
        {
        	return "";
        }
	}
}
