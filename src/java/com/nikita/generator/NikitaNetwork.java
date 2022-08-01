/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author rkrzmail
 */
public class NikitaNetwork {
    public static void pushUDP(String target, String messaage){
        String messageStr= target+ "://" + messaage;
        int server_port = 5000; //port that I’m using
        try{
            DatagramSocket s = new DatagramSocket();
            InetAddress local = InetAddress.getByName("255.255.255.255");//my broadcast ip
            int msg_length=messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
            s.send(p);
            
        }catch(Exception e){
         
        }
    }
    public static void pushMessage(String target, String message){
        //android:// iphone
    }
    public static void sendChat(String target, String message){
        
    }
}
