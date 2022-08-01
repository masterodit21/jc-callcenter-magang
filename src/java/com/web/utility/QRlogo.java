/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author rkrzmail
 */
public class QRlogo {
    public static void main(String[] args) {
        try {
            createQRlogo("2121212114232131211ATKJ341251531043262563153112122264200000121212000000061302321345", new FileInputStream("D:\\x.png"), new FileOutputStream("D:\\qr.png"));
        } catch (FileNotFoundException ex) {
            
        }
    }
    public static void createQRlogo(String barcode, InputStream logo, OutputStream baos ){
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
         

        try {
            // Create a qr code with the url as content and a size of 250x250 px
            bitMatrix = writer.encode(barcode, BarcodeFormat.QR_CODE, 360, 360, hints);

            MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);

            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

            // Load logo image
            BufferedImage logoImage = ImageIO.read(logo);//request.getSession().getServletContext().getResourceAsStream("/images/logo.png")

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
            int deltaWidth = qrImage.getWidth() - logoImage.getWidth();

            // Initialize combined image
            //BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            BufferedImage combined = new BufferedImage(400, 500, BufferedImage.TYPE_INT_RGB);
           
            Graphics2D g = (Graphics2D) combined.getGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, combined.getWidth(), combined.getHeight());
            // Write QR code to new image at position 0/0
            g.setClip(0, 0, combined.getWidth(), combined.getHeight());
            g.drawImage(qrImage, 15, 120, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

          
            
            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            //g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

            Font 
            f = new Font("Dialog", Font.PLAIN|Font.BOLD, 20);
            g.setColor(Color.BLACK);
            g.setFont(f);
            g.drawString("JUM", 170, 40);
            
            g.setColor(Color.BLACK);
            g.setFont(f);
            g.drawString("12/04", 60, 40);
            
            g.setColor(Color.BLACK);
            g.setFont(f);
            g.drawString("0001", 300, 40);
            
            
            f = new Font("Dialog", Font.PLAIN|Font.BOLD, 40);           
            g.setColor(Color.BLUE);
            g.setFont(f);
            g.drawString("2-A 01 CGKA", 70, 90);
            
            f = new Font("Dialog", Font.PLAIN|Font.BOLD, 20);           
            g.setColor(Color.BLUE);
            g.setFont(f);
            g.drawString("1234 5678 9012 12", 110, 130);
            
            // Write combined image as PNG to OutputStream
            ImageIO.write(combined, "png", baos);
        } catch (WriterException e) {
            //LOG.error("WriterException occured", e);
        } catch (IOException e) {
            //LOG.error("IOException occured", e);
        } catch (Exception ex) {

        }
    }
    
     
}
