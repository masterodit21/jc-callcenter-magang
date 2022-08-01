package com.web.utility;

import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
 
import java.util.Calendar;
import java.util.List;


public class Z {
    public static final String TYPE_BARCODE        = "TYPE_BARCODE";
    public static final String NOMOR_KIRIMAN       = "NOMOR_KIRIMAN";
    public static final String TANGGAL_KOMITMENT   = "TANGGAL_KOMITMENT";
    public static final String ORIGIN              = "ORIGIN";
    public static final String ROUTING_CODE        = "ROUTING_CODE";
    public static final String PONSEL_PENERIMA     = "PONSEL_PENERIMA";
    public static final String TYPE_PENERIMA       = "TYPE_PENERIMA";
    public static final String GPS_PENERIMA        = "GPS_PENERIMA";
    public static final String PRODUK              = "PRODUK";
    public static final String JUMLAH_PCS          = "JUMLAH_PCS";
    public static final String BERAT_CHARGES       = "BERAT_CHARGES";
    public static final String SERVICES_COMBO      = "SERVICES_COMBO";
    public static final String TYPE_ALAMAT         = "TYPE_ALAMAT";
    public static final String NOMOR_DOP           = "NOMOR_DOP";
    public static final String DGS_CLASS           = "DGS_CLASS";
    public static final String KODE_CENTRAL        = "KODE_CENTRAL";
    public static final String KODE_REGIONAL       = "KODE_REGIONAL";

    public static final String KODE_CENTRAL_DEST   = "KODE_CENTRAL_DEST"; //1 jun 2018
    public static final String KODE_REGIONAL_DEST  = "KODE_REGIONAL_DEST";//1 jun 2018

    public static final String TOTAL_COD           = "TOTAL_COD";

    public static final String ONGKIR_BBM_BERAT    = "ONGKIR_BBM_BERAT";
    public static final String BIAYA_SERVIS        = "BIAYA_SERVIS";
    public static final String BIAYA_CLEARANCE     = "BIAYA_CLEARANCE";
    public static final String BIAYA_ODA           = "BIAYA_ODA";
    public static final String TOTAL_COD_SOD       = "TOTAL_COD_SOD";
    public static final String PEMBAYARAN          = "PEMBAYARAN";//0:cash|1:saving[bayar lebihnya]|2:invoice[free]


    public static final String PROFILE              = "PROFILE";
    public static final String BARCODE_ENCODE       = "BARCODE_ENCODE";
    public static final String HASH                 = "HASH";
    public static final String VERSION              = "VERSION";

    public static final String BARCODE_NAME         = "BARCODE_NAME";//PROFILE|CON|REF|KIRIMAN
    public static final String BARCODE_NAME_PROFILE = "PROFILE";//PROFILE|CON|REF|KIRIMAN
    public static final String BARCODE_NAME_CON     = "CON";    //PROFILE|CON|REF|KIRIMAN
    public static final String BARCODE_NAME_REF     = "REF";    //PROFILE|CON|REF|KIRIMAN
    public static final String BARCODE_NAME_KIRIMAN = "KIRIMAN";//PROFILE|CON|REF|KIRIMAN


