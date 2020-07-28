package Furi;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;
import java.util.ArrayList;

// test. 
public class phase1 extends JFrame {

	static JMenuBar mb;// = new JMenuBar();
	static JMenu mFile;// = new JMenu("Open File");
	static JMenuItem FileOpenFile, FileOpenFolder;
	static JFrame FramePicture = new JFrame("Image Processor");
	static JSlider s; 
	static ArrayList<File> arrFiles = new ArrayList<File>();
	static JButton btnSave, btnSaveToLocation, btnForwardImg, btnBackImg; 
	static JTextField txtSaveTo, txtR, txtG, txtB,txtThreshold;
	static JLabel lblSaveTo, imgLabel;
	static int intCurrentFile = 0;
	static BufferedImage imgSource, imgWorking;
	static Image resizedImage;
	static JRadioButton rdoHRP, rdoIFC; 
	static ButtonGroup rdoGroup;
	static JPanel rdoPanel;
	static JCheckBox chkTrackClicks; //used to track clicks and estimate colors
	static int[][] rgb = {{0,255},{0,255},{0,255}}; //these are actually reversed.
	
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		UI();
	}
	
	public static void UI(){
		
		mb = new JMenuBar();
		mFile = new JMenu("File");
		FileOpenFile = new JMenuItem("Open File");
		FileOpenFolder = new JMenuItem("Open Folder");
		
		imgLabel = new JLabel();
		imgLabel.setSize(600,600);
		imgLabel.setLocation(50, 1);
		imgLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//JAS
		imgLabel.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
            	 BufferedImage img = (BufferedImage)resizedImage;
                 int packedInt = img.getRGB(e.getX(), e.getY());
                 Color color = new Color(packedInt, true);
                txtR.setText(Integer.toString(color.getRed()));
                txtG.setText(Integer.toString(color.getGreen()));
                txtB.setText(Integer.toString(color.getBlue()));
                if(chkTrackClicks.isSelected())
                {
                	AddToClickRGBAverage(color.getRed(), color.getGreen(), color.getBlue());
                }
             }
         });
		
		mFile.add(FileOpenFile);
		mFile.add(FileOpenFolder);
			
		mb.add(mFile);
		FramePicture.setJMenuBar(mb);
		FramePicture.add(imgLabel);
		
		AddSaveButton();
		AddSlider();
		AddtxtSaveTo();
		AddForwardandBackButtons();
		
		//JAS
		AddExtraUI();
		AddHRPandIFCRadios();
		AddTrackClicksCheckBox();
		
		FramePicture.setSize(900,850);
		FramePicture.setLayout(null);
		FramePicture.setVisible(true);

		FileOpenFolder.addActionListener(new ActionListener(){
			@Override			
			public void actionPerformed(ActionEvent arg0) {
				try {
					folderopener();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileOpenFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					fileopener();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
		
	public static void fileopener() throws IOException {
		
		JFileChooser J = new JFileChooser();
		
		J.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		if (J.showOpenDialog(FramePicture) == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = J.getSelectedFile();
		    arrFiles.add(selectedFile);
		    imgSource = ImageIO.read(selectedFile);
		    imgWorking = deepCopyImage(imgSource);
	    	LoadImageIntoUI(imgSource);
		}
	}
	
	static BufferedImage deepCopyImage(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	public static void LoadImageIntoUI(BufferedImage imgIn) throws IOException
	{
		 int w, h; 
		    //for now, let's go with 600px on the widest side.
		 BufferedImage img = deepCopyImage(imgIn);
		    w = img.getWidth();
		    h = img.getHeight();
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
		     resizedImage = getScaledImage(img, (int)(Math.round(w/reduceby)),
		    		 (int)(Math.round(h/reduceby)));
	        ImageIcon icon = new ImageIcon(resizedImage); //use resizedImage here
	        imgLabel.setSize(icon.getIconWidth(), icon.getIconHeight());
		    imgLabel.setIcon(icon);
	}
	
	public static void AddSaveButton()
	{
		btnSave = new JButton("Save Image");
		btnSave.setBounds(50,100,95,30);
		btnSave.setLocation(50,730);
		btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {
						SaveFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		FramePicture.add(btnSave);
	}
	

	public static void AddForwardandBackButtons() 
	{
		btnForwardImg  = new JButton(">>");
		btnForwardImg.setBounds(50,100,50,30);
		btnForwardImg.setLocation(330,605);
		btnForwardImg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {
						MovetoNextImage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		
		btnBackImg = new JButton("<<");
		btnBackImg.setBounds(50,100,50,30);
		btnBackImg.setLocation(290,605);
		btnBackImg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					MovetoPreviousImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		FramePicture.add(btnForwardImg);
		FramePicture.add(btnBackImg);
	}

	public static void MovetoNextImage() throws IOException
	{
		if (intCurrentFile+1 < arrFiles.size())
		{
			intCurrentFile++;		
			 imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
			 imgWorking = deepCopyImage(imgSource);
	    	LoadImageIntoUI(imgSource);
		}
	}
	
	public static void MovetoPreviousImage() throws IOException
	{
		//TODO: this method ;)
		if (intCurrentFile != 0) {
			intCurrentFile--; 
			imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
			imgWorking = deepCopyImage(imgSource);
			LoadImageIntoUI(imgSource);
			
		}
	}
	
	public static void AddtxtSaveTo()
	{
		lblSaveTo = new JLabel("Save Files to:");
		lblSaveTo.setSize(200,30);
		lblSaveTo.setLocation(50,665);
		FramePicture.add(lblSaveTo);

		txtSaveTo = new JTextField();
		txtSaveTo.setSize(250, 30);
		txtSaveTo.setLocation(50,700);
		FramePicture.add(txtSaveTo);
		
		btnSaveToLocation = new JButton("...");
		btnSaveToLocation.setBounds(50,100,50,30);
		btnSaveToLocation.setLocation(290,700);
		btnSaveToLocation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					txtSaveTo.setText(FindDestinationToSaveTo());
			}
		});
		FramePicture.add(btnSaveToLocation);
	}
	public static String FindDestinationToSaveTo()
	{
		JFileChooser FolderChooser = new JFileChooser();
		
		FolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int op = FolderChooser.showOpenDialog(FolderChooser);
        if(op == JFileChooser.APPROVE_OPTION){
        	File folder =  FolderChooser.getSelectedFile();
        	return folder.getAbsolutePath();
        }
        else
        {
        	return "";
        }
	}
	
	public static void AddSlider()
	{
	 	s = new JSlider(0,100,0);
		s.setSize(600, 40);
		s.setLocation(50,635);
		//add the slider to the 
		FramePicture.add(s) ;
	
		s.setMajorTickSpacing(10);
		s.setMinorTickSpacing(1);
		s.setPaintTicks(true);
		s.setPaintLabels(true);
		
		
		//s.addChangeListener(phase1var);
		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
		        if (!source.getValueIsAdjusting()) {
		        	try {
						SatChange();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        }
			}
		});
	}
	
	public static void SaveFile() throws IOException
	{
		//TODO: we need to add the saturation to this image. 
		//Right now it is not affecting the SAVED image.
		File out = new File(txtSaveTo.getText() + "/" + arrFiles.get(intCurrentFile).getName());
		ImageIO.write(ImageIO.read(arrFiles.get(intCurrentFile)), "TIFF", out);
	}
    
	public static void folderopener() throws IOException{
			JFileChooser FolderChooser = new JFileChooser();
			
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
            //start at the first one...
            intCurrentFile = 0;
            imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
            imgWorking = deepCopyImage(imgSource);
            LoadImageIntoUI(imgSource);
	}
	
	//https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	private static Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	public static void SatChange() throws IOException{
		 //Here is where we will show image to user after sat change was made. 
		imgWorking = deepCopyImage(ActuallyChangeSaturation(imgSource,s.getValue()));
		LoadImageIntoUI(imgWorking); //use resizedImage here
	}
	
	private static BufferedImage ActuallyChangeSaturation(BufferedImage img, int saturationchange)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = deepCopyImage(img);
		float satchange;
		satchange = (float)(saturationchange*.01);
        System.out.println("Start! change:" + satchange);
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);		
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	if (r<Integer.parseInt(txtR.getText())&&g<Integer.parseInt(txtG.getText())&&b<Integer.parseInt(txtB.getText()))
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
	
	public static void exporttoCSV(){
		// want a button to say "save to excel" and it saves current slider bar value. 
		// https://www.baeldung.com/java-csv	
	}
	
	public static void AddExtraUI()
	{
		JLabel lblRGB = new JLabel("r,g,b:");
		lblRGB.setSize(200,30);
		lblRGB.setLocation(660,300);
		FramePicture.add(lblRGB);

		 txtR = new JTextField("255");
		txtR.setSize(40, 30);
		txtR.setLocation(660,330);
		FramePicture.add(txtR);
		
		 txtG = new JTextField("255");
		txtG.setSize(40, 30);
		txtG.setLocation(700,330);
		FramePicture.add(txtG);
		
		 txtB = new JTextField("255");
		txtB.setSize(40, 30);
		txtB.setLocation(740,330);
		FramePicture.add(txtB);
		
		 txtThreshold = new JTextField("20");
		 txtThreshold.setSize(40, 30);
		 txtThreshold.setLocation(840,370);
			FramePicture.add(txtThreshold);
			
		JButton btnProcessImage = new JButton("Estimate Values");
		btnProcessImage.setBounds(50,100,200,30);
		//btnProcessImage.setBorder(BorderFactory.createLineBorder(Color.black));
		btnProcessImage.setLocation(650,270);
		btnProcessImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProcessImage(imgSource);
				try {
					LoadImageIntoUI(imgWorking);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnProcessImage);	
		
		JButton btnMakeWhite = new JButton("Make white");
		btnMakeWhite.setBounds(50,100,200,30);
		btnMakeWhite.setLocation(650,370);
		btnMakeWhite.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = deepCopyImage(MakeIgnoredPixelsWhite(imgSource));
					LoadImageIntoUI(imgWorking); //use resizedImage here
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnMakeWhite);	
		
		JButton btnMakeWhiteUsingTrackedClicks = new JButton("Make white (Use Tracked Clicks)");
		btnMakeWhiteUsingTrackedClicks.setBounds(50,100,250,30);
		btnMakeWhiteUsingTrackedClicks.setLocation(650,400);
		btnMakeWhiteUsingTrackedClicks.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = deepCopyImage(MakeIgnoredPixelsWhiteUsingRange(imgSource));
					LoadImageIntoUI(imgWorking); //use resizedImage here
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnMakeWhiteUsingTrackedClicks);	
	}
	
	static void ProcessImage(BufferedImage img)
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
		txtR.setText(Integer.toString(sumR/count));
		txtG.setText(Integer.toString(sumG/count));
		txtB.setText(Integer.toString(sumB/count));
	}
	private static BufferedImage MakeIgnoredPixelsWhite(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int r,g,b,pixel;
		BufferedImage copy = deepCopyImage(img);
		int th = Integer.parseInt(txtThreshold.getText());
		for(int y = 0; y < h; y++) {
		    for(int x = 0; x < w; x++) {
		    	 pixel = img.getRGB(x, y);		
		    	 r = (pixel >> 16) & 0xFF;
		    	 g = (pixel >> 8) & 0xFF;
		    	 b = (pixel) & 0xFF;
		    	if ((r>Integer.parseInt(txtR.getText())+th
		    			||r<Integer.parseInt(txtR.getText())-th)||
		    			(g>Integer.parseInt(txtG.getText())+th
		    					||g<Integer.parseInt(txtG.getText())-th)||
		    			(b>Integer.parseInt(txtB.getText())+th
		    					||b<Integer.parseInt(txtB.getText())-th))
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
	
	private static BufferedImage MakeIgnoredPixelsWhiteUsingRange(BufferedImage img)
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
		    	}
		    }// For x
		}// For y
        
		return copy;
	}
	
	public static void AddHRPandIFCRadios()
	{
		//btnProcessImage.setBounds(50,100,200,30);
		//btnProcessImage.setLocation(650,270);
		
		rdoHRP = new JRadioButton("HRP");
		rdoHRP.setBounds(50,100,200,30);    
		rdoHRP.setLocation(650,200);
		rdoHRP.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           	txtR.setText("140");
	           	txtG.setText("140");
	           	txtB.setText("140");
	        }
	    });
		rdoIFC = new JRadioButton("IFC");
		rdoIFC.setBounds(50,100,200,30);   
		rdoIFC.setLocation(650,240);
		
		rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoHRP);
		rdoGroup.add(rdoIFC);
		
		FramePicture.getContentPane().add(rdoIFC);
		FramePicture.getContentPane().add(rdoHRP);
		
		//FramePicture.pack();
	
	}

	public static void AddTrackClicksCheckBox()
	{
		chkTrackClicks = new JCheckBox("Track Clicks!");
		chkTrackClicks.setBounds(100,100, 150,50);  
		chkTrackClicks.setLocation(650,160);
		FramePicture.getContentPane().add(chkTrackClicks);
		
	}
	
	public static void AddToClickRGBAverage(int R, int G, int B)
	{
		//						high,low
		//static int[][] rgb = {{0,255},{0,255},{0,255}}; //these are actually reversed.
		//why are they reversed? Because if the following RGB comes in:
		//125, 115, 160
		//I want to say IF the value is higher than the high (0) make this the high. 
		//opposite for low.
		//first value will replace both obviously...
		
		if (R>rgb[0][0])
			rgb[0][0] = R;
		
		if (R<rgb[0][1])
			rgb[0][1] = R;
		
		if (G>rgb[1][0])
			rgb[1][0] = G;
		
		if (G<rgb[1][1])
			rgb[1][1] = G;
		
		if (B>rgb[2][0])
			rgb[2][0] = B;
		
		if (B<rgb[2][1])
			rgb[2][1] = B;
		
		System.out.println(
				rgb[0][0] + " " + 
				rgb[0][1] + " " +
				rgb[1][0] + " " +
				rgb[1][1] + " " +
				rgb[2][0] + " " +
				rgb[2][1]);
	}
}
	