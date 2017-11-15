package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("jn_t2_wms.wms_rfid_dock_operation_ctrl") // 表名
public class WMS_T2_Opreation_Mode {
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
   @Column("operation_type")  
   private String Operation_Type;
   
   public String getOperation_Type() {  
       return Operation_Type;  
   }  
 
   public void setOperation_Type(String Operation_Type) {  
       this.Operation_Type = Operation_Type;  
   }  
   
   /** 
    * Opreator
    */  
   @Column("operator")  
   private String Operator;
   
   public String getOperator() {  
       return Operator;  
   }  
 
   public void setOperator(String Operator) {  
       this.Operator = Operator;  
   }  
   
   @Override  
   public String toString() {  
       return " WH: " + WH+
       		" Area: " + Area+
       		" Gate_ID: " + Gate_ID+
       		" Operation_Type: " + Operation_Type+
       		" Operator: " + Operator;   		
   }  
}
