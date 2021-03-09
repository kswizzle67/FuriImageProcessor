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
public class Furi extends JFrame {

	//these are things that may need to be reset.
	static ArrayList<pixelData> DatatoSave = new ArrayList<pixelData>();
	static ArrayList<File> arrFiles = new ArrayList<File>();
	static ArrayList<Integer> MultiColors = new ArrayList<Integer>();
	
	static int intCurrentFile = 0;
	static BufferedImage imgSource, imgWorking;
	static Image resizedImage;
	static int[][] rgb = {{0,255},{0,255},{0,255}}; //these are actually reversed.


	static JMenuBar mb;// = new JMenuBar();
	static JMenu mFile, mHelp;// = new JMenu("Open File");
	static JMenuItem FileOpenFile, FileOpenFolder, mHelpDiag;
	static JFrame FramePicture = new JFrame("Image Processor");
	static JSlider s;
	static JButton btnSave, btnSaveToLocation, btnForwardImg, btnBackImg, btnAuto;
	static JTextField txtSaveTo, txtR, txtG, txtB,txtThreshold;
	static JLabel lblSaveTo, imgLabel, lblFileNameTop,lbllblFileNameTop,lblHRPIFC, lblColor;
	static JTextArea lblOutput;
	static JRadioButton rdoHRP, rdoIFC, rdoMultiColor;
	static ButtonGroup rdoGroup;
	static JPanel rdoPanel;
	static JCheckBox chkTrackClicks; //used to track clicks and estimate colors
	static File csvfile;
	static JScrollPane sbrText;

