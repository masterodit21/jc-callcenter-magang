/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.utility;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

/**
 *
 * @author rkrzmail
 */
public class RSA {
    public static void main(String [] args) throws Exception {
         SecureRandom gen = new SecureRandom();
          System.out.println(gen.getProvider());
    System.out.println(gen.getAlgorithm());
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
        //PublicKey pubKey = keyPair.getPublic();
        //PrivateKey privateKey = keyPair.getPrivate();
        System.out.println( Utility.encodeBase64(new String(keyPair.getPublic().getEncoded())) );
        System.out.println( Utility.encodeBase64(new String(keyPair.getPrivate().getEncoded())) );
        
        System.out.println(Utility.bytesToHex(keyPair.getPublic().getEncoded()));
        System.out.println(Utility.bytesToHex(keyPair.getPrivate().getEncoded()));
        PublicKey  pubKey = getPublic(Utility.hexToBytes("30820122300d06092a864886f70d01010105000382010f003082010a0282010100c11adb65efd9d875f8bb0e46d3ee2bf424c5e4d187d870e4cef3ddba991ebf58a9710b97e66514195ef9fd5f08bed5fbfb36d120ca0c2a99c62a1f255fd8ea7957faee9546f25438922dceafc4a39d57b940be586c8946e7ddba10c47b448372024f6d0cc04e21fd8da5f4000df4e871eb6c8a08d923d0dec2e0dcbbc4aefcaac91bce747ee9b2ea2d759713ebf082d1c2f571bf0af75cc6f0938e9ac733db68dbd0a70824e920167ff770b4b1385ad4a0e4ebc36828419a518cfc809d142db8ee6b131de12094d0a21318399536e800c09c85a888f66d5c914eb6a812a182955efcc3f1b0adb849cd404930fde308cb4cb68b3ab4586839c43488980357ad370203010001"));
        PrivateKey privateKey = getPrivate(Utility.hexToBytes("308204bc020100300d06092a864886f70d0101010500048204a6308204a20201000282010100c11adb65efd9d875f8bb0e46d3ee2bf424c5e4d187d870e4cef3ddba991ebf58a9710b97e66514195ef9fd5f08bed5fbfb36d120ca0c2a99c62a1f255fd8ea7957faee9546f25438922dceafc4a39d57b940be586c8946e7ddba10c47b448372024f6d0cc04e21fd8da5f4000df4e871eb6c8a08d923d0dec2e0dcbbc4aefcaac91bce747ee9b2ea2d759713ebf082d1c2f571bf0af75cc6f0938e9ac733db68dbd0a70824e920167ff770b4b1385ad4a0e4ebc36828419a518cfc809d142db8ee6b131de12094d0a21318399536e800c09c85a888f66d5c914eb6a812a182955efcc3f1b0adb849cd404930fde308cb4cb68b3ab4586839c43488980357ad3702030100010282010013f060fd1a9204ded18b07035b38b9832114e9f694af4da0e2999da879cd09f6eeb004816fc1ba1c908901978180aaf9b4cbc93638869f1377b9bbb3913d05a0f7ebaa1cf1d16dced591334944e59c0685d3609422c8ddf06d1d9c439d72ca9bc80f49744a9b06b97699cf22fe4dee7e6ccfad45129eb71f7941dca8eabb22cd957b687c2667471a4d4b70e03445cd16d0d5a52a2df6c9f8300c11bea492d9e7388b26d1ba28e6f96282b7bf7fac51e23cb9fb0f4930dd39248d0bf3926b9f333e672aaff44e2f18b98e6b6d8b6d733042dc7df688b3955e127e675814c29feacba40c3a384705976cb4c7f74f074c12372d1e7a2374d228d22ec186909c8ab102818100f812e861388ddd0d3e3301659f1568b5654c6e0aff9178592c08ccfb573187d9acb1759ec5e01e50ac89f1b6980963c2402033562ed0ea962169ce1caf0c2a846f821c06dc4e8e1521082f47f7565bcc8c357320f01c1c0bc94fbc1a443ffe4e0bc206aded856386639514c5aa08584d752276a3a11226266e4a8e18392d254f02818100c7465642879005c5c4685efdbd2cc3c20776ed3d0ff05ae09ef769f1372184c635f4819dfe94bf8eea4fb6abccffd84b5d9ec8cc1801b6a6e7fcb9ec990e6b95bc4b2b8e735189c800565847e04527656ba50bae6c8c75a5603562a541d9e7f24c6d056f79b817b011ee849439026867a04b927b1547efebc07609e255574f990281803e028beff17114b680bbc337a1c27a400da84341a08cbe669be93f43fc91b701a4775f8d66ad1526d95a49ff7895b048183b7459b0ae2c5586d8a9dae5aec6d9fffda7ffd70613e21835b18bedb9ba8cb0e79375c24f647b55c106a6123564de217648b125545882795604b13c4585bd26f08c736e89d8db766a5086a194356502818077da651d449da9eb25d34476c665fb23630bf36a4b80db97c7e4b512807f2775739428b5571e2e0c8dfefdb65ce8f0357c0c7966cf75905ed89ed750674a16090c2406017a786b2f208cce3204c0730e7becdb96bff0c6fd7d5cfe84b8938ffeda5aa3ef453d24fa1ffede3cac0137bc43dbd031393e781dca36f480eda542d902818042686545095f5c5bb41604abc67f946ef2114aefe4c35c0a4522808effd8e733f7b5929312686ddba31aa2624efb41381026481db8310aedb59b921892bdfdc73d3cec6e2ac9c5aebe3ddcf2db387ec90450c05a58515b02acf4bca0c81b0ff00e9a01a385adbec230d453376497e85246c0ed70ecacc764c433c9861ec668de"));
        System.out.println(Utility.bytesToHex(pubKey.getEncoded()));
        System.out.println(Utility.bytesToHex(privateKey.getEncoded()));
        
         
        System.out.println( Utility.encodeBase64(new String(pubKey.getEncoded())) );
        System.out.println( Utility.encodeBase64(new String(privateKey.getEncoded())) );
        
        
        
        // encrypt the message
        byte [] encrypted = encrypt(privateKey, "This is a secret message");     
        System.out.println(new String(encrypted));  // <<encrypted message>>
        //System.out.println(Utility.bytesToHex(encrypted));
        // decrypt the message
        byte[] secret = decrypt(pubKey, encrypted);                                 
        System.out.println(new String(secret));     // This is a secret message
        //System.out.println(Utility.bytesToHex(secret));
    }
    public static PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return getPrivate(keyBytes);
    }
    public static PrivateKey getPrivate(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public static PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return getPublic(keyBytes);
    }
    public static PublicKey getPublic(byte[] keyBytes ) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {            
        return buildKeyPair(2048);
    }
    public static KeyPair buildKeyPair(int keySize) throws NoSuchAlgorithmException {        
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }
    public static byte[] encrypt(byte[] privateKey, String message) throws Exception {
        return encrypt(getPrivate(privateKey), message);
    }
    public static byte[] encrypt(PrivateKey privateKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(message.getBytes());  
    }    
    public static byte[] decrypt(byte[] publicKey, byte [] encrypted) throws Exception {
        return  decrypt(getPublic(publicKey), encrypted);
    }        
    public static byte[] decrypt(PublicKey publicKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encrypted);
    }
}
