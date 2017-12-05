package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_reader_setting")   //表名  
public class RF_Reader_Setting {
	
	/** 
     * Reader IP
     */  
	@Id("reader_ip")  
    private String Reader_IP;  
	
	public String getReader_IP() {  
        return Reader_IP;  
    }  
  
    public void setReader_IP(String Reader_IP) {  
        this.Reader_IP = Reader_IP;  
    }  
  
    /** 
     * Listen from reader's port
     */  
    @Column("listen_port")  
    private int Listen_Port;  
    
    public int getListen_Port() {  
        return Listen_Port;  
    }  
  
    public void setListen_Port(int Listen_Port) {  
        this.Listen_Port = Listen_Port;  
    }  
    
    /** 
     * Port or Cylinder
     */  
    @Column("location")  
    private String Location;  
    
    public String getLocation() {  
        return Location;  
    }  
  
    public void setLocation(String Location) {  
        this.Location = Location;  
    }  
    
    /** 
     * Alien or Socket
     */  
    @Column("reader_type")  
    private String Reader_Type;  
    
    public String getReader_Type() {  
        return Reader_Type;  
    }  
  
    public void setReader_Type(String Reader_Type) {  
        this.Reader_Type = Reader_Type;  
    }  
    
    /** 
     * For debug use
     */  
    @Column("test_mode")  
    private boolean Test_Mode;  
  
    public boolean getTest_Mode() {  
        return Test_Mode;  
    }  
  
    public void setTest_Mode(boolean Test_Mode) {  
        this.Test_Mode = Test_Mode;  
    }  
	
	/** 
     * On_Line
     */  
    @Column("on_line")  
    private boolean On_Line;  
  
    public boolean getOn_Line() {  
        return On_Line;  
    }  
  
    public void setOn_Line(boolean On_Line) {  
        this.On_Line = On_Line;  
    }  
    
    /** 
     * Start Delay
     */  
    @Column("start_delay")  
    private long Start_Delay;  
    
    public long getStart_Delay() {  
        return Start_Delay;  
    }  
  
    public void setStart_Delay(long Start_Delay) {  
        this.Start_Delay = Start_Delay;  
    }  
	
	@Override  
    public String toString() {  
        return "Reader_IP: " + Reader_IP + 
        		" Listen_Port: " + Listen_Port+
        		" Location: " + Location+
        		" Reader_Type: " + Reader_Type+
        		" Test_Mode: " + Test_Mode+
        		" On_Line: " + On_Line+
        		" Start_Delay: " + Start_Delay;
        		
    }  
}
