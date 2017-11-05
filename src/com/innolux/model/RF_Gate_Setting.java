package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_gate_setting") // 表名
public class RF_Gate_Setting {

	/**
	 * ID
	 */
	@Id("id")
	private String ID;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
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
	 * For car binding to gate use
	 */
	@Column("mark_tag")
	private String Mark_Tag;

	public String getMark_Tag() {
		return Mark_Tag;
	}

	public void setMark_Tag(String Mark_Tag) {
		this.Mark_Tag = Mark_Tag;
	}

	/**
	 * For infrared trigger use
	 */
	@Column("use_ir")
	private String Use_IR;

	public String getUse_IR() {
		return Use_IR;
	}

	public void setUse_IR(String Use_IR) {
		this.Use_IR = Use_IR;
	}

	/**
	 * SignalTower Name Name
	 */
	@Column("signaltower_name")
	private String SignalTower_Name;

	public String getSignalTower_Name() {
		return SignalTower_Name;
	}

	public void setSignalTower_Name(String SignalTower_Name) {
		this.SignalTower_Name = SignalTower_Name;
	}

	/**
	 * Subtitle Name
	 */
	@Column("subtitle_name")
	private String Subtitle_Name;

	public String getSubtitle_Name() {
		return Subtitle_Name;
	}

	public void setSubtitle_Name(String Subtitle_Name) {
		this.Subtitle_Name = Subtitle_Name;
	}

	/**
	 * For car binding to gate use
	 */
	@Column("last_marktag_time")
	private long Last_MarkTag_Time;

	public long getLast_MarkTag_Time() {
		return Last_MarkTag_Time;
	}

	public void setLast_MarkTag_Time(long Last_MarkTag_Time) {
		this.Last_MarkTag_Time = Last_MarkTag_Time;
	}

	/**
	 * For car binding to gate use
	 */
	@Column("last_containertag_time")
	private long Last_ContainerTag_Time;

	public long getLast_ContainerTag_Time() {
		return Last_ContainerTag_Time;
	}

	public void setLast_ContainerTag_Time(long Last_ContainerTag_Time) {
		this.Last_ContainerTag_Time = Last_ContainerTag_Time;
	}

	/**
	 * For car binding to gate use
	 */
	@Column("voice_path")
	private String Voice_Path;

	public String getVoice_Path() {
		return Voice_Path;
	}

	public void setVoice_Path(String Voice_Path) {
		this.Voice_Path = Voice_Path;
	}

	/**
	 * IN / OUT For forklift use
	 */
	@Column("forklift_direction")
	private String ForkLift_Direction;

	public String getForkLift_Direction() {
		return ForkLift_Direction;
	}

	public void setForkLift_Direction(String ForkLift_Direction) {
		this.ForkLift_Direction = ForkLift_Direction;
	}

	/**
	 * For forklift use
	 */
	@Column("direction_starttime")
	private long Direction_StartTime;

	public long geDirection_StartTime() {
		return Direction_StartTime;
	}

	public void setDirection_StartTime(long Direction_StartTime) {
		this.Direction_StartTime = Direction_StartTime;
	}
	
	/**
	 * For forklift use
	 */
	@Column("direction_endtime")
	private long Direction_EndTime;

	public long geDirection_EndTime() {
		return Direction_EndTime;
	}

	public void setDirection_EndTime(long Direction_EndTime) {
		this.Direction_EndTime = Direction_EndTime;
	}

	@Override
	public String toString() {
		return "ID: " + ID + " Fab: " + Fab + " Area: " + Area + " Gate: " + Gate + " Mark_Tag: "
				+ Mark_Tag + " Use_IR: " + Use_IR + " SignalTower_Name: " + SignalTower_Name + " Subtitle_Name: "
				+ Subtitle_Name + " Last_MarkTag_Time: " + Last_MarkTag_Time + " Last_ContainerTag_Time: "
				+ Last_ContainerTag_Time + " Voice_Path: " + Voice_Path + " ForkLift_Direction: " + ForkLift_Direction
				+ " Direction_StartTime: " + Direction_StartTime+ " Direction_EndTime: " + Direction_EndTime;

	}
}
