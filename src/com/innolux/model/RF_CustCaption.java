package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_custcaption")   //表名  
public class RF_CustCaption {

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
	 * Show this on Subtitle
	 */
	@Column("caption")
	private String Caption;
	
	public String getCaption() {  
        return Caption;  
    }  
  
    public void setCaption(String Caption) {  
        this.Caption = Caption;  
    }  
    
    /**
	 * The name ,who modify this
	 */
	@Column("username")
	private String UserName;
	
	public String getUserName() {  
        return UserName;  
    }  
  
    public void setUserName(String UserName) {  
        this.UserName = UserName;  
    }  
    
    /**
	 * Update Time
	 */
	@Column("updatetime")
	private String UpdateTime;
	
	public String getUpdateTime() {  
        return UpdateTime;  
    }  
  
    public void setUpdateTime(String UpdateTime) {  
        this.UpdateTime = UpdateTime;  
    }  
    
    /**
	 * Is this setting is available
	 */
	@Column("active")
	private String Active;
    
	public String getActive() {  
        return Active;  
    }  
  
    public void setActive(String Active) {  
        this.Active = Active;  
    }  
    
    @Override  
    public String toString() {  
        return  "ID: " + ID +        		
        		" Fab: " + Fab+
        		" Area: " + Area+
        		" Gate: " + Gate+
        		" Caption: " + Caption+
        		" UserName: " + UserName+
        		" UpdateTime: " + UpdateTime+
        		" Active: " + Active;
        		
    }  
}
