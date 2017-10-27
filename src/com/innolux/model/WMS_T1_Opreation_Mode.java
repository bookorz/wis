package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("jn_t1_wms.wms_rfid_dock_operation_ctrl") // 表名
public class WMS_T1_Opreation_Mode {
	/** 
    * Fab (T1 T2)
    */  
   @Column("wh")  
   private String WH;  
   
   public String getWH() {  
       return WH;  
   }  
 
   public void setWH(String WH) {  
       this.WH = WH;  
   }  
   
   /** 
    * Area (Receive, Delivery)
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
   @Column("gate_id")  
   private String Gate_ID;
   
   public String getGate_ID() {  
       return Gate_ID;  
   }  
 
   public void setGate_ID(String Gate_ID) {  
       this.Gate_ID = Gate_ID;  
   }  
   
   /** 
    * Opreation Mode
    */  
   @Column("opreation_type")  
   private String Opreation_Type;
   
   public String getOpreation_Type() {  
       return Opreation_Type;  
   }  
 
   public void setOpreation_Type(String Opreation_Type) {  
       this.Opreation_Type = Opreation_Type;  
   }  
   
   /** 
    * Opreator
    */  
   @Column("opreator")  
   private String Opreator;
   
   public String getOpreator() {  
       return Opreator;  
   }  
 
   public void setOpreator(String Opreator) {  
       this.Opreator = Opreator;  
   }  
   
   @Override  
   public String toString() {  
       return " WH: " + WH+
       		" Area: " + Area+
       		" Gate_ID: " + Gate_ID+
       		" Opreation_Type: " + Opreation_Type+
       		" Opreator: " + Opreator;   		
   }  
}