    public static final String CON_NUMBER                   = "CON_NUMBER";
    public static final String CON_ORIGIN                   = "CON_ORIGIN";
    public static final String CON_ROUTING_CODE             = "CON_ROUTING_CODE";
    public static final String CON_PRODUCT_CODE             = "CON_PRODUCT_CODE";
    public static final String CON_MAX_COM_DATE_SHIPMENT    = "CON_MAX_COM_DATE_SHIPMENT";
    public static final String CON_TOTAL_PCS                = "CON_TOTAL_PCS";
    public static final String CON_TOTAL_ACTUAL_WEIGHT      = "CON_TOTAL_ACTUAL_WEIGHT";
    public static final String CON_TOTAL_CHARGES_WEIGHT     = "CON_TOTAL_CHARGES_WEIGHT";
    public static final String CON_FORMAT_TYPE              = "CON_FORMAT_TYPE";
    public static final String CON_FORMAT_REMARK            = "CON_FORMAT_REMARK";
    public static final String CON_FORMAT_NUMBER            = "CON_FORMAT_NUMBER";
    public static final String CON_SPLIT_TYPE               = "CON_SPLIT_TYPE";
    public static final String CON_DELIVERY_DAY_TYPE        = "CON_DELIVERY_DAY_TYPE";
    public static final String CON_CENTRAL_KODE             = "CON_CENTRAL_KODE";
    public static final String CON_REGIONAL_KODE            = "CON_REGIONAL_KODE";
    public static final String CON_TYPE                     = "CON_TYPE";
    public static final String CON_DATE                     = "CON_DATE";
    public static final String CON_CENTRAL_KODE_DEST        = "CON_CENTRAL_KODE_DEST";
    public static final String CON_REGIONAL_KODE_DEST       = "CON_REGIONAL_KODE_DEST";
    public static final String CON_ASP_DEST                 = "CON_ASP_DEST";


    //Z VERSION 1.2 ref 2
    public static void main(String[] args) {
        //Nson.setDefaultNullStringAsEmptyString(true);
        // System.out.println(Z.decodeBarcode("1212121212131232421431251531513515135136526246222121212121312324214312515315135151351365262462").toJson());
        //System.out.println(Z.decodeBarcode("2212121212131232421431251531513515135136526246222121212121312324214312515315135151351365262462").toJson());
        //System.out.println(Z.decodeBarcode("12121212121312324214312515315013515135136526246222121212100002131232476819").toJson());


//        System.err.println(Z.encodeBarcode(Nson.readJson("{\"ROUTING_CODE\":\"JKTA1234\",\"NOMOR_KIRIMAN\":\"21212121213123\",\"PRODUK\":\"6\",\"PONSEL_PENERIMA\":\"\",\"TYPE_ALAMAT\":\"1\",\"TOTAL_COD\":\"2131232\",\"TANGGAL_KOMITMENT\":\"242\",\"JUMLAH_PCS\":\"2\",\"TYPE_BARCODE\":\"1\",\"BERAT_CHARGES\":\"22121\",\"GPS_PENERIMA\":\"4\",\"SERVICES_COMBO\":\"212\",\"ORIGIN\":\"143\"}")));
//        System.out.println(Z.decodeBarcode("121212121213123242143JKTA1234013515135136526246222121212100002131232460184").toJson());
//        System.out.println(Z.decodeBarcode("12121212124232131221ATKJ34115153104326256315321212226412000012110642321348").toJson());
//
//
//        System.err.println(Z.encodeBarcode(Nson.readJson("{\"TOTAL_COD_SOD\":\"3513652\",\"BIAYA_SERVIS\":\"4214312\",\"PEMBAYARAN\":\"6\",\"TYPE_ALAMAT\":\"1\",\"TYPE_BARCODE\":\"2\",\"ONGKIR_BBM_BERAT\":\"2131232\",\"BIAYA_ODA\":\"35151\",\"ORIGIN\":\"143\",\"NOMOR_KIRIMAN\":\"21212121213123\",\"ROUTING_CODE\":\"12515315\",\"PRODUK\":\"6\",\"PONSEL_PENERIMA\":\"135151351365262\",\"TANGGAL_KOMITMENT\":\"242\",\"JUMLAH_PCS\":\"2\",\"BIAYA_CLEARANCE\":\"5153151\",\"BERAT_CHARGES\":\"22121\",\"GPS_PENERIMA\":\"4\",\"SERVICES_COMBO\":\"212\"}")));
//        System.out.println(Z.decodeBarcode("22121212121312324214312515315013515135136526246222121212100000000000421431251531513515135136526558011").toJson());
//        System.out.println(Z.decodeBarcode("002056121011301000C10A40007826000DKG25063513101000140600000020000000000000000000000000000.000000000.006121008937112").toJson());
//
//        System.out.println(comDate3("08/02"));
//        System.out.println(comDate4("071"));
//        System.out.println(getDayName("071"));
        
        Nson 
        nson = Z.decodeBarcode("002056121011301000C10A40007826000DKG25063513101000140600000020000000000000000000000000000.000000000.006121008937112")  ;
      System.out.println(nson.toJson());
    
    }

