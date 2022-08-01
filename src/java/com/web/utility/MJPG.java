/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 *
 * @author rkrzmail
 */
public class MJPG {
    private final List<byte[]> imageByteList;
	private int currentIndex;
       
    /**
     * Constructor
     * @see HttpServlet#HttpServlet()
     */
    public MJPG() {
        super();
        
        // set the index
        currentIndex = 0;
        
        // load images from the user's home directory into the list of image bytes
        String[] names = {"bap32.png", "4112682_20150906044111.jpg", "bap11.png", "bap12.png", "s.png"};
        imageByteList = new ArrayList<byte[]>(0);
        
        for(String name : names) {
        	try {
        		File image = new File( "D:\\"  + name  );
        		BufferedImage originalImage = ImageIO.read(image);
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	ImageIO.write( originalImage, "jpg", baos );
            	baos.flush();
            	imageByteList.add(baos.toByteArray());
            	baos.close();
        	} catch (Exception ex) {
            	System.err.println("There was a problem loading the images.");
            }
        }

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
 
	public void doGet(OutputStream outputStream  ){
		
		// set the proper content type for MJPG
		//response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
		
		// get the output stream to write to
		//OutputStream outputStream = response.getOutputStream();
		 
		// loop over and send the images while the browser is present and listening, then return
		while(true) {
			try {
				// reset the current index if necessary or increment it
				if(currentIndex > 2) currentIndex = 0;
				else currentIndex++;

				// write the image and wrapper
				outputStream.write((
					"--BoundaryString\r\n" +
					"Content-type: image/jpeg\r\n" +
					"Content-Length: " +
					imageByteList.get(currentIndex).length +
					"\r\n\r\n").getBytes());
				outputStream.write(imageByteList.get(currentIndex));
				outputStream.write("\r\n\r\n".getBytes());
				outputStream.flush();			      

				// force sleep to not overwhelm the browser, simulate ~20 FPS
				TimeUnit.MILLISECONDS.sleep(50);
			}
			
			// If there is a problem with the connection (it likely closed), so return
			catch (Exception e) {
				return;
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	 
}
