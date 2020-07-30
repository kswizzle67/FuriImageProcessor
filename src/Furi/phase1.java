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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.util.ArrayList;

// 2222
public class phase1 extends JFrame {

	
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
		JMenuBar mb;// = new JMenuBar();
		JMenu mFile;// = new JMenu("Open File");
		 JMenuItem FileOpenFile, FileOpenFolder;
		
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
					  //start at the first one...
		            intCurrentFile = 0;
					arrFiles = fileManipulation.folderopener();
					imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
		            imgWorking = ImageManipulation.deepCopyImage(imgSource);
		            LoadImageIntoUI(imgSource);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileOpenFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					File newfile = fileManipulation.fileopener(FramePicture);
					  
					arrFiles.add(newfile);
					imgSource =  ImageManipulation.FiletoBufferedImage(arrFiles.get(0));
					imgWorking = ImageManipulation.deepCopyImage(imgSource);
			    	LoadImageIntoUI(imgSource);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void LoadImageIntoUI(BufferedImage imgIn) throws IOException
	{
		 BufferedImage img = ImageManipulation.deepCopyImage(imgIn);
		  double reduceby = ImageManipulation.getReduceBy(img);
		     resizedImage = ImageManipulation.getScaledImage((Image)img, (int)(Math.round(img.getWidth()/reduceby)),
		    		 (int)(Math.round(img.getHeight()/reduceby)));
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
						fileManipulation.SaveFile(txtSaveTo.getText(), arrFiles.get(intCurrentFile));
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
			 imgWorking = ImageManipulation.deepCopyImage(imgSource);
	    	LoadImageIntoUI(imgSource);
		}
	}

	public static void MovetoPreviousImage() throws IOException
	{
		//TODO: this method ;)
		if (intCurrentFile != 0) {
			intCurrentFile--;
			imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
			imgWorking = ImageManipulation.deepCopyImage(imgSource);
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
					txtSaveTo.setText(fileManipulation.FindDestinationToSaveTo());
			}
		});
		FramePicture.add(btnSaveToLocation);
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

	public static void SatChange() throws IOException{
		 //Here is where we will show image to user after sat change was made.
		imgWorking = ImageManipulation.deepCopyImage(
				ImageManipulation.ActuallyChangeSaturation(
						imgSource,s.getValue(),
							Integer.parseInt(txtR.getText()), 
							Integer.parseInt(txtG.getText()),
							Integer.parseInt(txtB.getText())));
		LoadImageIntoUI(imgWorking); //use resizedImage here
	}

	

	public static void exporttoCSV(){
		
		// listener? for window where they can type file name. 
		// and it sets it equal to namecsv
		
		
		// want a button to say "save to excel" and it saves current slider bar value.
		// https://examples.javacodegeeks.com/core-java/writeread-csv-files-in-java-example/
		// I don't know why type final won't work. 
		private static final comma = ","; 
		private static final separator = "\n"; 
		
		private static final header = "X, Y, r, g, b, H, S, B, Percent Area"; 
		
		public static void csvfile(String namecsv) {
			
			
			List DatatoSave = new Arraylist();
			DatatoSave.add(x);
			DatatoSave.add(y);
			DatatoSave.add(r);
			DatatoSave.add(g);
			DatatoSave.add(b);
			
			//calculate percent area and add it as last element
			// we need total number of pixels that fall within accepted
			// range divided by total pixels in image. Doesn't
			// matter that image was scaled down. 
			
			
			
			FileWriter fileWriter = null; 
			try {
				
				fileWriter = new fileWriter(namecsv);
				fileWriter.append(header.toString());
				fileWriter.append(Separator); 
			}
			catch (Exception e) {
			}
		}
	
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
				ImageManipulation.ProcessImage(imgSource);
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
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhite(imgSource, 
								Integer.parseInt(txtThreshold.getText()),
								Integer.parseInt(txtR.getText()),
								Integer.parseInt(txtG.getText()),
								Integer.parseInt(txtB.getText())));
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
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhiteUsingRange(imgSource, rgb));
					LoadImageIntoUI(imgWorking); //use resizedImage here

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnMakeWhiteUsingTrackedClicks);
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
	}
}
