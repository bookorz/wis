package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_subtitle_setting") // 表名
public class RF_Subtitle_Setting {

	/**
	 * Subtitle IP
	 */
	@Id("subtitle_ip")
	private String Subtitle_IP;

	public String getSubtitle_IP() {
		return Subtitle_IP;
	}

	public void setSubtitle_IP(String Subtitle_IP) {
		this.Subtitle_IP = Subtitle_IP;
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
	 * Area (Receive, Delivery, Cylinders, CentralParking)
	 */
	@Column("area")
	private String Area;

	public String getArea() {
		return Area;
	}

	public void setArea(String Area) {
		this.Area = Area;
	}

	/**
	 * Gate
	 */
	@Column("gate")
	private String Gate;

	public String getGate() {
		return Gate;
	}

	public void setGate(String Gate) {
		this.Gate = Gate;
	}

	/**
	 * Current Subtitle
	 */
	@Column("current_subtitle")
	private String Current_Subtitle;

	public String getCurrent_Subtitle() {
		return Current_Subtitle;
	}

	public void setCurrent_Subtitle(String Current_Subtitle) {
		this.Current_Subtitle = Current_Subtitle;
	}
	
	/**
	 * Update Time
	 */
	@Column("update_time")
	private long Update_Time;
	
	public long getUpdate_Time() {  
        return Update_Time;  
    }  
  
    public void setUpdate_Time(long Update_Time) {  
        this.Update_Time = Update_Time;  
    }  
    
    /**
	 * Show this on Subtitle
	 */
	@Column("cust_subtitle")
	private String Cust_Subtitle;
	
	public String getCust_Subtitle() {  
        return Cust_Subtitle;  
    }  
  
    public void setCust_Subtitle(String Cust_Subtitle) {  
        this.Cust_Subtitle = Cust_Subtitle;  
    }  
    
    /**
	 * The name ,who modify this
	 */
	@Column("modify_username")
	private String Modify_UserName;
	
	public String getModify_UserName() {  
        return Modify_UserName;  
    }  
  
    public void setModify_UserName(String Modify_UserName) {  
        this.Modify_UserName = Modify_UserName;  
    }  
    
    /**
	 * Update Time
	 */
	@Column("modify_time")
	private String Modify_Time;
	
	public String getModify_Time() {  
        return Modify_Time;  
    }  
  
    public void setUpdateTime(String Modify_Time) {  
        this.Modify_Time = Modify_Time;  
    }  
    
    /**
	 * Is this setting is available
	 */
	@Column("cust_active")
	private boolean Cust_Active;
    
	public boolean getCust_Active() {  
        return Cust_Active;  
    }  
  
    public void setCust_Active(boolean Cust_Active) {  
        this.Cust_Active = Cust_Active;  
    }  

	@Override
	public String toString() {
		return "Subtitle_IP: " + Subtitle_IP + " Fab: " + Fab + " Area: " + Area + " Gate: " + Gate
				+ " Current_Subtitle: " + Current_Subtitle+ " Update_Time: " + Update_Time
				+ " Cust_Subtitle: " + Cust_Subtitle
				+ " Modify_UserName: " + Modify_UserName
				+ " Modify_Time: " + Modify_Time
				+ " Cust_Active: " + Cust_Active;

	}
}
