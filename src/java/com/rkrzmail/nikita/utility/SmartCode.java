/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.utility;

/**
 *
 * @author rkrzmail
 */
public class SmartCode {
    
    public class BarcodeCode{
        public String NOMOR_KIRIMAN = "";
        public String TIPE_PENGIRIM = "";
        public String NOMOR_AC_PENGRIRIM_KODE_MEMBER = "";
        public String TIPE_PEMBAYARAN = "";
        public String NO_AC_PEMBAYAR = "";
        public String BAGIAN_INV = "";
        public String TANGGAL_KIRIM = "";
        public String TANGGAL_KOMITMENT = "";
        public String KODE_ORG_DES_ASAL_KIRIM = "";
        public String KODE_POS_TUJUAN_KIRIM = "";
        public String KODE_ROUTING = "";
        public String ALAMAT_PENERIMA = "";
        public String HP_PENERIMA_= "";
        public String KODE_PRODUK = "";
        public String BERAT = "";
        public String TIPE_DELIVERY = "";
        public String NOMOR_KOMBINASI_SERVICES = "";
        public String TOTAL_ONGKIR = "";
        public String DISC_ONGKIR = "";
        public String TOTAL_BIAYA_SERVICES = "";
        public String TOTAL_PENERUS = "";
        public String TOTAL_BBM = "";
        public String BIAYA_PUP_DEL_CARGO = "";
        public String TOTAL_COD_SOD = "";
        
        

        private StringBuilder sb ;
        private String barcode;
        private void preset(String barcode){
            this.barcode = barcode;             
        }
        private String code(String str, int fixlen){
            String ret = "";
            fixChar (str, fixlen);            
            if (barcode!=null) {
                if (barcode.length()>fixlen) {
                    ret = ret.substring(0, fixlen);
                    barcode = barcode.substring(fixlen);                
                }else{
                    ret = barcode;
                    barcode = "";
                }
            }else{
                return str;
            }
            return ret;        
        }  
        private String getBarcode(){
            return sb.toString();
        }
        private String fixChar(String str, int fixlen){
            StringBuilder sb = new StringBuilder(str);
            for (int i = str.length(); i < fixlen; i++) {
                sb.append(" ");            
            }
            if (str.length()>fixlen) {
                str = str.substring(0, fixlen);
            }
            return  str;
        }
    }
     
    private String codeBarcode(String code, BarcodeCode barcode){
        barcode.preset(code);
        barcode.NOMOR_KIRIMAN                   = barcode.code ( barcode.NOMOR_KIRIMAN, 12);
        barcode.TIPE_PENGIRIM                   = barcode.code ( barcode.TIPE_PENGIRIM, 1);
        barcode.NOMOR_AC_PENGRIRIM_KODE_MEMBER  = barcode.code ( barcode.NOMOR_AC_PENGRIRIM_KODE_MEMBER, 13);
        barcode.TIPE_PEMBAYARAN                 = barcode.code ( barcode.TIPE_PEMBAYARAN, 2);
        barcode.NO_AC_PEMBAYAR                  = barcode.code ( barcode.NO_AC_PEMBAYAR, 13);
        barcode.BAGIAN_INV                      = barcode.code ( barcode.BAGIAN_INV, 3);
        barcode.TANGGAL_KIRIM                   = barcode.code ( barcode.TANGGAL_KIRIM, 3);
        barcode.TANGGAL_KOMITMENT               = barcode.code ( barcode.TANGGAL_KOMITMENT, 3);
        barcode.KODE_ORG_DES_ASAL_KIRIM         = barcode.code ( barcode.KODE_ORG_DES_ASAL_KIRIM, 3);
        barcode.KODE_POS_TUJUAN_KIRIM           = barcode.code ( barcode.KODE_POS_TUJUAN_KIRIM, 5);
        
        barcode.HP_PENERIMA_                    = barcode.code ( barcode.HP_PENERIMA_, 15);
        barcode.KODE_PRODUK                     = barcode.code ( barcode.KODE_PRODUK, 1);
        barcode.BERAT                           = barcode.code ( barcode.BERAT, 4);
        barcode.TIPE_DELIVERY                   = barcode.code ( barcode.TIPE_DELIVERY, 1);
        barcode.NOMOR_KOMBINASI_SERVICES        = barcode.code ( barcode.NOMOR_KOMBINASI_SERVICES, 3);
        barcode.TOTAL_ONGKIR                    = barcode.code ( barcode.TOTAL_ONGKIR, 7);
        barcode.DISC_ONGKIR                     = barcode.code ( barcode.DISC_ONGKIR, 7);
        barcode.TOTAL_BIAYA_SERVICES            = barcode.code ( barcode.TOTAL_BIAYA_SERVICES, 7);
        barcode.TOTAL_PENERUS                   = barcode.code ( barcode.TOTAL_PENERUS, 7);
        barcode.TOTAL_BBM                       = barcode.code ( barcode.TOTAL_BBM, 6);
        barcode.BIAYA_PUP_DEL_CARGO             = barcode.code ( barcode.BIAYA_PUP_DEL_CARGO, 7);
        barcode.TOTAL_COD_SOD                   = barcode.code ( barcode.TOTAL_COD_SOD, 7);
        
        barcode.KODE_ROUTING                    = barcode.code ( barcode.KODE_ROUTING, 9);
        barcode.ALAMAT_PENERIMA                 = barcode.code ( barcode.ALAMAT_PENERIMA.toUpperCase(), 60); 
        
        return barcode.getBarcode();
    }
    public BarcodeCode newBarcodeCode(){
        return new BarcodeCode();
    }
    public String encodeBarcode(BarcodeCode barcode){
        return codeBarcode(null, barcode);
    }
    public BarcodeCode decodeBarcode(String code){
        BarcodeCode barcode = new BarcodeCode();
        codeBarcode(code, barcode);         
        return barcode;
    }
}
