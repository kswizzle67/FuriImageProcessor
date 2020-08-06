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

	static ArrayList<pixelData> DatatoSave = new ArrayList<pixelData>();
	static JMenuBar mb;// = new JMenuBar();
	static JMenu mFile;// = new JMenu("Open File");
	static JMenuItem FileOpenFile, FileOpenFolder;
	static JFrame FramePicture = new JFrame("Image Processor");
	static JSlider s;
	static ArrayList<File> arrFiles = new ArrayList<File>();
	static JButton btnSave, btnSaveToLocation, btnForwardImg, btnBackImg;
	static JTextField txtSaveTo, txtR, txtG, txtB,txtThreshold;
	static JLabel lblSaveTo, imgLabel, lblFileNameTop,lbllblFileNameTop,lblHRPIFC;
	static int intCurrentFile = 0;
	static BufferedImage imgSource, imgWorking;
	static Image resizedImage;
	static JRadioButton rdoHRP, rdoIFC;
	static ButtonGroup rdoGroup;
	static JPanel rdoPanel;
	static JCheckBox chkTrackClicks; //used to track clicks and estimate colors
	static int[][] rgb = {{0,255},{0,255},{0,255}}; //these are actually reversed.
	static File csvfile;

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
		imgLabel.setSize(300,300);
		imgLabel.setLocation(100, 50);
		imgLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		FramePicture.add(imgLabel);


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

		lbllblFileNameTop = new JLabel("Filename: ");
		lbllblFileNameTop.setSize(70,30);
		lbllblFileNameTop.setLocation(130, 10);
		FramePicture.add(lbllblFileNameTop);

		lblFileNameTop = new JLabel();
		lblFileNameTop.setSize(200,30);
		lblFileNameTop.setLocation(200, 10);
		lblFileNameTop.setBorder(BorderFactory.createLineBorder(Color.black));
		FramePicture.add(lblFileNameTop);

		mFile.add(FileOpenFile);
		mFile.add(FileOpenFolder);

		mb.add(mFile);
		FramePicture.setJMenuBar(mb);




		//Not using slider anymore?
		//AddSlider();
		AddtxtSaveTo();
		AddForwardandBackButtons();

		//JAS
		AddExtraUI();
		AddHRPandIFCRadios();
		AddTrackClicksCheckBox();

		FramePicture.setSize(800,800);
		FramePicture.setLayout(null);
		FramePicture.setVisible(true);

		FileOpenFolder.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					  //start at the first one...
		            intCurrentFile = 0;
					arrFiles = fileManipulation.folderopener();
					ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
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
					intCurrentFile = 0;
					arrFiles.add(newfile);
					ChangeImageLabel(arrFiles.get(intCurrentFile).getName());
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




	protected static void exporttocsvfile() {
		// TODO Auto-generated method stub

	}

	public static void AddForwardandBackButtons()
	{
		btnForwardImg  = new JButton(">>");
		btnForwardImg.setBounds(50,100,50,30);
		btnForwardImg.setLocation(710,350);
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
		btnBackImg.setLocation(10,350);
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
		lblSaveTo.setLocation(25,810);
		FramePicture.add(lblSaveTo);

		txtSaveTo = new JTextField();
		txtSaveTo.setSize(250, 30);
		txtSaveTo.setLocation(25,840);
		FramePicture.add(txtSaveTo);

		btnSaveToLocation = new JButton("...");
		btnSaveToLocation.setBounds(50,100,50,30);
		btnSaveToLocation.setLocation(270,840);
		btnSaveToLocation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					txtSaveTo.setText(fileManipulation.FindDestinationToSaveTo());
			}
		});
		FramePicture.add(btnSaveToLocation);
		btnSave = new JButton("Save Image");
		btnSave.setBounds(25,100,95,30);
		btnSave.setLocation(25,860);
		btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {
						fileManipulation.SaveFile(txtSaveTo.getText(), arrFiles.get(intCurrentFile));
						exporttocsvfile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
		JLabel lblRGB = new JLabel("r,g,b:");
		lblRGB.setSize(200,30);
		lblRGB.setLocation(450,690);
		FramePicture.add(lblRGB);

		 txtR = new JTextField("255");
		txtR.setSize(40, 30);
		txtR.setLocation(450,720);
		FramePicture.add(txtR);

		 txtG = new JTextField("255");
		txtG.setSize(40, 30);
		txtG.setLocation(490,720);
		FramePicture.add(txtG);

		 txtB = new JTextField("255");
		txtB.setSize(40, 30);
		txtB.setLocation(530,720);
		FramePicture.add(txtB);

		 txtThreshold = new JTextField("20");
		 txtThreshold.setSize(40, 30);
		 txtThreshold.setLocation(630,760);
			FramePicture.add(txtThreshold);

		JButton btnProcessImage = new JButton("Estimate Values");
		btnProcessImage.setBounds(50,100,200,30);
		//btnProcessImage.setBorder(BorderFactory.createLineBorder(Color.black));
		btnProcessImage.setLocation(440,660);
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
		btnMakeWhite.setLocation(440,760);
		btnMakeWhite.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhite(imgSource,
								Integer.parseInt(txtThreshold.getText()),
								Integer.parseInt(txtR.getText()),
								Integer.parseInt(txtG.getText()),
								Integer.parseInt(txtB.getText()), DatatoSave));
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
		btnMakeWhiteUsingTrackedClicks.setLocation(440,790);
		btnMakeWhiteUsingTrackedClicks.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ProcessImage(imgSource);
				try {
					imgWorking = ImageManipulation.deepCopyImage(ImageManipulation.MakeIgnoredPixelsWhiteUsingRange(
							imgSource, rgb, DatatoSave));
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

		lblHRPIFC = new JLabel("Type:");
		lblHRPIFC.setSize(70,40);
		lblHRPIFC.setLocation(25, 650);
		FramePicture.add(lblHRPIFC);

		// for IFC (green): r = 0, g = 254, b = 0;
		// for HRP: r = 171, g = 171, b = 141;
		// both have range +/- 20 for "make white". The button should "make white" as well.

		rdoHRP = new JRadioButton("HRP");
		rdoHRP.setBounds(25,20,200,30);
		rdoHRP.setLocation(25,670);
		rdoHRP.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           	txtR.setText("140");
	           	txtG.setText("140");
	           	txtB.setText("140");
	        }
	    });
		rdoIFC = new JRadioButton("IFC (green)");
		rdoIFC.setBounds(25,20,200,30);
		rdoIFC.setLocation(25,690);

		rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoHRP);
		rdoGroup.add(rdoIFC);

		FramePicture.getContentPane().add(rdoIFC);
		FramePicture.getContentPane().add(rdoHRP);

		rdoIFC.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				// set rgb to 0,254,0
			}


		});

		rdoHRP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				// set rgb to 171,171,141
			}


		});
	}

	public static void AddTrackClicksCheckBox()
	{
		chkTrackClicks = new JCheckBox("Track Clicks!");
		chkTrackClicks.setBounds(50,50, 150,50);
		chkTrackClicks.setLocation(300,680);
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
