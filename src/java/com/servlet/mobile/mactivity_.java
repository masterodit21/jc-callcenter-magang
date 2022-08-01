/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Date;

/**
 *
 * @author rkrzmail
 */
public class mactivity_  extends NikitaServlet{

     
    int dbCore;
    
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
         //sent activity
        NikitaConnection nc = response.getConnection("wom_survey");  
        
        
        
        dbCore = WebUtility.getDBCore(nc);  
        Nset n2 =  Nset.readJSON(request.getParameter("body")); 
        Nikitaset ns2 = nc.Query("SELECT * FROM MOBILE_ORDER_INITIATION WHERE MOBILE_ORDER_ID = ? ",n2.getData("wom_minitial_score_bag1").getData("lblorderid").getData(0).toString());
        boolean bt = true;
        try {
           
                //if(n2.getData("wom_minitial_score_bag5").getData("lblfase").getData(0).toString().equals("INITIATION")){  
                       
                                

                    if(ns2.getRows() == 0){
                        ns2 = nc.QueryNF("INSERT INTO MOBILE_ORDER_INITIATION(MOBILE_ORDER_ID,MAP_NO,KAPOS_ID,BMH_ID,BMH_NAME,"
                               + "NAME_FLAG,FIRST_NAME,MID_NAME,LAST_NAME,FIRST_TITLE,CUSTNAME,END_TITLE,E_KTP,EXP_ID,MERK_ID,"
                               + "VEHICLE_MODEL_ID,VEHICLE_YEAR,SOURCEORDER,SOURCEORDERNAME,DEALER_ID,DEALER_NAME,GRADING_CODE,"
                               + "FINANCE_TYPE,ORDER_TYPE,FLAG_SP,SP_CODE,PRICE_CODE,SUB_ORDER_TYPE,BPKB_OWNER,BPKB_NAME,OTR,DP,"
                               + "TENOR,ADMIN_FEE, INSTALLMENT_TYPE,AMT_INSTALLMENT,PAYMENT_TYPE,HOME_STATUS,STAYYEAR,STAYMONTH,"
                               + "PREFIX,NO_TELEPON,PREFIX_HP,NO_HP,PREFIX_HP2,NO_HP2,EDUCATION,SUPPORTPERSONS,CHILD_NAME,"
                               + "CHILD_SCHOOLNAME,CHILD_SCHOOLADDR,CHILD_SCHOOLPREFIX,CHILD_SCHOOLPHONE,SEPEDA_QTY,MOTOR_QTY,"
                               + "CAR_QTY,KATEGORI_PENGHASILAN,JOBCATID,JOBSTATID,JOB_ID,COUSAHA,USIA_POHON,JOB_TITLE,BUSINESS_ENTITY,"
                               + "LENGTHOFWORK,MONTHOFWORK,GAPOK,TUNJTETAP,TUNJTIDAKTETAP,PENGHASILAN_TOT,BUSINESS_YEAR,BUSINESS_MONTH,"
                               + "OMZET_DAILY,WORKSDAY,OMZET_MONTHLY,MARGINHASIL,OMZETKOTOR,KATEGORY_PENGHASILAN_PASANGAN,"
                               + "JOBCATID_PASANGAN,JOBSTAT_PASANGAN,JOB_ID_PASANGAN,COUSAHA_PASANGAN,USIA_POHON_PASANGAN,"
                               + "JOB_TITLE_PASANGAN,BUSINESS_ENTITY_PASANGAN,LENGTHOFWORK_PASANGAN,MONTHOFWORK_PASANGAN,"
                               + "GAPOK_PASANGAN,TUNJTETAP_PASANGAN,TUNJTIDAKTETAP_PASANGAN,PENGHASILAN_TOT_PASANGAN,"
                               + "BUSINESS_YEAR_PASANGAN,BUSINESS_MONTH_PASANGAN,OMZET_DAILY_PASANGAN,WORKSDAY_PASANGAN,"
                               + "OMZET_MONTHLY_PASANGAN,MARGINHASIL_PASANGAN,OMZETKOTOR_PASANGAN,ADDITIONAL_INCOME,"
                               + "ADDITIONAL_INCOME_SOURCE,TOTAL_INCOME,AMT_HOUSEHOLD,AMT_EDUCATION,AMT_RENT,AMT_EXIST_INT_INSTALLMENT,"
                               + "AMT_EXIST_EXT_INSTALLMENT,AMT_MONTHLY,NET_INCOME,PCTIIR,PCTDSR,MEMO,CAM_KTP_PMH,CAM_KTP_PS,CAM_KK,"
                               + "CAM_STATUS_TINGGAL,CAM_BUKTI_PENGHASILAN,GRADE,CREATEDBY) " 
                            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?,?,?,"
                                    + "?,?,?,?,?,?,?,?) ",Utility.argumentsQueryNF(
                            n2.getData("wom_minitial_score_bag1").getData("lblorderid").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txt_no_map").getData(0).toString(),
                            (n2.getData("wom_minitial_score_bag1").getData("rbt_penjualan_cabang").getData(0).toString().equals("[\"cabang\"]")?n2.getData("wom_minitial_score_bag1").getData("lbl_penampung_kapos").getData(0).toString():n2.getData("wom_minitial_score_bag1").getData("cmb_kapos_kassa").getData(0).toString()),
                            n2.getData("wom_minitial_score_bag1").getData("cmb_bmh").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txt_nama_bmh").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("chk_sama_dengan_ktp").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtfirstname").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtmiddlename").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtlastname").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtgelar1").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtfullname").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("txtgelar2").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag1").getData("rbtn_ektp").getData(0).toString(),
                            vdate(n2.getData("wom_minitial_score_bag1").getData("cmb_tgl_exp").getData(0).toString()),
                            //----------------------------------------------------------------------------------
                            n2.getData("wom_minitial_score_bag2").getData("cmb_brand").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_model").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_thn_produk").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_sumber_order").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_nama_sumber_order").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_id_dealer").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_nama_dealer").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_grading_dealer").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_cat_pembiayaan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_type_order").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("rbtn_special_program").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_nama_special_program").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_kode_pricelist").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_sub_type_order").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("chk_sama_dengan_pmh").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_fullname_bpkb").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_otr").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_dp_pricelist").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_tenor").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag2").getData("rbtn_loan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("cmb_tipe_angsuran").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag2").getData("txt_angsuran_per_bulan").getData(0).toLong(),
                            //----------------------------------------------------------------------------------
                            n2.getData("wom_minitial_score_bag3").getData("cmb_cara_bayar").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("cmb_status_rumah").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_lama_tinggal_thn").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_lama_tinggal_bln").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_kode_area_telp").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_no_telp").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_kode_area_hp1").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_no_hp1").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_kode_area_hp_2").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_no_hp2").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("cmb_pendidikan_terakhir").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_jml_tanggungan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_nama_anak").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_sekolah_anak").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("area_alamat_sekolah_anak").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_kode_area_sekolah").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_telp_sekolah").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_sepeda").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_motor").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag3").getData("txt_mobil").getData(0).toLong(), 
                            //----------------------------------------------------------------------------------
                            n2.getData("wom_minitial_score_bag4").getData("rbtn_cat_penghasilan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_jenis_pekerjaan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_status_pekerjaan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_pekerjaan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_bidang_usaha").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_usia_pohon").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_jabatan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_bentuk_usaha").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_tahun_bekerja").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_bulan_bekerja").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_gaji_pokok").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tunjangan_tetap").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tunjangan_tdk_tetap").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_penghasilan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_tahun_usaha").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_bulan_usaha").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_omzet").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_jml_hari_kerja").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_omzet").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_margin_penghasilan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_penghasilan2").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("rbtn_cat_penghasilan_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_jenis_pekerjaan_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_status_pekerjaan_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_pekerjaan_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_bidang_usaha_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_usia_pohon_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_jabatan_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_bentuk_badan_usaha_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_thn_kerja_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_bln_kerja_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_gaji_pokok_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tunjangan_tetap_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tunjangan_tdk_tetap_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_penghasilan_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_thn_usaha_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_lama_bulan_usaha_p").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_omzet_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_jml_hari_kerja_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_omzet_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_margin_penghasilan_p").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_tot_penghasilan_p2").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_penghasilan_tambahan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag4").getData("cmb_sumber_penghasilan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag4").getData("txt_penghasilan_pp").getData(0).toString(), 
                            //----------------------------------------------------------------------------------
                            n2.getData("wom_minitial_score_bag5").getData("txt_biaya_rt").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_biaya_pendidikan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_biaya_sewa_kontrak").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_angsuran_wom").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_angsuran_diluar").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_tot_pengeluaran").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_sisa_penghasilan").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_iir").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_dsr").getData(0).toLong(),
                            n2.getData("wom_minitial_score_bag5").getData("txtarea_memo_cmo").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("cam_ktp_pemohon").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("cam_ktp_pasangan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("cam_kk").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("cam_bukti_status_tpt_tinggal").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("cam_bukti_penghasilan").getData(0).toString(),
                            n2.getData("wom_minitial_score_bag5").getData("txt_result").getData(0).toString(),
                            request.getParameter("userid"))
                        );

                        if(!ns2.getError().equals("")){
                            nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                            n2.getData("wom_minitial_score_bag5").getData("lblorderid").getData(0).toString(),
                            "ININ", 
                            request.getParameter("activityid"),
                            ns2.getError());

                            /*
                            String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "ININ").toJSON();
                            response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                            return;
                            */
                        }
                        
                        //CALL PROCEDURE
                        ns2 = nc.Query("CALL WOM_MOBILE.RISK_PRC_SCARD_RESULT(?,'INITIATION') ",
                        n2.getData("wom_minitial_score_bag5").getData("lblorderid").getData(0).toString());
                        if(!ns2.getError().equals("")){
                            nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                            n2.getData("wom_minitial_score_bag5").getData("lblorderid").getData(0).toString(),
                            "CPIN", 
                            request.getParameter("activityid"),
                            ns2.getError());

                            String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "CPIN").toJSON();
                            response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                            return;
                        }
                        
                        //INSERT ORDER LIST
                        ns2 = nc.Query("update MOBILE_ORDER_LIST set BUCKET_PROSES='INITIATION',UPDATE_USER = ?, UPDATE_DATE= "+WebUtility.getDBDate(dbCore)+" "+
                                                     "WHERE MOBILE_ORDER_ID=? ",                                                                 
                                                  request.getParameter("userid"),
                                                  n2.getData("wom_minitial_score_bag5").getData("lblorderid").getData(0).toString());


                        if(!ns2.getError().equals("")){
                            nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                            n2.getData("wom_minitial_score_bag5").getData("lblorderid").getData(0).toString(),
                            "OLIN", 
                            request.getParameter("activityid"),
                            ns2.getError());


                            String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "OLIN").toJSON();
                            response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                            return;
                        }

                //}//tutup disini 
        }
        //END INITIATION
        
        } catch (Exception e) {
     
            String mresult = Nset.newObject().setData("error", String.valueOf(e)).setData("state", "INITIATION").toJSON();
            response.writeStream( Nset.newObject().setData("error", String.valueOf(e)).setData("mobileresult", mresult).toJSON());
            return;
        }
        
        try {
            
        if(n2.getData("wom_msurvey_verification6").getData("lblfase").getData(0).toString().equals("SURVEY VERIFICATION")){  
                   //CALL PROCEDURE
                    ns2 = nc.Query("CALL WOM_MOBILE.RISK_PRC_SCARD_RESULT(?,'SURVEY VERIFICATION')",
                    n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString());
                    
                    if(!ns2.getError().equals("")){
                        nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                        n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString(),
                        "CPSV", 
                        request.getParameter("activityid"),
                        ns2.getError());
                        
                        /*
                        String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "CPSP").toJSON();
                        response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                        return;
                        */
                    }           
                    
                    
                    ns2 = nc.QueryNF("INSERT INTO MOBILE_ORDER_SCORING(MOBILE_ORDER_ID,MAP_NO,NATIONALITY,RESIDENTIAL,EMAIL_ADDR,"
                           + "NPWP_OWNERSHIP,NPWP_ID,JK_PASANGAN,E_KTP,EXP_ID,JENIS_ALAMAT_PASANGAN,ALAMAT_PASANGAN,RT_PASANGAN,"
                           + "RW_PASANGAN,PROV_PASANGAN,KAB_PASANGAN,KEL_PASANGAN,KEC_PASANGAN,ZIP_PASANGAN,PREFIX_PASANGAN,"
                           + "PHONE_PASANGAN,PREFIX_HP_PASANGAN,HP_PASANGAN,GUARANTOR,GUARANTOR_NAME,GUARANTOR_ID,GUARANTOR_EXP_ID,"
                           + "POB_GUARANTOR,DOB_GUARANTOR,GUARANTOR_SEX,GUARANTOR_JOBCATID,GUARANTORWORK,GUARANTORMONTHWORK,"
                           + "GUARANTOR_RELATION,EMERGENCY_CONTACT_NAME,EMERGENCY_ADDRESS,EMERGENCY_RT,EMERGENCY_RW,EMERGENCY_PROV,"
                           + "EMERGENCY_KAB,EMERGENCY_KEL,EMERGENCY_KEC,EMERGENCY_ZIP,EMERGENCY_PREFIX,EMERGENCY_PHONE,"
                           + "EMERGENCY_PREFIX_HP,EMERGENCY_HP,EMERGENCY_RELATION,BPKB,BPKB_ADDR,BPKB_RT,BPKB_RW,BPKB_PROV,BPKB_KAB,"
                           + "BPKB_KEC,BPKB_KEL,BPKB_ZIP,CO_NAME,EMPLOYEE_QTY,CO_BIZADDR,CO_ADDR,CO_RT,CO_RW,CO_PROV,CO_KAB,CO_KEL,"
                           + "CO_KEC,CO_ZIP,CO_PREFIX,CO_PHONE,CO_PHONE_EXT,CO_FAX_PREFIX,CO_FAX_NO,CO_SPOUSE_FLAG,CO_SPOUSE_ADDR,"
                           + "CO_SPOUSE_RT,CO_SPOUSE_RW,CO_SPOUSE_PROV,CO_SPOUSE_KAB,CO_SPOUSE_KEL,CO_SPOUSE_KEC,CO_SPOUSE_ZIP,"
                           + "CO_SPOUSE_PREFIX,CO_SPOUSE_PHONE,CO_SPOUSE_PHONE_EXT,CO_SPOUSE_FAX_PREFIX,CO_SPOUSE_FAX_NO,SURVEY1,"
                           + "JARAK_TEMPUH,FLOOR_MATERIAL,ROOF_MATERIAL,WALL_MATERIAL,LINGKUNGAN,STREET_MATERIAL,ELECTRICITY,"
                           + "HOUSE_FRT_PHT,HOME_INSD_PHT,VEHICLE_FRT_PHT,VEHICLE_BHD_PHT,VEHICLE_RGH_PHT,VEHICLE_LFT_PHT,"
                           + "SPEDOMETER_PHT,OTHER1_PHT,OTHER2_PHT,BANK_NAME,ACCOUNT_NAME,ACCOUNT_NO,MONTHLY_INCOME,MONTHLY_COST,"
                           + "MONTHLY_INCOME_FREQUENT,MONTHLY_COST_FREQUENT,CRCARDISSUER,CRCARDNO,CRCARD_FREQUENT,OPEN_ACCOUNT,"
                           + "SC_FLAG,PRODUCT_ORIGIN,PRODUCT_OFFERING,PRODUCT_DEC,SIGN_CITY,"
                           + "SIGN_DATE,SIGN_PHT,SIGNSPS_PHT,SIGN_BPKB_PHT,INFORMANT,INFORMANT_RELATION,INFORMANT_FAMILIAR,"
                           + "CONFIRM_ADDR,CONFIRM_HOUSE_STAT,CONF_STAYYEAR,CONF_STAYMONTH,JOB_SURVEY_FLAG,CONFIRM_JOB,CONFIRM_DEBT,"
                           + "SOCIALATTITUDE,NOTES,INFORMANT2,INFORMANT2_RELATION,INFORMANT2_FAMILIAR,CONFIRM2_ADDR,CONFIRM2_HOUSE_STAT,"
                           + "CONF2_STAYYEAR,CONF2_STAYMONTH,JOB_SURVEY2_FLAG,CONFIRM2_JOB,CONFIRM2_DEBT,SOCIALATTITUDE2,NOTES2,APPSCR_RESULT_GRADE2,CREATEDBY) " 
                           + "VALUES (?,?,?,?,?,?,?,?,?,?,"
                                   + "?,?,?,?,?,?,?,?,?,?,"
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?," 
                                   + "?,?,?,?,?,?,?,?,?,?) ",Utility.argumentsQueryNF(
                        n2.getData("wom_msurvey_verification1").getData("lblorderid").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("lbl_no_map_aplikasi").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("kewarganegaraan_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("kependudukan_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("alamat_email_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("opsi_npwp").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("npwp").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("jenis_kelamin_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("opsi_e_ktp").getData(0).toString(),
                        vdate(n2.getData("wom_msurvey_verification1").getData("tanggal_masa_berlaku").getData(0).toString()),
                        n2.getData("wom_msurvey_verification1").getData("chk_sama_dengan_alamat_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("nama_jalan_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("rt_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("rw_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("cmb_provinsi").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("cmb_kab").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("cmb_kel").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("cmb_kec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("txt_kdpos").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("prefix_no_telepon_rumah").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("no_telepon_rumah").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("prefix_no_mobile_phone").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("no_mobile_phone").getData(0).toString(),
                        n2.getData("wom_msurvey_verification1").getData("opsi_perlu_data_pinjaman").getData(0).toString(),
                        //----------------------------------------------------------------------------------
                        n2.getData("wom_msurvey_verification2").getData("nama_lengkap_penjamin").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("no_ktp_penjamin").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("tgl_masa_berlaku_ktp_penjamin").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("tempat_lahir_penjamin").getData(0).toString(),
                        vdate(n2.getData("wom_msurvey_verification2").getData("tgl_lahir_penjamin").getData(0).toString()),
                        n2.getData("wom_msurvey_verification2").getData("opsi_jenis_kelamin_penjamin").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("jenis_pekerjaan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification2").getData("tahun_lama_bekerja").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("bulan_lama_bekerja").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("hubungan_dengan_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("nama_lengkap_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("nama_jalan_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("rt_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("rw_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("cmb_provinsi").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("cmb_kab").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("cmb_kel").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("cmb_kec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("kode_pos_ec").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification2").getData("prefix_no_telepon_rumah_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("no_telepon_rumah_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("prefix_no_mobile_phone_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("no_mobile_phone_ec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification2").getData("hubungan_dengan_pemohon_ec").getData(0).toString(),
                        //-------------------------------------------------------------------------------------
                        n2.getData("wom_msurvey_verification3").getData("nama_pemohon_bpkb").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("nama_jalan_bpkb").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("rt_bpkb").getData(0).toString(),
                        n2.getData("wom_msurvey_verification").getData("rw_bpkb").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_provinsi").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kab").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kel").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kec").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("txt_kdpos").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("nama_perusahaan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("jumlah_karyawan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification3").getData("chk_sama_dengan_alamat_tinggal_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("nama_jalan_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("rt_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("rw_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_provinsi2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kab2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kel2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kec2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("txt_kdpos2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("prefix_no_telepon_kantor").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("no_telepon_kantor").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("ext_kantor").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("prefix_no_fax").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("no_fax").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("chk_sama_dengan_alamat_tinggal_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("nama_jalan_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("rt_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("rw_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_provinsi3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kab3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kel3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("cmb_kec3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("txt_kdpos3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("prefix_no_telepon_kantor_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("no_telepon_kantor_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("ext_kantor_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("prefix_no_fax_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification3").getData("no_fax_pasangan").getData(0).toString(),
                        //----------------------------------------------------------------------------------
                        n2.getData("wom_msurvey_verification4").getData("opsi_survey_leasing_lain").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("jarak_tempuh_tpt_tinggal_pemohon").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification4").getData("material_lantai_tpt_tinggal").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cmb_material_atap_tpt_tinggal").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("material_dinding_tpt_tinggal").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cmb_lokasi_tpt_tinggal").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("jalan_menuju_lokasi_rumah").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("daya_listrik").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_depan_rumah_tinggal").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_dalam_rumah").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_depan_kendaraan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_belakang_kendaraan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_samping_kanan_kendaraan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_samping_kiri_kendaraan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_tampak_spedometer").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_foto_pendukung_lainnya1").getData(0).toString(),
                        n2.getData("wom_msurvey_verification4").getData("cam_foto_pendukung_lainnya2").getData(0).toString(),
                        //-------------------------------------------------------------------------------------
                        n2.getData("wom_msurvey_verification5").getData("nama_bank").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("nama_pemilik_bank").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("no_rekening_bank").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("transaksi_uang_masuk_perbulan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification5").getData("transaksi_uang_keluar_perbulan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification5").getData("frekuensi_uang_masuk_perbulan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification5").getData("frekuensi_uang_keluar_perbulan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification5").getData("kartu_kredit_yang_dimiliki").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("no_kartu_kredit").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("rata_rata_pemakaian_kk_perbulan").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification5").getData("pertanyaan_1_1").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("pertanyaan_1_2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("pertanyaan_2_1").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("pertanyaan_2_2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("pertanyaan_2_3").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("nama_kota").getData(0).toString(),
                        vdate(n2.getData("wom_msurvey_verification5").getData("tgl_ttd").getData(0).toString()),
                        n2.getData("wom_msurvey_verification5").getData("ttd_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("ttd_pasangan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification5").getData("ttd_pemohon_bpkb").getData(0).toString(),
                        //-------------------------------------------------------------------------------------
                        n2.getData("wom_msurvey_verification6").getData("nama_sumber_informasi").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("hubungan_dengan_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_kenal_dengan_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_alamat_konsumen_sesuai_pengajuan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("status_rumah_pemohon").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("tahun_menempati").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification6").getData("bulan_menempati").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_pekerjaan_konsumen_sesuai").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("kondisi_pekerjaan_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_konsumen_terlibat_hutang").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("hub_sosial_konsumen").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("informasi_tambahan").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("nama_sumber_informasi2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("hubungan_dengan_pemohon2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_kenal_dengan_pemohon2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_alamat_konsumen_sesuai_pengajuan2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("status_rumah_pemohon2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("tahun_menempati2").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification6").getData("bulan_menempati2").getData(0).toLong(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_pekerjaan_konsumen_sesuai2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("kondisi_pekerjaan_konsumen2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("opsi_konsumen_terlibat_hutang2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("hub_sosial_konsumen2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("informasi_tambahan2").getData(0).toString(),
                        n2.getData("wom_msurvey_verification6").getData("final_score_aplikasi").getData(0).toString(),
                        request.getParameter("userid"))                    
                         );
                    
                    if(!ns2.getError().equals("")){
                        nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                        n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString(),
                        "INSV", 
                        request.getParameter("activityid"),
                        ns2.getError());

                        /*
                        String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "INSV").toJSON();
                        response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                        return;
                                */
                    }
                    
                    
                    
                //INSERT ORDER LIST
                ns2 = nc.Query("update MOBILE_ORDER_LIST set BUCKET_PROSES='SURVEY VERIFICATION',UPDATE_USER = ?, UPDATE_DATE= "+WebUtility.getDBDate(dbCore)+" "+
                                             "WHERE MOBILE_ORDER_ID=? ",                                                                 
                                          request.getParameter("userid"),
                                          n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString());
                        
                        
                if(!ns2.getError().equals("")){
                    nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
                    n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString(),
                    "OLSV", 
                    request.getParameter("activityid"),
                    ns2.getError());
                    
                     String mresult = Nset.newObject().setData("error", ns2.getError()).setData("state", "OLSV").toJSON();
                    response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
                    return;
                }
                    
            }
            //END SURVEY VERIVICATION       
        } catch (Exception e) {

            String mresult = Nset.newObject().setData("error", String.valueOf(e)).setData("state", "SURVEY").toJSON();
            response.writeStream( Nset.newObject().setData("error", String.valueOf(e)).setData("mobileresult", mresult).toJSON());
            return;
        }
          
        
        try {
            nc.Query("INSERT INTO MOBILE_ACTIVITY (MOBILE_ORDER_ID, STATUS, MOBILE_ACTIVITY_ID,MOBILE_LOG) VALUES (?,?,?,?) ",
            n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString(),
            "1", 
            request.getParameter("activityid"),
            "");

            
            
            String mresult = nc.Query("select INITIAL_SCORE,FINAL_SCORE,INITIAL_DESC,FINAL_DESC from MOBILE_ORDER_LIST WHERE MOBILE_ORDER_ID = ?", 
                    n2.getData("wom_msurvey_verification6").getData("lblorderid").getData(0).toString()).toNset().toJSON();
            
            response.writeStream( Nset.newObject().setData("status", "OK").setData("mobileresult", mresult).toJSON());
	
        } catch (Exception e) {             
            
            String mresult = Nset.newObject().setData("error", String.valueOf(e)).setData("state", "FINISH").toJSON();
            response.writeStream( Nset.newObject().setData("error", String.valueOf(e)).setData("mobileresult", mresult).toJSON());
	
            return;
        }
    }
		
		
		
        private Object vdate(String date){
            try {
                final long l = Utility.getDate(date);
                return new Object(){ public String toString() {
                    return "date|"+Utility.formatDate(l, "yyyy-MM-dd");
                }};
            } catch (Exception e) {
                return new Object(){ public String toString() {
                    return "date|0000-00-00"; 
                }};
  
            }
        }
		
		
    }