    public static  boolean isMyBarcode(Nson barcode){
        if (barcode.getData(BARCODE_NAME).asString().equalsIgnoreCase(BARCODE_NAME_KIRIMAN)){
            return true;
        }
        return false;
    }
    public static boolean isBarcodeCON(Nson barcode){
        if (barcode.getData(BARCODE_NAME).asString().equalsIgnoreCase(BARCODE_NAME_CON)){
            return true;
        }
        return false;
    }
    public static boolean isBarcodeREF(Nson barcode){
        if (barcode.getData(BARCODE_NAME).asString().equalsIgnoreCase(BARCODE_NAME_REF)){
            return true;
        }
        return false;
    }
    public static  boolean isMyProfile(Nson barcode){
        if (barcode.getData(BARCODE_NAME).asString().equalsIgnoreCase(BARCODE_NAME_PROFILE)){
            return true;
        }
        return false;
    }

    private static String chr0 (String str, int len){
        if (str.length()>len){
            return str.substring(0, len);
        }else if (str.length()<len){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < len-str.length(); i++) {
                stringBuilder.append("0");
            }
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
        return str;
    }
    public static String encodeBarcode(Nson barcode){
        String buffer = "";
        String strack = barcode.getData(NOMOR_KIRIMAN).asString();
        if (barcode.containsKey(CON_NUMBER) || (strack.startsWith("CON")&& strack.length() == 14)){
            buffer = encodeConA(barcode);
        }else{
            buffer = encodeBarcodeA(barcode);
        }
        StringBuilder builder = new StringBuilder();
        while ( buffer.length() >= 1 ) {
            String sub = buffer.substring(0, Math.min(9, buffer.length()) );
            buffer = buffer.substring(sub.length());
            for (int i = 0; i < sub.length(); i++) {
                builder.append(sub.substring(sub.length()-i-1, sub.length()-i));
            }
        }
        return builder.toString();
    }
    private static String encodeBarcodeA(Nson barcode){
        barcode.setNullStringAsEmptyString(true);
        barcode.setData(VERSION, "1");
        boolean binv = true;
        if (barcode.containsKey(ONGKIR_BBM_BERAT)||barcode.containsKey(BIAYA_ODA)||barcode.containsKey(TOTAL_COD_SOD)||barcode.containsKey(BIAYA_CLEARANCE)||barcode.containsKey(BIAYA_SERVIS)||barcode.containsKey(PEMBAYARAN)){
            binv = false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < barcoFormat.size(); i++) {
            if (binv) {
                stringBuilder.append(chr0(barcode.getData(barcoFormat.getData(i).getData(0).asString()).asString(), barcoFormat.getData(i).getData(1).asInteger()));
                if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(TOTAL_COD)){
                    int rnd = 1+barcode.getData(VERSION).asInteger();
                    stringBuilder.append(  uhash(stringBuilder.toString()).substring(rnd, rnd+6) );
                    return stringBuilder.toString();
                }
            }else if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(TOTAL_COD)){
                stringBuilder.append(chr0(barcode.getData(ONGKIR_BBM_BERAT).asString(), barcoFormat.getData(i).getData(1).asInteger()));
            }else{
                stringBuilder.append(chr0(barcode.getData(barcoFormat.getData(i).getData(0).asString()).asString(), barcoFormat.getData(i).getData(1).asInteger()));
            }
        }
        //no invoice
        int rnd = 1+barcode.getData(VERSION).asInteger();
        stringBuilder.append(  uhash(stringBuilder.toString()).substring(rnd, rnd+6) );
        return stringBuilder.toString();
    }
    private static final Nson barcoFormat = Nson.newArray();
    private static final Nson conFormat = Nson.newArray();
    static {
        barcoFormat.addData(Nson.newArray().addData(VERSION)          .addData(1));
        barcoFormat.addData(Nson.newArray().addData(TYPE_BARCODE)     .addData(1));
        barcoFormat.addData(Nson.newArray().addData(NOMOR_KIRIMAN)    .addData(14));
        barcoFormat.addData(Nson.newArray().addData(TANGGAL_KOMITMENT).addData(3));
        barcoFormat.addData(Nson.newArray().addData(ORIGIN)           .addData(3));
        barcoFormat.addData(Nson.newArray().addData(ROUTING_CODE)     .addData(8));
        barcoFormat.addData(Nson.newArray().addData(TYPE_PENERIMA)    .addData(1));//user|bukan
        barcoFormat.addData(Nson.newArray().addData(PONSEL_PENERIMA)  .addData(15));
        barcoFormat.addData(Nson.newArray().addData(GPS_PENERIMA)     .addData(1));
        barcoFormat.addData(Nson.newArray().addData(PRODUK)           .addData(1));
        barcoFormat.addData(Nson.newArray().addData(JUMLAH_PCS)       .addData(1));
        barcoFormat.addData(Nson.newArray().addData(BERAT_CHARGES)    .addData(5));
        barcoFormat.addData(Nson.newArray().addData(SERVICES_COMBO)   .addData(3));
        barcoFormat.addData(Nson.newArray().addData(TYPE_ALAMAT)      .addData(1));//hold|dex|del
        barcoFormat.addData(Nson.newArray().addData(NOMOR_DOP)        .addData(4));//DOP HALL
        barcoFormat.addData(Nson.newArray().addData(DGS_CLASS)        .addData(2));
        barcoFormat.addData(Nson.newArray().addData(KODE_CENTRAL)     .addData(3));
        barcoFormat.addData(Nson.newArray().addData(KODE_REGIONAL)    .addData(3));
        barcoFormat.addData(Nson.newArray().addData(KODE_CENTRAL_DEST)     .addData(3));//new 1/06/18
        barcoFormat.addData(Nson.newArray().addData(KODE_REGIONAL_DEST)    .addData(3));//new 1/06/18

        barcoFormat.addData(Nson.newArray().addData(TOTAL_COD)        .addData(7));
        //barcoFormat.addData(Nson.newArray().addData("ONGKIR_BBM_BERAT") .addData(7));

        barcoFormat.addData(Nson.newArray().addData(BIAYA_SERVIS)     .addData(7));
        barcoFormat.addData(Nson.newArray().addData(BIAYA_CLEARANCE)  .addData(7));
        barcoFormat.addData(Nson.newArray().addData(BIAYA_ODA)        .addData(4));
        barcoFormat.addData(Nson.newArray().addData(TOTAL_COD_SOD)    .addData(7));
        barcoFormat.addData(Nson.newArray().addData(PEMBAYARAN)       .addData(1));



        //CON
        conFormat.addData(Nson.newArray().addData(VERSION)                  .addData(1));
        conFormat.addData(Nson.newArray().addData(CON_NUMBER)               .addData(14));
        conFormat.addData(Nson.newArray().addData(CON_ORIGIN)               .addData(4));
        conFormat.addData(Nson.newArray().addData(CON_ROUTING_CODE)         .addData(8));
        conFormat.addData(Nson.newArray().addData(CON_PRODUCT_CODE)         .addData(1));
        conFormat.addData(Nson.newArray().addData(CON_MAX_COM_DATE_SHIPMENT).addData(3));
        conFormat.addData(Nson.newArray().addData(CON_TOTAL_PCS)            .addData(4));
        conFormat.addData(Nson.newArray().addData(CON_TOTAL_ACTUAL_WEIGHT)  .addData(7));
        conFormat.addData(Nson.newArray().addData(CON_TOTAL_CHARGES_WEIGHT) .addData(7));
        conFormat.addData(Nson.newArray().addData(CON_FORMAT_TYPE)          .addData(2));
        conFormat.addData(Nson.newArray().addData(CON_FORMAT_REMARK)        .addData(12));
        conFormat.addData(Nson.newArray().addData(CON_FORMAT_NUMBER)        .addData(12));
        conFormat.addData(Nson.newArray().addData(CON_SPLIT_TYPE)           .addData(2));
        conFormat.addData(Nson.newArray().addData(CON_DELIVERY_DAY_TYPE)    .addData(1));
        conFormat.addData(Nson.newArray().addData(CON_CENTRAL_KODE)         .addData(3));
        conFormat.addData(Nson.newArray().addData(CON_REGIONAL_KODE)        .addData(3));
        conFormat.addData(Nson.newArray().addData(CON_TYPE)                 .addData(1));
        conFormat.addData(Nson.newArray().addData(CON_DATE)                 .addData(3));
        conFormat.addData(Nson.newArray().addData(CON_CENTRAL_KODE_DEST)    .addData(3));//new 1/06/18
        conFormat.addData(Nson.newArray().addData(CON_REGIONAL_KODE_DEST)   .addData(3));//new 1/06/18
        conFormat.addData(Nson.newArray().addData(CON_ASP_DEST)             .addData(3));//new 1/06/18

    }
    public static Nson decodeBarcode(String barcode){
        if (barcode.startsWith("https://exaq.id/me?q=")){
            barcode = barcode.substring(21);
        }
        Nson result = Nson.newObject();
        result.setNullStringAsEmptyString(true);
        if (barcode.startsWith("{")){
            //profile
            result.asObject().putAll(Nson.readJson(barcode).asObject()) ;
            result.setData(NOMOR_KIRIMAN, barcode);
            result.setData(BARCODE_NAME, BARCODE_NAME_PROFILE);
            return result;
        }else if ( barcode.startsWith("(") && barcode.endsWith(")")){
            //profile encode
            result.asObject().putAll(Nson.readJson(barcode).asObject()) ;
            result.setData(NOMOR_KIRIMAN, barcode);
            result.setData(BARCODE_NAME, BARCODE_NAME_PROFILE);
            return result;
        }else if ( barcode.startsWith("REF") && barcode.length() == 14){
            result.setData(NOMOR_KIRIMAN, barcode);
            result.setData(BARCODE_NAME, BARCODE_NAME_REF);
            return result;
        }else{
            String buffer = barcode;
            StringBuilder builder = new StringBuilder();
            while ( buffer.length() >= 1 ) {
                String sub = buffer.substring(0, Math.min(9, buffer.length()) );
                buffer = buffer.substring(sub.length());
                for (int i = 0; i < sub.length(); i++) {
                    builder.append(sub.substring(sub.length()-i-1, sub.length()-i));
                }
            }
            String str = builder.toString();
            if (str.length()<=1){
            }else if (str.substring(1).startsWith("CON")){
                return decodeConA(str, barcode);
            }
            return decodeBarcodeA(str, barcode);
        }
    }
    public static String bname(String barcode){
        int inbr = Utility.getNumberOnly(barcode).length();
        if (barcode.length() == 14 && barcode.startsWith("CON") && barcode.endsWith("0") && inbr == 11){
            return BARCODE_NAME_CON;
        }else if (barcode.length() == 14 && barcode.startsWith("REF") && barcode.endsWith("0") && inbr == 11){
            return BARCODE_NAME_REF;
        }else if (barcode.length() == 14 && inbr == 14){
            return BARCODE_NAME_KIRIMAN;
        }else if (barcode.startsWith("{")&& barcode.endsWith("}")){
            return BARCODE_NAME_PROFILE;
        }else if (barcode.startsWith("(")&& barcode.endsWith(")")){
            return BARCODE_NAME_PROFILE;//encode
        }
        return "";
    }
    public static String getNumberConFromRef(String refnumber){
        if (bname(refnumber).equalsIgnoreCase(BARCODE_NAME_REF)){
            return "CON"+Utility.getNumberOnly(refnumber);
        }
        return "";
    }
    public static String getNumberRefFromCon(String connumber){
        if (bname(connumber).equalsIgnoreCase(BARCODE_NAME_CON)){
            return "REF"+Utility.getNumberOnly(connumber);
        }
        return "";
    }
    private static Nson decodeBarcodeA(String barcode, String encode){
        Nson result = Nson.newObject();
        result.setNullStringAsEmptyString(true);
        boolean binv = true;

        result.setData(NOMOR_KIRIMAN, barcode);
        result.setData(BARCODE_ENCODE, encode);
        result.setData(BARCODE_NAME, BARCODE_NAME_KIRIMAN);

        for (int i = 0; i < barcoFormat.size(); i++) {
            int v = barcoFormat.getData(i).getData(1).asInteger();
            if (v > barcode.length()){
                break;
            }else{
                String vs = barcode.substring(0, v);
                barcode = barcode.substring(v);

                if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(NOMOR_KIRIMAN)){
                    if (vs.startsWith("00000")) {
                        result.setData(barcoFormat.getData(i).getData(0).asString(), vs.substring(4));//CON
                    }else if (vs.startsWith("000")) {
                        result.setData(barcoFormat.getData(i).getData(0).asString(), vs.substring(3));
                    }else if (vs.startsWith("00")) {
                        result.setData(barcoFormat.getData(i).getData(0).asString(), vs.substring(2));
                    }else if (vs.startsWith("0")) {
                        result.setData(barcoFormat.getData(i).getData(0).asString(), vs.substring(1));
                    }else{
                        result.setData(barcoFormat.getData(i).getData(0).asString(), vs);
                    }
                }else{
                    result.setData(barcoFormat.getData(i).getData(0).asString(), vs);
                }
                if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(TOTAL_COD)){
                    if (barcode.length() <= 6) {
                        result.setData(HASH, barcode); //uhash6
                        break;
                    }else{
                        result.removeData(TOTAL_COD);
                        result.setData(ONGKIR_BBM_BERAT, vs);
                    }
                }else if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(PEMBAYARAN)){
                    binv = false;
                    result.setData(HASH, barcode);
                    break;
                }

            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        result.setData(BARCODE_NAME, bname(result.getData(NOMOR_KIRIMAN).asString()));

        for (int i = 0; i < barcoFormat.size(); i++) {
            if (binv) {
                stringBuilder.append(chr0(result.getData(barcoFormat.getData(i).getData(0).asString()).asString(), barcoFormat.getData(i).getData(1).asInteger()));
                if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(TOTAL_COD)){
                    int rnd = 1+result.getData(VERSION).asInteger();
                    if ( result.getData(HASH).asString().length()< 6 ) {
                        return Nson.newObject();//NO BARCODE
                    }else if ( uhash(stringBuilder.toString()).substring(rnd, rnd+6).equalsIgnoreCase( result.getData(HASH).asString().substring(0,6)  )){
                        return result;
                    }else{
                        return Nson.newObject();//NO BARCODE
                    }
                }
            }else{
                if (barcoFormat.getData(i).getData(0).asString().equalsIgnoreCase(TOTAL_COD)){
                    stringBuilder.append(chr0(result.getData(ONGKIR_BBM_BERAT).asString(), barcoFormat.getData(i).getData(1).asInteger()));
                }else{
                    stringBuilder.append(chr0(result.getData(barcoFormat.getData(i).getData(0).asString()).asString(), barcoFormat.getData(i).getData(1).asInteger()));
                }
            }
        }
        //type2
        int rnd = 1+result.getData(VERSION).asInteger();
        if ( result.getData(HASH).asString().length()< 6 ) {
            return Nson.newObject();//NO BARCODE
        }else if ( uhash(stringBuilder.toString()).substring(rnd, rnd+6).equalsIgnoreCase( result.getData(HASH).asString().substring(0,6)  )){
            return result;
        }else{
            return Nson.newObject();//NO BARCODE
        }
    }
    private static String uhash(String str){
        return  Utility.getNumberOnly(Utility.MD5(str))+"0000000000000000";
    }

    private static Nson decodeConA(String barcode, String encode){
        Nson result = Nson.newObject();
        String sEncode = barcode;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < conFormat.size(); i++) {
            int v = conFormat.getData(i).getData(1).asInteger();
            if (v > barcode.length()){
                break;
            }else{
                String vs = barcode.substring(0, v);
                barcode = barcode.substring(v);

                result.setData(conFormat.getData(i).getData(0).asString(), vs);
                stringBuilder.append(chr0(result.getData(conFormat.getData(i).getData(0).asString()).asString(), conFormat.getData(i).getData(1).asInteger()));

                if (conFormat.getData(i).getData(0).asString().equalsIgnoreCase(CON_DATE)){
                    result.setData(HASH, barcode);
                }
            }
        }
        //maping
        result.setData(NOMOR_KIRIMAN, result.getData(CON_NUMBER).asString());
        result.setData(BARCODE_NAME, BARCODE_NAME_CON);
        result.setData(BARCODE_ENCODE, encode);

        result.setData(JUMLAH_PCS, result.getData(CON_TOTAL_PCS).asInteger());
        result.setData(ROUTING_CODE, result.getData(CON_ROUTING_CODE).asInteger());
        result.setData(BERAT_CHARGES, result.getData(CON_TOTAL_ACTUAL_WEIGHT).asInteger());
        result.setData(KODE_CENTRAL, result.getData(CON_CENTRAL_KODE).asInteger());
        result.setData(KODE_REGIONAL, result.getData(CON_REGIONAL_KODE).asInteger());
        result.setData(KODE_CENTRAL_DEST, result.getData(CON_CENTRAL_KODE_DEST).asInteger());//new
        result.setData(KODE_REGIONAL_DEST, result.getData(CON_REGIONAL_KODE_DEST).asInteger());//new
        result.setData(PRODUK, result.getData(CON_PRODUCT_CODE).asInteger());
        result.setData(ORIGIN, result.getData(CON_ORIGIN).asInteger());
        result.setData(TANGGAL_KOMITMENT, result.getData(CON_MAX_COM_DATE_SHIPMENT).asInteger());




        int rnd =  1 + result.getData(VERSION).asInteger();
        if ( result.getData(HASH).asString().length()< 6 ) {
            return Nson.newObject();//NO BARCODE
        }else if ( uhash(stringBuilder.toString()).substring(rnd, rnd+6).equalsIgnoreCase( result.getData(HASH).asString().substring(0,6)  )){
            return result;
        }else{
            return Nson.newObject();//NO BARCODE
        }
    }
    private static String encodeConA(Nson barcode){
        barcode.setNullStringAsEmptyString(true);
        barcode.setData(VERSION, "1");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < conFormat.size(); i++) {
            stringBuilder.append(chr0(barcode.getData(conFormat.getData(i).getData(0).asString()).asString(), conFormat.getData(i).getData(1).asInteger()));
        }

        int rnd = 1 + barcode.getData(VERSION).asInteger();
        stringBuilder.append(  uhash(stringBuilder.toString()).substring(rnd, rnd+6) );
        return stringBuilder.toString();
    }


    /*
    public static final int TYPE_BARCODE        = 0;
    public static final int NOMOR_KIRIMAN       = 1;
    public static final int TANGGAL_KOMITMENT   = 2;
    public static final int ORIGIN              = 3;
    public static final int ROUTING_CODE        = 4;
    public static final int PONSEL_PENERIMA     = 5;
    public static final int GPS_PENERIMA        = 6;
    public static final int PRODUK              = 7;
    public static final int JUMLAH_PCS          = 8;
    public static final int BERAT_CHARGES       = 9;
    public static final int SERVICES_COMBO      = 10;
    public static final int TYPE_ALAMAT         = 11;
    public static final int TOTAL_COD           = 12;

    public static final int ONGKIR_BBM_BERAT    = 12;
    public static final int BIAYA_SERVIS        = 13;
    public static final int BIAYA_CLEARANCE     = 14;
    public static final int BIAYA_ODA           = 15;
    public static final int TOTAL_COD_SOD       = 16;
    public static final int PEMBAYARAN          = 17;*/



    public static long cash(Nson encode){
        if (encode.getData(PEMBAYARAN).asInteger()==2){
            //return 0;//invoice
        }
        //return 1;//cashLogic
        return  encode.getData(ONGKIR_BBM_BERAT).asLong()+
                encode.getData(BIAYA_SERVIS).asLong()+
                encode.getData(BIAYA_CLEARANCE).asLong()+
                encode.getData(BIAYA_ODA).asLong() ;
    }
    public static String comDate4(String com3){
        int i = Utility.getInt(com3);
        int d = i % 32;
        int m = (i / 32);
        return chr0(String.valueOf(d),  2)+"/"+chr0(String.valueOf(m),  2);
    }
    public static String comDate3(String com4){
        int d = 0;
        int m = 0;

        if (com4.contains("/")){
            List<String> vx = Utility.splitList(com4, "/");
            d = Utility.getInt(vx.get(0));
            m = Utility.getInt(vx.get(1));
        }else if (com4.contains("-")){
            List<String> vx = Utility.splitList(com4, "-");
            d = Utility.getInt(vx.get(0));
            m = Utility.getInt(vx.get(1));
        }else if (com4.length() == 4){
            d = Utility.getInt(com4.substring(0, 2));
            m = Utility.getInt(com4.substring(2));
        }else{

        }
        return String.valueOf(m * 32 + d);
    }
    //['ONE', 'NEX', 'TDX','REG', 'ECO', 'CFT', 'EFT']
    public static String productName(String pcode){
        if (pcode.equalsIgnoreCase("1")) {
            return "ONE";
        }else if (pcode.equalsIgnoreCase("2")) {
            return "NEX";
        }else if (pcode.equalsIgnoreCase("3")) {
            return "TDX";
        }else if (pcode.equalsIgnoreCase("4")) {
            return "REG";
        }else if (pcode.equalsIgnoreCase("5")) {
            return "ECO";
        }else if (pcode.equalsIgnoreCase("6")) {
            return "CFT";
        }else if (pcode.equalsIgnoreCase("7")) {
            return "EFT";
        }
        return "";
    }
    public static String productCode(String pname){
        if (pname.equalsIgnoreCase("ONE")) {
            return "1";
        }else if (pname.equalsIgnoreCase("NEX")) {
            return "2";
        }else if (pname.equalsIgnoreCase("TDX")) {
            return "3";
        }else if (pname.equalsIgnoreCase("REG")) {
            return "4";
        }else if (pname.equalsIgnoreCase("ECO")) {
            return "5";
        }else if (pname.equalsIgnoreCase("CFT")) {
            return "6";
        }else if (pname.equalsIgnoreCase("EFT")) {
            return "7";
        }
        return "";
    }
    private static final String[] namesOfDays = new String[] {  "MIN", "SEN", "SEL", "RAB", "KAM", "JUM", "SAB" };
    public static Calendar getDayCalendar(String com3){
        int i = Utility.getInt(com3);
        int d = i % 32;
        int m = (i / 32);
        Calendar calendar =  Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int rm  = calendar.get(Calendar.MONTH);
        if (rm <= 2) {
            if (m >=10 ) {
                y = y-1;
            }
        }else  if (rm >= 10) {
            if (m <=2 ) {
                y = y+1;
            }
        }
        calendar.set(y,  m-1, d);
        return calendar;
    }
    public static String getDayName(String com3){
        int day = getDayCalendar(com3).get(Calendar.DAY_OF_WEEK);
        return namesOfDays[day-1];
    }
    public static String encodeService (boolean...service8){
        if (service8 == null) {
        }else if (service8.length < 8) {
        }else{
            int sc = 0;
            for (int i = 0; i < 8; i++) {
                sc = sc + (service8[i]? (int) Math.pow(2, i):0);
            }
            return chr0(""+sc, 3);
        }
        return "000";
    }
    public static boolean[] decodeService(String sercom){
        boolean[] ret = new boolean[8];
        int isercom = Utility.getInt(sercom);
        for (int j = 0; j < 8; j++) {
            int bm = (int) Math.pow(2, j) ;
            ret[j] =  ((isercom & bm )== bm)?true:false;
        }
        return ret;
    }
}
