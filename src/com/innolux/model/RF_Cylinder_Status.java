package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_cylinder_status") // 表名
public class RF_Cylinder_Status {
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
	 * Tag ID
	 */
	@Id("tag_id")
	private String Tag_ID;

	public String getTag_ID() {
		return Tag_ID;
	}

	public void setTag_ID(String Tag_ID) {
		this.Tag_ID = Tag_ID;
	}

	/**
	 * Position
	 */
	@Column("position")
	private String Position;

	public String getPosition() {
		return Position;
	}

	public void setPosition(String Position) {
		this.Position = Position;
	}

	/**
	 * Update Time
	 */
	@Column("updatetime")
	private long UpdateTime;

	public long getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(long UpdateTime) {
		this.UpdateTime = UpdateTime;
	}

	/**
	 * Check Times
	 */
	@Column("check_times")
	private int Check_Times;

	public int getCheck_Times() {
		return Check_Times;
	}

	public void setCheck_Times(int Check_Times) {
		this.Check_Times = Check_Times;
	}

	/**
	 * Cylinder Type
	 */
	@Column("cylinder_type")
	private String Cylinder_Type;

	public String getCylinder_Type() {
		return Cylinder_Type;
	}

	public void setCylinder_Type(String Cylinder_Type) {
		this.Cylinder_Type = Cylinder_Type;
	}

	/**
	 * New Position
	 */
	@Column("new_position")
	private String New_Position;

	public String getNew_Position() {
		return New_Position;
	}

	public void setNew_Position(String New_Position) {
		this.New_Position = New_Position;
	}

	/**
	 * Status
	 */
	@Column("status")
	private String Status;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
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
		return "RF_Cylinder_Status [Fab=" + Fab + ", Area=" + Area + ", Tag_ID=" + Tag_ID + ", Position=" + Position
				+ ", UpdateTime=" + UpdateTime + ", Check_Times=" + Check_Times + ", Cylinder_Type=" + Cylinder_Type
				+ ", New_Position=" + New_Position + ", Status=" + Status + ", Status_ChangeTime=" + Status_ChangeTime
				+ "]";
	}

}
