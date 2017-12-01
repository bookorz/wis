package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_pallet_check") // 表名
public class RF_Pallet_Check { 
	
	/**
	 * ID
	 */
	@Id("id")
	private String ID;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
	
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
	 * Position (T1 T2)
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
	@Column("UpdateTime")
	private String UpdateTime;
	
	public String getUpdateTime() {  
        return UpdateTime;  
    }  
  
    public void setUpdateTime(String UpdateTime) {  
        this.UpdateTime = UpdateTime;  
    }  
    
    @Override  
    public String toString() {  
        return "ID: " + ID +" Container_ID: " + Container_ID + 
        		" Pallet_ID: " + Pallet_ID+
        		" In_Container: " + In_Container+
        		" Opreation_Mode: " + Opreation_Mode+       		
        		" Position: " + Position+
        		" DN_No: " + DN_No+
        		" UpdateTime: " + UpdateTime;   		
    }  
}
