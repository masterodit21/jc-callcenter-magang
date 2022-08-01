/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author rkrzmail
 */
public class UtilityCommand {
    public static void runCmd(String args) { 
        String s = null; 
        try {             
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec(args);
             
            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
 
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
             
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
             
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public static void runCmdAsyn(String args) { 
        try {
            final Process p = Runtime.getRuntime().exec(args);
            new Thread(new Runnable() {
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null; 

                    try {
                        while ((line = input.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            p.waitFor();        
        } catch (Exception e) { }
    }
    
    public static void apkSign(String args) { 
        //jarsigner -verbose -sigfile CERT -sigalg MD5withRSA -digestalg SHA1 -keystore D:\debug.keystore D:\NikitaMobile.apk AndroidDebugKey -storepass android
        
    }    
    
    public static void modifyZipFile(File zipFile,  File newZipFile,  String[] filesToAddOrOverwrite,  InputStream[] filesToAddOrOverwriteInputStreams,
                String[] filesToDelete) throws IOException {
        
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(newZipFile))) {
            out.setLevel(0);//nocompress 13k
            // add existing ZIP entry to output stream
            try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile))) {                
                ZipEntry entry = null;
                while ((entry = zin.getNextEntry()) != null) {
                    String name = entry.getName();

                    // check if the file should be deleted
                    if (filesToDelete != null) {
                        boolean ignoreFile = false;
                        for (String fileToDelete : filesToDelete) {
                            if (name.equalsIgnoreCase(fileToDelete)) {
                                ignoreFile = true;
                                break;
                            }
                        }
                        if (ignoreFile) {
                            continue;
                        }
                    }

                    // check if the file should be kept as it is
                    boolean keepFileUnchanged = true;
                    if (filesToAddOrOverwrite != null) {
                        for (String fileToAddOrOverwrite : filesToAddOrOverwrite) {
                            if (name.equalsIgnoreCase(fileToAddOrOverwrite)) {
                                keepFileUnchanged = false;
                            }
                        }
                    }

                    if (keepFileUnchanged) {
                        // copy the file as it is
                        out.putNextEntry(new ZipEntry(name));
                        IOUtils.copy(zin, out);
                    }
                }
            }

            // add the modified or added files to the zip file
            if (filesToAddOrOverwrite != null) {
                for (int i = 0; i < filesToAddOrOverwrite.length; i++) {
                    String fileToAddOrOverwrite = filesToAddOrOverwrite[i];
                    try (InputStream in = filesToAddOrOverwriteInputStreams[i]) {
                        out.putNextEntry(new ZipEntry(fileToAddOrOverwrite));
                        IOUtils.copy(in, out);
                        out.closeEntry();
                    }
                }
            }

        }
    }
    
    
}
