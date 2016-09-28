package application.core;
/*-----------------------------------------------------------------------------+

			Filename			: Application.java

			Project				: fingerprint-recog
			Package				: application.core

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



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.UIManager;

import application.DAO.DAOUser;
import application.DAO.models.User;
import application.gui.MainFrame;

public class Application 
{
	//---------------------------------------------------------- CONSTANTS --//

	//---------------------------------------------------------- VARIABLES --//	
	private static MainFrame mainWindow;				// Main window
	private static FingerPrintEngine fingerPrintEngine;	// Fingerprint engine

	//------------------------------------------------------- CONSTRUCTORS --//	

	//------------------------------------------------------------ METHODS --//
	/**
	 * Launch the application
	 * 
	 * @param args application arguments
	 */
	public static void main(String[] args){

		saveImagesInBD();
		
		// Set style
		//		setStyle();

		// Create the main frame
		mainWindow = new MainFrame();

		// Create objects
		fingerPrintEngine = new FingerPrintEngine(mainWindow);
		mainWindow.addMainFrameListener(fingerPrintEngine);	

		// Set full screen
		mainWindow.setExtendedState(mainWindow.getExtendedState()|JFrame.MAXIMIZED_BOTH);

		// Show the window
		mainWindow.setVisible(true);
	}

	//---------------------------------------------------- PRIVATE METHODS --//
	/**
	 * Set the style of the application
	 */
	private static void setStyle()
	{	
		try 
		{	    	
			UIManager.setLookAndFeel( "de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel"  );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void saveImagesInBD(){
		System.out.println("Maven + Hibernate + MySQL");
		DAOUser userDAO = new DAOUser();

		File dir = new File(new java.io.File("").getAbsolutePath()+"\\data");
		String[] ficheros = dir.list();

		for (String fichero : ficheros) {
			FingerPrint fingerprint2 = new FingerPrint(new java.io.File("").getAbsolutePath()+"\\data\\"+fichero);

//			BufferedImage originalImage = fingerprint2.getOriginalImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream os = new ObjectOutputStream(baos);
				os.writeObject(new File(new java.io.File("").getAbsolutePath()+"\\data\\"+fichero));
				baos.flush();
				os.close();
				byte[] imageInByte = baos.toByteArray();

				User user = new User();
				user.setUsername(fichero);
				user.setImage(imageInByte);
				if(imageInByte.length >= 100000)
					System.out.println("------------------------------------------------> "+imageInByte.length);
				userDAO.save(user);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
