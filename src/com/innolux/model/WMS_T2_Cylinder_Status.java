package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("jn_t2_wms.????") // 表名
public class WMS_T2_Cylinder_Status {

	@Id("Tag_ID")
	private String Tag_ID;
	
	@Column("Fab")
	private String Fab;
	
	@Column("Area")
	private String Area;
	
	@Column("Cylinder_Type")
	private String Cylinder_Type;
	
	@Column("Status")
	private String Status;
	
	@Column("part_no_desc")
	private String Part_No_Desc;
	
	@Column("expire_date")
	private String Expire_Date;
	
	@Column("batch")
	private String Batch;

	public String getTag_ID() {
		return Tag_ID;
	}

	public void setTag_ID(String tag_ID) {
		Tag_ID = tag_ID;
	}

	public String getFab() {
		return Fab;
	}

	public void setFab(String fab) {
		Fab = fab;
	}

	public String getArea() {
		return Area;
	}

	public void setArea(String area) {
		Area = area;
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
		return "WMS_T2_Cylinder_Status [Tag_ID=" + Tag_ID + ", Fab=" + Fab + ", Area=" + Area + ", Cylinder_Type="
				+ Cylinder_Type + ", Status=" + Status + ", Part_No_Desc=" + Part_No_Desc + ", Expire_Date="
				+ Expire_Date + ", Batch=" + Batch + "]";
	}

	
}
