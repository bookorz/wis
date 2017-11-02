package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_gate_error") // 表名
public class RF_Gate_Error {

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
	 * Error Type
	 */
	@Column("error_type")
	private String Error_Type;

	public String getError_Type() {
		return Error_Type;
	}

	public void setError_Type(String Error_Type) {
		this.Error_Type = Error_Type;
	}

	/**
	 * Error Message
	 */
	@Column("error_message")
	private String Error_Message;

	public String getError_Message() {
		return Error_Message;
	}

	public void setError_Message(String Error_Message) {
		this.Error_Message = Error_Message;
	}

	/**
	 * TimeStamp
	 */
	@Column("timestamp")
	private Date TimeStamp;

	public Date getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(Date TimeStamp) {
		this.TimeStamp = TimeStamp;
	}

	public String getTimeStampString() {

		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeStamp);
	}

	@Override
	public String toString() {
		return "ID: " + ID + "Fab: " + Fab + " Area: " + Area + " Gate: " + Gate + " Error_Type: "
				+ Error_Type + " Error_Message: " + Error_Message + " TimeStamp: " + getTimeStampString();

	}
}
