package com.innolux.common.base;

public class IR_MessageBase {
	private String Fab;

	public String getFab() {
		return Fab;
	}

	public void setFab(String Fab) {
		this.Fab = Fab;
	}

	private String Area;

	public String getArea() {
		return Area;
	}

	public void setArea(String Area) {
		this.Area = Area;
	}

	private String Gate;

	public String getGate() {
		return Gate;
	}

	public void setGate(String Gate) {
		this.Gate = Gate;
	}

	private String Type;

	public String getType() {
		return Type;
	}

	public void setType(String Type) {
		this.Type = Type;
	}

	private String SensorNo;

	public String getSensorNo() {
		return SensorNo;
	}

	public void setSensorNo(String SensorNo) {
		this.SensorNo = SensorNo;
	}

	private String Status;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	private String Direction;

	public String getDirection() {
		return Direction;
	}

	public void setDirection(String Direction) {
		this.Direction = Direction;
	}

	private long TimeStamp;

	public long getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(long TimeStamp) {
		this.TimeStamp = TimeStamp;
	}

	@Override
	public String toString() {
		return " Fab: " + Fab + " Area: " + Area + " Gate: " + Gate + " Type: " + Type + " SensorNo: " + SensorNo
				+ " Status: " + Status + " Direction: " + Direction + " TimeStamp: " + TimeStamp;
	}
}
