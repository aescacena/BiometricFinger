/*-----------------------------------------------------------------------------+

			Filename			: FingerPrintEngine.java

			Project				: fingerprint-recog
			Package				: application

			Developed by		: Thomas DEVAUX & Estelle SENAY
			                      (2007) Concordia University

							-------------------------

	This program is free software. You can redistribute it and/or modify it 
 	under the terms of the GNU Lesser General Public License as published by 
	the Free Software Foundation. Either version 2.1 of the License, or (at your 
    option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT 
	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
	FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
    more details.

+-----------------------------------------------------------------------------*/
package application.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import application.core.Biometrics.CFingerPrint;
import application.core.FingerPrint.direction;
import application.gui.MainFrame;

public class FingerPrintEngine implements MainFrameListener
{
	//------------------------------------------------------------- TYPES --//

	// Thread that only compute fingerprint images
	private class computeThread extends Thread
	{
		public void run()
		{
			compute();
		}
	}

	//---------------------------------------------------------- CONSTANTS --//

	//---------------------------------------------------------- VARIABLES --//	
	private MainFrame mainWindow;				// Main frame
	private String filename1, filename2;			// Filename of the current fingerprint
	private FingerPrint fingerprint1, fingerprint2;			// Current fingerprint
	private computeThread thread;				// Computation thread

	//------------------------------------------------------- CONSTRUCTORS --//	

	//------------------------------------------------------------ METHODS --//	

	/**
	 * Construct the fingerprint engine from a given window
	 * @param the main window
	 */
	public FingerPrintEngine(MainFrame mainWindow) 
	{
		this.mainWindow = mainWindow;
		thread = new computeThread();
	}	

	//---------------------------------------------------- PRIVATE METHODS --//

