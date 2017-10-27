package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_signaltower_setting") // 表名
public class RF_SignalTower_Setting {
	/**
	 * SignalTower Name Name
	 */
	@Id("signaltower_name")
	private String SignalTower_Name;

	public String getSignalTower_Name_Name() {
		return SignalTower_Name;
	}

	public void setSignalTower_Name(String SignalTower_Name) {
		this.SignalTower_Name = SignalTower_Name;
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
	@Column("state")
	private String State;

	public String getState() {
		return State;
	}

	public void setState(String State) {
		this.State = State;
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
	
	@Override  
    public String toString() {  
        return "SignalTower_Name: " + SignalTower_Name + 
        		" Reader_IP: " + Reader_IP+
        		" State: " + State+
        		" Red_On_Cmd: " + Red_On_Cmd+
        		" Red_Off_Cmd: " + Red_Off_Cmd+
        		" Orange_On_Cmd: " + Orange_On_Cmd+
        		" Orange_Off_Cmd: " + Orange_Off_Cmd+
        		" Green_On_Cmd: " + Green_On_Cmd+
        		" Green_Off_Cmd: " + Green_Off_Cmd;
        		
    }  
}
