package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_signaltower_setting") // 表名
public class RF_SignalTower_Setting {
	/**
	 * SignalTower Name Name
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
	 * Reader IP
	 */
	@Column("reader_ip")
	private String Reader_IP;

	public String getReader_IP() {
		return Reader_IP;
	}

	public void setReader_IP(String Reader_IP) {
		this.Reader_IP = Reader_IP;
	}

	/**
	 * State
	 */
	@Column("red_state")
	private boolean Red_State;

	public boolean getRed_State() {
		return Red_State;
	}

	public void setRed_State(boolean Red_State) {
		this.Red_State = Red_State;
	}
	
	/**
	 * State
	 */
	@Column("orange_state")
	private boolean Orange_State;

	public boolean getOrange_State() {
		return Orange_State;
	}

	public void setOrange_State(boolean Orange_State) {
		this.Orange_State = Orange_State;
	}
	
	/**
	 * State
	 */
	@Column("green_state")
	private boolean Green_State;

	public boolean getGreen_State() {
		return Green_State;
	}

	public void setGreen_State(boolean Green_State) {
		this.Green_State = Green_State;
	}
	
	/**
	 * Red Cmd
	 */
	@Column("red_on_cmd")
	private String Red_On_Cmd;
	
	public String getRed_On_Cmd() {  
        return Red_On_Cmd;  
    }  
  
    public void setRed_On_Cmd(String Red_On_Cmd) {  
        this.Red_On_Cmd = Red_On_Cmd;  
    }  
    
    /**
	 * Red Cmd
	 */
	@Column("red_off_cmd")
	private String Red_Off_Cmd;
	
	public String getRed_Off_Cmd() {  
        return Red_Off_Cmd;  
    }  
  
    public void setRed_Off_Cmd(String Red_Off_Cmd) {  
        this.Red_Off_Cmd = Red_Off_Cmd;  
    }  
    
    /**
	 * Orange Cmd
	 */
	@Column("orange_on_cmd")
	private String Orange_On_Cmd;
	
	public String getOrange_On_Cmd() {  
        return Orange_On_Cmd;  
    }  
  
    public void setOrange_On_Cmd(String Orange_On_Cmd) {  
        this.Orange_On_Cmd = Orange_On_Cmd;  
    } 
    
    /**
	 * Orange Cmd
	 */
	@Column("orange_off_cmd")
	private String Orange_Off_Cmd;
	
	public String getOrange_Off_Cmd() {  
        return Orange_Off_Cmd;  
    }  
  
    public void setOrange_Off_Cmd(String Orange_Off_Cmd) {  
        this.Orange_Off_Cmd = Orange_Off_Cmd;  
    } 
    
    /**
	 * Green Cmd
	 */
	@Column("green_on_cmd")
	private String Green_On_Cmd;
	
	public String getGreen_On_Cmd() {  
        return Green_On_Cmd;  
    }  
  
    public void setGreen_On_Cmd(String Green_On_Cmd) {  
        this.Green_On_Cmd = Green_On_Cmd;  
    }
    
    /**
	 * Green Cmd
	 */
	@Column("green_off_cmd")
	private String Green_Off_Cmd;
	
	public String getGreen_Off_Cmd() {  
        return Green_Off_Cmd;  
    }  
  
    public void setGreen_Off_Cmd(String Green_Off_Cmd) {  
        this.Green_Off_Cmd = Green_Off_Cmd;  
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
	 * Active Expire
	 */
	@Column("active_expire")
	private long Active_Expire;

	public long getActive_Expire() {
		return Active_Expire;
	}

	public void setActive_Expire(long Active_Expire) {
		this.Active_Expire = Active_Expire;
	}
	
	@Override  
    public String toString() {  
        return "ID: " + ID + 
        		" Reader_IP: " + Reader_IP+
        		" Active_Expire: " + Active_Expire+
        		" Red_State: " + Red_State+
        		" Orange_State: " + Orange_State+
        		" Green_State: " + Green_State+
        		" Red_On_Cmd: " + Red_On_Cmd+
        		" Red_Off_Cmd: " + Red_Off_Cmd+
        		" Orange_On_Cmd: " + Orange_On_Cmd+
        		" Orange_Off_Cmd: " + Orange_Off_Cmd+
        		" Green_On_Cmd: " + Green_On_Cmd+
        		" Green_Off_Cmd: " + Green_Off_Cmd+ " Fab: " + Fab + " Area: " + Area + " Gate: " + Gate;
        		
    }  
}
