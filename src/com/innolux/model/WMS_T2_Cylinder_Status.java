package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("jn_t2_wms.????") // 表名
public class WMS_T2_Cylinder_Status {

	@Id("Tag_ID")
	private String Tag_ID;
	
	@Column("Cylinder_Type")
	private String Cylinder_Type;
	
	@Column("Status")
	private String Status;

	public String getTag_ID() {
		return Tag_ID;
	}

	public void setTag_ID(String tag_ID) {
		Tag_ID = tag_ID;
	}

	public String getCylinder_Type() {
		return Cylinder_Type;
	}

	public void setCylinder_Type(String cylinder_Type) {
		Cylinder_Type = cylinder_Type;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
	
	/**
	 * Status_ChangeTime
	 */
	@Column("Status_ChangeTime")
	private String Status_ChangeTime;

	public String getStatus_ChangeTime() {
		return Status_ChangeTime;
	}

	public void setStatus_ChangeTime(String Status_ChangeTime) {
		this.Status_ChangeTime = Status_ChangeTime;
	}

	@Override
	public String toString() {
		return "WMS_T2_Cylinder_Status [Tag_ID=" + Tag_ID + ", Cylinder_Type=" + Cylinder_Type + ", Status=" + Status
				+ ", Status_ChangeTime=" + Status_ChangeTime + "]";
	}

	

	
	
}
