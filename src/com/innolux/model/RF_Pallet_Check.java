package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_pallet_check") // 銵典��
public class RF_Pallet_Check { 
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
	@Id("pallet_id")  
    private String Pallet_ID;  
	
	public String getPallet_ID() {  
        return Pallet_ID;  
    }  
  
    public void setPallet_ID(String Pallet_ID) {  
        this.Pallet_ID = Pallet_ID;  
    }  
    
    /** 
     * In Container
     */  
	@Column("in_container")  
    private boolean In_Container;  
	
	public boolean getIn_Container() {  
        return In_Container;  
    }  
  
    public void setIn_Container(boolean In_Container) {  
        this.In_Container = In_Container;  
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
	 * Position
	 */
	@Column("position")
	private String Position;

	public String getPosition() {
		return Position;
	}

	public void setPosition(String Position) {
		this.Position = Position;
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
        		" In_Container: " + In_Container+
        		" Opreation_Mode: " + Opreation_Mode+       		
        		" Position: " + Position+
        		" DN_No: " + DN_No+
        		" TimeStamp: " + getTimeStampString();   		
    }  
}
