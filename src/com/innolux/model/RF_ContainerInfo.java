package com.innolux.model;  

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;  
  

@Entity("rf_containerinfo")   //表名  
public class RF_ContainerInfo {  
  
    /** 
     * Container ID
     */  
	@Id("container_id")  
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
     * Start Time of check-in
     */  
    @Column("starttime")  
    private String StartTime;  
    
    public String getStartTime() {  
        return StartTime;  
    }  
  
    public void setStartTime(String StartTime) {  
        this.StartTime = StartTime;  
    }  
    
    /** 
     * End Time of check-out(not use)
     */  
    @Column("endtime")  
    private String EndTime; 
    
    public String getEndTime() {  
        return EndTime;  
    }  
  
    public void setEndTime(String EndTime) {  
        this.EndTime = EndTime;  
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
     * Imp No
     */  
    @Column("impno")  
    private String ImpNo;
    
    public String getImpNo() {  
        return ImpNo;  
    }  
  
    public void setImpNo(String ImpNo) {  
        this.ImpNo = ImpNo;  
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
     * Vendor ID
     */  
    @Column("vendorid")  
    private String VendorID;
    
    public String getVendorID() {  
        return VendorID;  
    }  
  
    public void setVendorID(String VendorID) {  
        this.VendorID = VendorID;  
    }  
    
    /** 
     * Vendor Count
     */  
    @Column("vendorcount")  
    private String VendorCount;
    
    public String getVendorCount() {  
        return VendorCount;  
    }  
  
    public void setVendorCount(String VendorCount) {  
        this.VendorCount = VendorCount;  
    }  
    
    /** 
     * Current status
     */  
    @Column("currentstatus")  
    private String CurrentStatus;
    
    public String getCurrentStatus() {  
        return CurrentStatus;  
    }  
  
    public void setCurrentStatus(String CurrentStatus) {  
        this.CurrentStatus = CurrentStatus;  
    }  
    
    /** 
     * Current action
     */  
    @Column("currentaction")  
    private String CurrentAction;
    
    public String getCurrentAction() {  
        return CurrentAction;  
    }  
  
    public void setCurrentAction(String CurrentAction) {  
        this.CurrentAction = CurrentAction;  
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
     * Opreation start time
     */  
    @Column("process_start")  
    private String Process_Start;
    
    public String getProcess_Start() {  
        return Process_Start;  
    }  
  
    public void setProcess_Start(String Process_Start) {  
        this.Process_Start = Process_Start;  
    }  
    
    /** 
     * Opreation end time
     */  
    @Column("process_end")  
    private String Process_End;
    
    public String getProcess_End() {  
        return Process_End;  
    }  
  
    public void setProcess_End(String Process_End) {  
        this.Process_End = Process_End;  

    }  
    
    /** 
     * Process_Count
     */  
    @Column("process_count")  
    private String Process_Count;
    
    public String getProcess_Count() {  
        return Process_Count;  
    }  
  
    public void setProcess_Count(String Process_Count) {  
        this.Process_Count = Process_Count;  
    }  
    
    /** 
     * Current Operation
     */  
    @Column("current_operation")  
    private String Current_Operation;
    
    public String getCurrent_Operation() {  
        return Current_Operation;  
    }  
  
    public void setCurrent_Operation(String Current_Operation) {  
        this.Current_Operation = Current_Operation;  
    }  
  
    @Override  
    public String toString() {  
        return "Container_ID: " + Container_ID + 
        		" Container_Type: " + Container_Type+
        		" Container_Status: " + Container_Status+
        		" Vendor_Name: " + Vendor_Name+
        		" StartTime: " + StartTime+
        		" EndTime: " + EndTime+
        		" Car_ID: " + Car_ID+
        		" Fab: " + Fab+
        		" Area: " + Area+
        		" Gate: " + Gate+
        		" Car_Type: " + Car_Type+
        		" ImpNo: " + ImpNo+
        		" Reason: " + Reason+
        		" DriverName: " + DriverName+
        		" DriverPhone: " + DriverPhone+
        		" VendorID: " + VendorID+
        		" VendorCount: " + VendorCount+
        		" CurrentStatus: " + CurrentStatus+
        		" CurrentAction: " + CurrentAction+
        		" Source: " + Source+
        		" Process_Start: " + Process_Start+
        		" Process_End: " + Process_End+
        		" Process_Count: " + Process_Count+
        		" Current_Operation: " + Current_Operation;
        		
    }  
}  