	/**
	 * Compute all the fingerprint data and synchronize with the GUI
	 */
	private void compute()
	{
		// Disable buttons
		mainWindow.setEnableButtons(false);

		// Create binaryPicture
//		fingerprint1 = new FingerPrint(filename1);
		//		fingerprint2 = new FingerPrint(filename2);

		BufferedImage buffer;


		/***********************************************************************************/
		/*********************************** First image ***********************************/
		/***********************************************************************************/
		File dir = new File(new java.io.File("").getAbsolutePath()+"\\data");
		String[] ficheros = dir.list();
		/***********************************************************************************/
		/***********************************************************************************/
		/***********************************************************************************/


		/************************************************************************************/
		/************************Busqueda de la huella***************************************/
		/***********************************************************************************/
		long res = System.currentTimeMillis();
		for (String fichero : ficheros) {
			fingerprint1 = new FingerPrint(filename1);
			fingerprint2 = new FingerPrint(new java.io.File("").getAbsolutePath()+"\\data\\"+fichero);
			filename2 = fichero;
			
			CFingerPrint m_finger1 = new CFingerPrint(fingerprint1.getOriginalImage().getWidth(), fingerprint1.getOriginalImage().getHeight());
			CFingerPrint m_finger2 = new CFingerPrint(fingerprint2.getOriginalImage().getWidth(), fingerprint2.getOriginalImage().getWidth());
			BufferedImage m_bimage1 = new BufferedImage(m_finger1.FP_IMAGE_WIDTH ,m_finger1.FP_IMAGE_HEIGHT,BufferedImage.TYPE_INT_RGB );
			BufferedImage m_bimage2 = new BufferedImage(m_finger2.FP_IMAGE_WIDTH ,m_finger2.FP_IMAGE_HEIGHT,BufferedImage.TYPE_INT_RGB );
			double finger1[] = new double[m_finger1.FP_TEMPLATE_MAX_SIZE];
			double finger2[] = new double[m_finger2.FP_TEMPLATE_MAX_SIZE];

			try{        
				//picture1
				//Set picture new
				fingerprint1.setColors(Color.black, Color.green);
//				fingerprint1.binarizeMean();
				fingerprint1.binarizeLocalMean();
				fingerprint1.addBorders(1);
				fingerprint1.removeNoise();
				fingerprint1.removeNoise();
				fingerprint1.removeNoise();
				m_bimage1 = fingerprint1.toBufferedImage();
				//Send image for skeletinization
				m_finger1.setFingerPrintImage(m_bimage1) ;
				finger1=m_finger1.getFingerPrintTemplate();
				//See what skeletinized image looks like
				m_bimage1 = m_finger1.getFingerPrintImageDetail();

				//picture2
				//Set picture new
				fingerprint2.setColors(Color.black, Color.green);
//				fingerprint2.binarizeMean();
				fingerprint2.binarizeLocalMean();
				fingerprint2.addBorders(1);
				fingerprint2.removeNoise();
				fingerprint2.removeNoise();
				fingerprint2.removeNoise();
				m_bimage2 = fingerprint2.toBufferedImage();
				//Send image for skeletinization
				m_finger2.setFingerPrintImage(m_bimage2) ;
				finger2=m_finger2.getFingerPrintTemplate();
				//See what skeletinized image looks like
				m_bimage2 = m_finger2.getFingerPrintImageDetail();

//				JOptionPane.showMessageDialog (null,Double.toString(m_finger1.Match(finger1 , finger2,65,false)),"Match %",JOptionPane.PLAIN_MESSAGE);
				int porcentaje = m_finger1.Match(finger1 , finger2,65,false);
				if(porcentaje > 65){
					res=(System.currentTimeMillis()-res)/1000;
					JOptionPane.showMessageDialog (null, "Time: "+Long.toString(res)+" - Accuracy: "+porcentaje,"Time to find",JOptionPane.PLAIN_MESSAGE);
					break;
				}

			}catch (Exception ex){
				JOptionPane.showMessageDialog (null,ex.getMessage(),"Error",JOptionPane.PLAIN_MESSAGE);
			} 
		}

		/************************************************************************************/
		/************************************************************************************/
		/***********************************************************************************/


		/***********************************************************************************/
		/*********************************** First image ***********************************/
		/***********************************************************************************/
		
		fingerprint1 = new FingerPrint(filename1);
		
		// Print original image
		mainWindow.setIsWorking(0, true);
		buffer = fingerprint1.getOriginalImage();
		mainWindow.setImage(0, buffer);
		mainWindow.setIsWorking(0, false);

		// Print binary local result
		mainWindow.setIsWorking(1, true);
		fingerprint1.setColors(Color.black, Color.green);
		fingerprint1.binarizeLocalMean();
		fingerprint1.addBorders(1);
		fingerprint1.removeNoise();
		fingerprint1.removeNoise();
		fingerprint1.removeNoise();
		buffer = fingerprint1.toBufferedImage();
		mainWindow.setIsWorking(1, false);
		mainWindow.setImage(1, buffer);

		// Skeletonization
		mainWindow.setIsWorking(2, true);
		fingerprint1.skeletonize();
		mainWindow.setIsWorking(2, false);
		mainWindow.setImage(2, fingerprint1.toBufferedImage());

		// Direction, Core and Minutiaes
		mainWindow.setIsWorking(3, true);
		direction [][] dirMatrix = fingerprint1.getDirections();
		buffer = fingerprint1.directionToBufferedImage(dirMatrix);
		Point core1 = fingerprint1.getCore(dirMatrix);
		mainWindow.setImage(3, buffer);
		int coreRadius1 = buffer.getWidth() / 4;
		mainWindow.setCore(3, core1, coreRadius1);
		ArrayList<Point> intersections1 = fingerprint1.getMinutiaeIntersections(core1, coreRadius1);
		ArrayList<Point> endPoints1 = fingerprint1.getMinutiaeEndpoints(core1, coreRadius1);

		// Add intersections
		Graphics g1 = buffer.getGraphics();
		g1.setColor(Color.magenta);
		for (Point point : intersections1)
		{
			g1.fillOval(point.x-2, point.y-2, 5, 5);
		}

		g1.setColor(Color.blue);
		for (Point point : endPoints1)
		{
			g1.fillOval(point.x-2, point.y-2, 5,5);
		}

		// Add to buffer
		mainWindow.setImage(3,buffer);
		mainWindow.setIsWorking(3, false);		

		// Enable buttons
		mainWindow.setEnableButtons(true);


		/***********************************************************************************/
		/*********************************** Second image ***********************************/
		/***********************************************************************************/

		fingerprint2 = new FingerPrint(new java.io.File("").getAbsolutePath()+"\\data\\"+filename2);
		
		// Print original image
		mainWindow.setIsWorking(4, true);
		buffer = fingerprint2.getOriginalImage();
		mainWindow.setImage(4, buffer);
		mainWindow.setIsWorking(4, false);

		// Print binary local result
		mainWindow.setIsWorking(5, true);
		fingerprint2.setColors(Color.black, Color.green);
		fingerprint2.binarizeLocalMean();
		fingerprint2.addBorders(1);
		fingerprint2.removeNoise();
		fingerprint2.removeNoise();
		fingerprint2.removeNoise();
		buffer = fingerprint2.toBufferedImage();
		mainWindow.setIsWorking(5, false);
		mainWindow.setImage(5, buffer);

		// Skeletonization
		mainWindow.setIsWorking(6, true);
		fingerprint2.skeletonize();
		mainWindow.setIsWorking(6, false);
		mainWindow.setImage(6, fingerprint2.toBufferedImage());

		// Direction, Core and Minutiaes
		mainWindow.setIsWorking(7, true);
		direction [][] dirMatrix2 = fingerprint2.getDirections();
		buffer = fingerprint2.directionToBufferedImage(dirMatrix2);
		Point core2 = fingerprint2.getCore(dirMatrix2);
		mainWindow.setImage(7, buffer);
		int coreRadius2 = buffer.getWidth() / 4;
		mainWindow.setCore(7, core2, coreRadius2);
		ArrayList<Point> intersections2 = fingerprint2.getMinutiaeIntersections(core2, coreRadius2);
		ArrayList<Point> endPoints2 = fingerprint2.getMinutiaeEndpoints(core2, coreRadius2);

		// Add intersections
		Graphics g2 = buffer.getGraphics();
		g2.setColor(Color.magenta);
		for (Point point : intersections2)
		{
			g2.fillOval(point.x-2, point.y-2, 5, 5);
		}

		g2.setColor(Color.blue);
		for (Point point : endPoints2)
		{
			g2.fillOval(point.x-2, point.y-2, 5,5);
		}

		// Add to buffer
		mainWindow.setImage(7,buffer);
		mainWindow.setIsWorking(7, false);		

		// Enable buttons
		mainWindow.setEnableButtons(true);

	}

	@Override
	public void startExtraction() 
	{	
		thread = new computeThread();
		thread.start();
	}

	//	@Override
	//	public void newPictureFile(String filename) 
	//	{
	//		this.filename = filename;
	//		mainWindow.init();
	//	}

	@Override
	public void newPictureFiles(String filename1, String filename2) {
		// TODO Auto-generated method stub
		this.filename1 = filename1;
		mainWindow.init();
	}

}
