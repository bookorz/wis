package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("rf_error_pallet") // 表名
public class RF_Error_Pallet {
	/** 
     * Container ID
     */  
	@Column("container_id")  
    private String Container_ID;  
	
	public String getContainer_ID() {  
        return Container_ID;  
    }  
  
    public void setContainer_ID(String Container_ID) {  
        this.Container_ID = Container_ID;  
    }  
    
    /** 
     * Pallet ID
     */  
	@Column("pallet_id")  
    private String Pallet_ID;  
	
	public String getPallet_ID() {  
        return Pallet_ID;  
    }  
  
    public void setPallet_ID(String Pallet_ID) {  
        this.Pallet_ID = Pallet_ID;  
    }  
    
    /**
	 * Opreation Mode
	 */
	@Column("opreation_mode")
	private String Opreation_Mode;
	
	public String getOpreation_Mode() {  
        return Opreation_Mode;  
    }  
  
    public void setOpreation_Mode(String Opreation_Mode) {  
        this.Opreation_Mode = Opreation_Mode;  
    }  
    
    /**
	 * TimeStamp
	 */
	@Column("timestamp")
	private Date TimeStamp;
	
	public Date getTimeStamp() {  
        return TimeStamp;  
    }  
  
    public void setTimeStamp(Date TimeStamp) {  
        this.TimeStamp = TimeStamp;  
    }  
    
    public String getTimeStampString(){
    	
    	return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeStamp);
    }
    
    @Override  
    public String toString() {  
        return "Container_ID: " + Container_ID + 
        		" Pallet_ID: " + Pallet_ID+
        		" Opreation_Mode: " + Opreation_Mode+       		
        		" TimeStamp: " + TimeStamp+
        		" TimeStamp: " + getTimeStampString();   		
    }  
}
