package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("rf_move_history") // 表名
public class RF_Move_History {

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
     * Size of container
     */  
    @Column("container_type")  
    private String Container_Type;  
  
    public String getContainer_Type() {  
        return Container_Type;  
    }  
  
    public void setContainer_Type(String Container_Type) {  
        this.Container_Type = Container_Type;  
    }  
    
    /** 
     * Empty or nonempty
     */  
    @Column("container_status")  
    private String Container_Status;  
    
    public String getContainer_Status() {  
        return Container_Status;  
    }  
  
    public void setContainer_Status(String Container_Status) {  
        this.Container_Status = Container_Status;  
    }  

    /** 
     * Vendor Name
     */  
    @Column("vendor_name")  
    private String Vendor_Name;  
    
    public String getVendor_Name() {  
        return Vendor_Name;  
    }  
  
    public void setVendor_Name(String Vendor_Name) {  
        this.Vendor_Name = Vendor_Name;  
    }  
    
    /** 
     * Car ID
     */  
    @Column("car_id")  
    private String Car_ID; 
    
    public String getCar_ID() {  
        return Car_ID;  
    }  
  
    public void setCar_ID(String Car_ID) {  
        this.Car_ID = Car_ID;  
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
     * Car_Type
     */  
    @Column("car_type")  
    private String Car_Type;
    
    public String getCar_Type() {  
        return Car_Type;  
    }  
  
    public void setCar_Type(String Car_Type) {  
        this.Car_Type = Car_Type;  
    }  
    
    /** 
     * Reason of check-in
     */  
    @Column("reason")  
    private String Reason;
    
    public String getReason() {  
        return Reason;  
    }  
  
    public void setReason(String Reason) {  
        this.Reason = Reason;  
    }  
    
    /** 
     * Driver Name
     */  
    @Column("drivername")  
    private String DriverName;
    
    public String getDriverName() {  
        return DriverName;  
    }  
  
    public void setDriverName(String DriverName) {  
        this.DriverName = DriverName;  
    }  
    
    /** 
     * Driver Phone
     */  
    @Column("driverphone")  
    private String DriverPhone;
    
    public String getDriverPhone() {  
        return DriverPhone;  
    }  
  
    public void setDriverPhone(String DriverPhone) {  
        this.DriverPhone = DriverPhone;  
    }  
    
    /** 
     * Source
     */  
    @Column("source")  
    private String Source;
    
    public String getSource() {  
        return Source;  
    }  
  
    public void setSource(String Source) {  
        this.Source = Source;  
    }  
    
    /**
	 * TimeStamp
	 */
	@Column("timestamp")
	private String TimeStamp;
	
	public String getTimeStamp() {  
        return TimeStamp;  
    }  
  
    public void setTimeStamp(String TimeStamp) {  
        this.TimeStamp = TimeStamp;  
    }  
  
    @Override  
    public String toString() {  
        return "Container_ID: " + Container_ID + 
        		" Container_Type: " + Container_Type+
        		" Container_Status: " + Container_Status+
        		" Vendor_Name: " + Vendor_Name+       		
        		" Car_ID: " + Car_ID+
        		" Fab: " + Fab+
        		" Area: " + Area+
        		" Gate: " + Gate+
        		" Car_Type: " + Car_Type+     		
        		" Reason: " + Reason+
        		" DriverName: " + DriverName+
        		" DriverPhone: " + DriverPhone+
        		" Source: " + Source+
        		" TimeStamp: " + TimeStamp;   		
    }  
}