	static JButton countCells;
	static JRadioButton rdoBlueIFC;
	static JComboBox<String> cblMultiColors;
	static JButton countCellsMult;

	

	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		UI();
	}

	public static void UI(){
		JMenuBar mb;// = new JMenuBar();
		JMenu mFile;// = new JMenu("Open File");
		 JMenuItem FileOpenFile, FileOpenFolder, mHelpDiag;

		mb = new JMenuBar();
		mFile = new JMenu("File");
		mHelp = new JMenu ("Help");
		FileOpenFile = new JMenuItem("Open File");
		FileOpenFolder = new JMenuItem("Open Folder");
		mHelpDiag = new JMenuItem("Get Help");

		imgLabel = new JLabel();
		imgLabel.setSize(300,300);
		imgLabel.setLocation(50, 50);
		imgLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		FramePicture.add(imgLabel);


		//JAS
		imgLabel.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
            	 if (resizedImage != null) {
            		 //this just sets the RGB textboxes
	            	 BufferedImage img = (BufferedImage)resizedImage;
	                 int packedInt = img.getRGB(e.getX(), e.getY());
	                 Color color = new Color(packedInt, true);
	                txtR.setText(Integer.toString(color.getRed()));
	                txtG.setText(Integer.toString(color.getGreen()));
	                txtB.setText(Integer.toString(color.getBlue()));
	                lblColor.setBackground(color);
	                if(chkTrackClicks.isSelected())
	                {
	                		AddToClickRGBAverage(color.getRed(), color.getGreen(), color.getBlue());
	                }
	                
	                if(rdoMultiColor.isSelected())
	                {
	                	//here is where we will track which pixels go into which index of the array
	                	//when the user is doing multicolor, they get to tell us which index to apply the color too. 
	                	//what we need to end up with is list of upper and lower bounds for each type of color.
	                	//it will be like trackclicks, but more than one color...
	                ImageManipulation.SetColorForMultiColor(Integer.parseInt(cblMultiColors.getItemAt(cblMultiColors.getSelectedIndex())), color);
	        		

	                }
            	 }
             }
         });

		lbllblFileNameTop = new JLabel("Filename: ");
		lbllblFileNameTop.setSize(70,30);
		lbllblFileNameTop.setLocation(10, -5);
		FramePicture.add(lbllblFileNameTop);

		lblFileNameTop = new JLabel();
		lblFileNameTop.setSize(400,20);
		lblFileNameTop.setLocation(10, 16);
		lblFileNameTop.setBorder(BorderFactory.createLineBorder(Color.gray));
		FramePicture.add(lblFileNameTop);

		mFile.add(FileOpenFile);
		mFile.add(FileOpenFolder);
		
		mHelp.add(mHelpDiag);
		mb.add(mFile);
		mb.add(mHelp);
		FramePicture.setJMenuBar(mb);

		//Not using slider anymore?
		//AddSlider();
		AddtxtSaveTo();
		AddForwardandBackButtons();
		CellCountMulti();

		//JAS
		AddExtraUI();
		AddHRPandIFCRadios();
		AddTrackClicksCheckBox();
		CellCount();


		FramePicture.setSize(700,550);
		FramePicture.setLayout(null);
		FramePicture.setVisible(true);

		FileOpenFolder.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					//should enable the auto button if they have clicked a folder
					btnAuto.setEnabled(true);
					  //start at the first one...
					ResetVariables(true);
		            intCurrentFile = 0;
					arrFiles = fileManipulation.folderopener();
					ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
					txtSaveTo.setText(arrFiles.get(intCurrentFile).getPath().substring(0, arrFiles.get(intCurrentFile).getPath().lastIndexOf("/")+1) + "output.csv");
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
					//should disable the folder option if they haven't clicked a folder
					btnAuto.setEnabled(false);
					ResetVariables(true);
					File newfile = fileManipulation.fileopener(FramePicture);
					intCurrentFile = 0;
					arrFiles.add(newfile);
					ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
					txtSaveTo.setText(arrFiles.get(intCurrentFile).getPath().substring(0, arrFiles.get(intCurrentFile).getPath().lastIndexOf("/")+1) + "output.csv");
					imgSource =  ImageManipulation.FiletoBufferedImage(arrFiles.get(intCurrentFile));
					imgWorking = ImageManipulation.deepCopyImage(imgSource);
			    	LoadImageIntoUI(imgSource);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mHelpDiag.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(FramePicture,"Please email KristinHuber@gmail.com for assistance!");	
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

	public static void AddForwardandBackButtons()
	{
		btnForwardImg  = new JButton(">>");
		btnForwardImg.setBounds(50,100,50,30);
		btnForwardImg.setLocation(350,200);
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
		btnBackImg.setLocation(5, 200);
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
			ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
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
			ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
			imgSource = ImageIO.read(arrFiles.get(intCurrentFile));
			imgWorking = ImageManipulation.deepCopyImage(imgSource);
			LoadImageIntoUI(imgSource);

		}
	}

	public static void ChangeImageLabel(String ImageName)
	{
		lblFileNameTop.setText(ImageName);
	}


	public static void AddtxtSaveTo()
	{
		lblSaveTo = new JLabel("Save Files to:");
		lblSaveTo.setSize(200,30);
		lblSaveTo.setLocation(25,375);
		FramePicture.add(lblSaveTo);

		txtSaveTo = new JTextField();
		txtSaveTo.setSize(250, 30);
		txtSaveTo.setLocation(25,405);
		FramePicture.add(txtSaveTo);

		btnSaveToLocation = new JButton("...");
		btnSaveToLocation.setBounds(50,100,50,30);
		btnSaveToLocation.setLocation(270,405);
		btnSaveToLocation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					txtSaveTo.setText(fileManipulation.FindDestinationToSaveTo());
			}
		});
		FramePicture.add(btnSaveToLocation);
		btnSave = new JButton("Save Data to CSV");
		btnSave.setBounds(25,100,150,30);
		btnSave.setLocation(25,435);
		btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					//fileManipulation.SaveFile(txtSaveTo.getText(), arrFiles.get(intCurrentFile));
					exportData.exporttocsvfile(txtSaveTo.getText(), DatatoSave);
			}
		});
		FramePicture.add(btnSave);
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

	public static void AddExtraUI()
	{
		JLabel lblR = new JLabel("Red:");
		lblR.setSize(200,30);
		lblR.setLocation(600, 15);
		FramePicture.add(lblR);

		JLabel lblG = new JLabel("Green:");
		lblG.setSize(200,30);
		lblG.setLocation(600, 35);
		FramePicture.add(lblG);

		JLabel lblB = new JLabel("Blue:");
		lblB.setSize(200,30);
		lblB.setLocation(600, 55);
		FramePicture.add(lblB);

		 txtR = new JTextField("255");
		txtR.setSize(40, 30);
		txtR.setLocation(640,15);
		FramePicture.add(txtR);

		 txtG = new JTextField("255");
		txtG.setSize(40, 30);
		txtG.setLocation(640,35);
		FramePicture.add(txtG);

		 txtB = new JTextField("255");
		txtB.setSize(40, 30);
		txtB.setLocation(640,55);
		FramePicture.add(txtB);

		lblColor = new JLabel("");
		lblColor.setSize(30,20);
		lblColor.setLocation(640, 85);
		lblColor.setOpaque(true);
		lblColor.setBackground(Color.lightGray);
		FramePicture.add(lblColor);

		
		 txtThreshold = new JTextField("20");
		 txtThreshold.setSize(40, 30);
		 txtThreshold.setLocation(650,360);
			FramePicture.add(txtThreshold);

		JButton btnProcessImage = new JButton("Estimate Values");
		btnProcessImage.setBounds(50,100,200,30);
		//btnProcessImage.setBorder(BorderFactory.createLineBorder(Color.black));
		btnProcessImage.setLocation(450,330);
		btnProcessImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String rgb[] = ImageManipulation.ProcessImage(imgSource);
				if (rgb != null)
				{
					txtR.setText(rgb[0]);
					txtG.setText(rgb[1]);
					txtB.setText(rgb[2]);
				}
				try {
					if(imgWorking != null)
					{
						LoadImageIntoUI(imgWorking);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnProcessImage);

		JButton btnMakeWhite = new JButton("Make white");
		btnMakeWhite.setBounds(50,100,200,30);
		btnMakeWhite.setLocation(450,360);
		btnMakeWhite.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhite(imgSource,
								Integer.parseInt(txtThreshold.getText()),
								Integer.parseInt(txtR.getText()),
								Integer.parseInt(txtG.getText()),
								Integer.parseInt(txtB.getText()), DatatoSave,null) );
					LoadImageIntoUI(imgWorking); //use resizedImage here

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnMakeWhite);

		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(50,100,200,30);
		btnReset.setLocation(150,350);
		btnReset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					ResetVariables(false); //not everything
					if(!arrFiles.isEmpty()) {
						ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
						txtSaveTo.setText(arrFiles.get(intCurrentFile).getPath().substring(0, arrFiles.get(intCurrentFile).getPath().lastIndexOf("/")+1) + "output.csv");
						imgSource =  ImageManipulation.FiletoBufferedImage(arrFiles.get(intCurrentFile));
						imgWorking = ImageManipulation.deepCopyImage(imgSource);
				    	try {
							LoadImageIntoUI(imgSource);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		});
		FramePicture.add(btnReset);

		JButton btnMakeWhiteUsingTrackedClicks = new JButton("Make white (Use Tracked Clicks)");
		btnMakeWhiteUsingTrackedClicks.setBounds(50,100,250,30);
		btnMakeWhiteUsingTrackedClicks.setLocation(450,390);
		btnMakeWhiteUsingTrackedClicks.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhiteUsingRange(
							imgSource, rgb, DatatoSave, null));
					LoadImageIntoUI(imgWorking); //use resizedImage here

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //use resizedImage here
			}
		});
		FramePicture.add(btnMakeWhiteUsingTrackedClicks);
		
		lblOutput = new JTextArea("");
		lblOutput.setSize(205, 150);
		lblOutput.setLocation(450,150);
		lblOutput.setLineWrap(true);
		
		sbrText = new JScrollPane(lblOutput);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//sbrText.setVisible(true);
		sbrText.setLocation(450,155);
		
		//FramePicture.add(lblOutput);
		//FramePicture.add(sbrText);
		//FramePicture.setVisible(true);
	}

	public static void AddHRPandIFCRadios()
	{
		//btnProcessImage.setBounds(50,100,200,30);
		//btnProcessImage.setLocation(650,270);

		lblHRPIFC = new JLabel("Type:");
		lblHRPIFC.setSize(70,40);
		lblHRPIFC.setLocation(450, 5);
		FramePicture.add(lblHRPIFC);

		// for IFC (green): r = 0, g = 254, b = 0;
		// for HRP: r = 171, g = 171, b = 141;
		// both have range +/- 20 for "make white". The button should "make white" as well.

		rdoHRP = new JRadioButton("HRP");
		rdoHRP.setBounds(25,20,150,30);
		rdoHRP.setLocation(450,35);
		rdoHRP.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           	txtR.setText("171");
	           	txtG.setText("171");
	           	txtB.setText("141");
	        }
	    });
		rdoIFC = new JRadioButton("IFC (green)");
		rdoIFC.setBounds(25,20,150,30);
		rdoIFC.setLocation(450,55);

		rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoHRP);
		rdoGroup.add(rdoIFC);

		FramePicture.getContentPane().add(rdoIFC);
		FramePicture.getContentPane().add(rdoHRP);

		rdoIFC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtR.setText("0");
	           	txtG.setText("254");
	           	txtB.setText("0");
			}
		});

		rdoBlueIFC = new JRadioButton("IFC (blue)");
		rdoBlueIFC.setBounds(25,20,150,30);
		rdoBlueIFC.setLocation(450,75); // edit this placement 
		FramePicture.getContentPane().add(rdoBlueIFC);
		rdoGroup.add(rdoBlueIFC);
		rdoBlueIFC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtR.setText("39");
	           	txtG.setText("0");
	           	txtB.setText("232"); 
	           	//user needs to allow for margin of 120. 
				}
			});
		
		rdoMultiColor = new JRadioButton("Multi Colors");
		rdoMultiColor.setBounds(25,20,120,30);
		rdoMultiColor.setLocation(450,95); // edit this placement 
		FramePicture.getContentPane().add(rdoMultiColor);
		rdoGroup.add(rdoMultiColor);
		rdoMultiColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OutPutThis( 
						"You have selected to use multiple Colors. \n" + 
						"The dropdown above should be on 1.\n" + 
						"This means you are selecting the first color\n" + 
						"When you want to select the second color, change the dropdown\n" );
	           
				}
			});
		
		String[] choices = {"1","2","3","4","5"};
	    cblMultiColors = new JComboBox<String>(choices);
	    cblMultiColors.setBounds(25,20,75,30);
	    cblMultiColors.setLocation(600,100); // edit this placement 
	    FramePicture.getContentPane().add(cblMultiColors);
	    cblMultiColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int colornumber = Integer.parseInt(cblMultiColors.getItemAt(cblMultiColors.getSelectedIndex()));
				OutPutThis("You are now working with Color #" + colornumber);
				
				int r,g,b;
				if (ImageManipulation.rgbMulti.size()>=colornumber)
				{
					r = ImageManipulation.rgbMulti.get(colornumber-1).r;
					g = ImageManipulation.rgbMulti.get(colornumber-1).g;
					b = ImageManipulation.rgbMulti.get(colornumber-1).b;
				}
				else
				{
					r=255;
					g=255;
					b=255;
				}
				Color color = new Color(r,g,b);
					
               txtR.setText(String.valueOf(r));
               txtG.setText(String.valueOf(g));
	           txtB.setText(String.valueOf(b));
               lblColor.setBackground(color);
               
				}
			});
		
		btnAuto = new JButton("Analyze Current Folder!"); 
		btnAuto.setBounds(50,50,220,50);
		btnAuto.setLocation(450, 450);
		FramePicture.getContentPane().add(btnAuto); 
		btnAuto.setEnabled(false);
	
		btnAuto.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ImageManipulation.automatefolder(arrFiles, rgb, Integer.parseInt(txtThreshold.getText()), DatatoSave); 
			}
		});
	}
	
	public static void OutPutThis(String x)
	{
		//lblOutput.setText(x + "\n" + lblOutput.getText());
       	//user needs to allow for margin of 120. 
		System.out.println(x);
	}
	
	public static void AddTrackClicksCheckBox()
	{
		chkTrackClicks = new JCheckBox("Track Clicks!");
		chkTrackClicks.setBounds(10,10,120,20);
		chkTrackClicks.setLocation(450,120);
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
		String s = "R=" + rgb[0][0] + "-" + rgb[0][1] + "\n"
				+ "G=" + rgb[1][0] + "-" + rgb[1][1] + "\n"
				+ "B=" + rgb[2][0] + "-" + rgb[2][1] + "\n";
		OutPutThis(s);
	}

	public static void ResetVariables(boolean everything)
	{
		//everything will tell us if they opened a new folder or file...
		//We should warn them if they haven't saved...
		if (everything)
		{
			DatatoSave.clear();
			arrFiles.clear();
			intCurrentFile = 0;
			lblFileNameTop.setText("");
			ImageIcon icon = new ImageIcon(); //use resizedImage here
	        imgLabel.setSize(300,300);
		    imgLabel.setIcon(icon);
			imgSource = null;
			imgWorking = null;
			resizedImage = null;
		}

		//these are just the temp vars.
		rgb[0][0] = 0;
		rgb[0][1] = 255;
		rgb[0][0] = 0;
		rgb[0][1] = 255;
		rgb[0][0] = 0;
		rgb[0][1] = 255;
	    txtR.setText("255");
	    txtG.setText("255");
	    txtB.setText("255");
	    rdoGroup.clearSelection();
	    lblOutput.setText("");

	}
	public static void CellCount(){
		countCells = new JButton("Cell Count!");
		countCells.setBounds(50,100,200,30);
		countCells.setLocation(450,420);
		FramePicture.getContentPane().add(countCells); 
		// countCells.setEnabled(false);
		countCells.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imgSource != null)
				{
					try {
						imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhiteCellCount(imgSource,
									Integer.parseInt(txtThreshold.getText()),
									Integer.parseInt(txtR.getText()),
									Integer.parseInt(txtG.getText()),
									Integer.parseInt(txtB.getText()), DatatoSave));
						LoadImageIntoUI(imgWorking); //use resizedImage here
	
					} catch (IOException z) {
						// TODO Auto-generated catch block
						z.printStackTrace();
					} //use resizedImage here
				}
			}
		});
	}
	
	
	public static void CellCountMulti(){
		countCellsMult = new JButton("Cell Count - Multi!");
		countCellsMult.setBounds(50,100,150,30);
		countCellsMult.setLocation(450,270);
		FramePicture.getContentPane().add(countCellsMult); 


		//at this point the user should have chosen an image or multiple images.
		//The would have picked one more more RGB values
		//so that arraylist exists.
		//then we want to use those values to process the image.
		countCellsMult.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imgSource != null)
				{
					try {
						imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhiteCellCountMulti(imgSource,
									Integer.parseInt(txtThreshold.getText()),
									 DatatoSave));
						LoadImageIntoUI(imgWorking); //use resizedImage here
	
					} catch (IOException z) {
						// TODO Auto-generated catch block
						z.printStackTrace();
					} //use resizedImage here
				}
			}
		});
}
}
