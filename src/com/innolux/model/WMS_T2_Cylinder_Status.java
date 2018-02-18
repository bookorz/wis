package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("jn_t2_wms.????") // 表名
public class WMS_T2_Cylinder_Status {
	@Column("pallet_id?")
	private String Pallet_ID;
	
	@Column("status?")
	private String Status;
	
	@Column("part_no_desc?")
	private String Part_No_Desc;
	
	@Column("expire_date?")
	private String Expire_Date;
	
	@Column("batch?")
	private String Batch;

	public String getPallet_ID() {
		return Pallet_ID;
	}

	public void setPallet_ID(String pallet_ID) {
		Pallet_ID = pallet_ID;
	}

	

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getPart_No_Desc() {
		return Part_No_Desc;
	}

	public void setPart_No_Desc(String part_No_Desc) {
		Part_No_Desc = part_No_Desc;
	}

	public String getExpire_Date() {
		return Expire_Date;
	}

	public void setExpire_Date(String expire_Date) {
		Expire_Date = expire_Date;
	}

	public String getBatch() {
		return Batch;
	}

	public void setBatch(String batch) {
		Batch = batch;
	}

	@Override
	public String toString() {
		return "WMS_T2_Cylinder_Status [Pallet_ID=" + Pallet_ID + ", Status=" + Status + ", Part_No_Desc="
				+ Part_No_Desc + ", Expire_Date=" + Expire_Date + ", Batch=" + Batch + "]";
	}

	

	
	
}
