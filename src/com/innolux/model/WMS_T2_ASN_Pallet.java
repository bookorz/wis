package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("jn_t2_wms.v_t2_wms_asn_rfid_pallet") // 表名
public class WMS_T2_ASN_Pallet {
	/** 
     * ASN No
     */  
	@Column("asn_no")  
    private String ASN_NO; 
	
	public String getASN_NO() {  
        return ASN_NO;  
    }  
  
    public void setASN_NO(String ASN_NO) {  
        this.ASN_NO = ASN_NO;  
    }  
    
    /** 
     * Pallet ID
     */  
	@Id("pallet_id")  
    private String Pallet_ID; 
	
	public String getPallet_ID() {  
        return Pallet_ID;  
    }  
  
    public void setPallet_ID(String Pallet_ID) {  
        this.Pallet_ID = Pallet_ID;  
    }  
    
    /** 
     * Car No
     */  
	@Column("car_no")  
    private String Car_NO; 
	
	public String getCar_NO() {  
        return Car_NO;  
    }  
  
    public void setCar_NO(String Car_NO) {  
        this.Car_NO = Car_NO;  
    }
    
    /** 
     * Comfirn flag
     */  
	@Column("rfid_chk")  
    private String RFID_Chk; 
	
	public String getRFID_Chk() {  
        return RFID_Chk;  
    }  
  
    public void setRFID_Chk(String RFID_Chk) {  
        this.RFID_Chk = RFID_Chk;  
    }  
    
    @Override  
    public String toString() {  
        return " ASN_NO: " + ASN_NO+
        		" Pallet_ID: " + Pallet_ID+
        		" Car_NO: " + Car_NO+
        		" RFID_Chk: " + RFID_Chk;   		
    }  
}
