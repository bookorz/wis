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
     * Reader type 
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
    private String Test_Mode;  
  
    public String getTest_Mode() {  
        return Test_Mode;  
    }  
  
    public void setTest_Mode(String Test_Mode) {  
        this.Test_Mode = Test_Mode;  
    }  
	
	@Override  
    public String toString() {  
        return "Reader_IP: " + Reader_IP + 
        		" Listen_Port: " + Listen_Port+
        		" Reader_Type: " + Reader_Type+
        		" Test_Mode: " + Test_Mode;
        		
    }  
}
