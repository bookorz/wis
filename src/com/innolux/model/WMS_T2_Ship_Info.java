package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("jn_t2_wms.v_t2_wms_container_status") // 表名
public class WMS_T2_Ship_Info {
	/** 
     * Container No
     */  
	@Column("container_no")  
    private String Container_NO;  
	
	public String getContainer_NO() {  
        return Container_NO;
    }  
  
    public void setContainer_NO(String Container_NO) {  
        this.Container_NO = Container_NO;  
    }  
    
    /** 
     * Truck NO
     */  
	@Column("truck_no")  
    private String Truck_NO;  
	
	public String getTruck_NO() {  
        return Truck_NO;  
    }  
  
    public void setTruck_NO(String Truck_NO) {  
        this.Truck_NO = Truck_NO;  
    }  
    
    /** 
     * Ship to
     */  
	@Column("ship_to")  
    private String Ship_To;  
	
	public String getShip_To() {  
        return Ship_To;  
    }  
  
    public void setShip_To(String Ship_To) {  
        this.Ship_To = Ship_To;  
    }  
    
    @Override  
    public String toString() {  
        return " Container_NO: " + Container_NO+
        		" Truck_NO: " + Truck_NO+
        		" Ship_To: " + Ship_To;   		
    }  
}
