package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_error_pallet") // 表名
public class RF_Error_Pallet {

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
	 * Pallet ID
	 */
	@Column("pallet_id")
	private String Pallet_ID;

	public String getPallet_ID() {
		return Pallet_ID;
	}

	public void setPallet_ID(String Pallet_ID) {
		this.Pallet_ID = Pallet_ID;
	}

	/**
	 * Reason
	 */
	@Column("reason")
	private String Reason;

	public String getReason() {
		return Reason;
	}

	public void setReason(String Reason) {
		this.Reason = Reason;
	}

	/**
	 * Opreation Mode
	 */
	@Column("opreation_mode")
	private String Opreation_Mode;

	public String getOpreation_Mode() {
		return Opreation_Mode;
	}

	public void setOpreation_Mode(String Opreation_Mode) {
		this.Opreation_Mode = Opreation_Mode;
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
		String result = "";
    	if(TimeStamp!=null) {
    		result = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeStamp);
    	}
    	return result;
		
	}

	@Override
	public String toString() {
		return "ID: " + ID + " Fab: " + Fab + " Area: " + Area + " Gate: " + Gate + " Pallet_ID: " + Pallet_ID + " Reason: "
				+ Reason + " Opreation_Mode: " + Opreation_Mode + " TimeStamp: " + getTimeStampString();
	}
}
