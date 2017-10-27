package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("jn_t2_wms.v_t2_wms_rfid_chk_pallet") // 表名
public class WMS_T2_Check_Pallet {
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
	 * DN number
	 */
	@Column("dn_no")
	private String DN_No;
	
	public String getDN_No() {  
        return DN_No;  
    }  
  
    public void setDN_No(String DN_No) {  
        this.DN_No = DN_No;  
    }  
    
    /** 
     * Fab (T1 T2)
     */  
    @Column("fab")  
    private String Fab;  
    
    public String getFab() {  
        return Fab;  
    }  
  
    public void setFab(String Fab) {  
        this.Fab = Fab;  
    }  
    
    /** 
     * Container No
     */  
	@Column("container_no")  
    private String Container_NO;  
	
	public String getContainer_NO() {  
        return Container_NO;
    }  
  
    public void setContainer_NO(String Container_NO) {  
        this.Container_NO = Container_NO;  
    }  
    
    /** 
     * Truck NO
     */  
	@Column("truck_no")  
    private String Truck_NO;  
	
	public String getTruck_NO() {  
        return Truck_NO;  
    }  
  
    public void setTruck_NO(String Truck_NO) {  
        this.Truck_NO = Truck_NO;  
    }  
    
    /** 
     * Comfirn flag
     */  
	@Column("status")  
    private String Status;  
	
	public String getStatus() {  
        return Status;  
    }  
  
    public void setStatus(String Status) {  
        this.Status = Status;  
    }  
    
    @Override  
    public String toString() {  
        return " Pallet_ID: " + Pallet_ID+
        		" DN_No: " + DN_No+
        		" Fab: " + Fab+
        		" Container_NO: " + Container_NO+
        		" Truck_NO: " + Truck_NO+
        		" Status: " + Status;   		
    }  
}
