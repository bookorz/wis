package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_antenna_setting") // 表名
public class RF_Antenna_Setting {
	
	@Id("id")
	private String ID;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
	
	/**
	 * Reader IP
	 */
	@Column("reader_ip")
	private String Reader_IP;

	public String getReader_IP() {
		return Reader_IP;
	}

	public void setReader_IP(String Reader_IP) {
		this.Reader_IP = Reader_IP;
	}

	/**
	 * Reader's antenna no
	 */
	@Column("antenna_no")
	private int Antenna_No;

	public int getAntenna_No() {
		return Antenna_No;
	}

	public void setAntenna_No(int Antenna_No) {
		this.Antenna_No = Antenna_No;
	}

	/**
	 * Reader's antenna type
	 */
	@Column("antenna_type")
	private String Antenna_Type;

	public String getAntenna_Type() {
		return Antenna_Type;
	}

	public void setAntenna_Type(String Antenna_Type) {
		this.Antenna_Type = Antenna_Type;
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
	 * Setting_ID
	 */
	@Column("setting_id")
	private String Setting_ID;

	public String getSetting_ID() {
		return Setting_ID;
	}

	public void setSetting_ID(String Setting_ID) {
		this.Setting_ID = Setting_ID;
	}

	/**
	 * Antenna power
	 */
	@Column("rfattenuation")
	private int RFAttenuation;

	public int getRFAttenuation() {
		return RFAttenuation;
	}

	public void setRFAttenuation(int RFAttenuation) {
		this.RFAttenuation = RFAttenuation;
	}

	/**
	 * Active
	 */
	@Column("active")
	private boolean Active;

	public boolean getActive() {
		return Active;
	}

	public void setActive(boolean Active) {
		this.Active = Active;
	}
	
	

	@Override
	public String toString() {
		return "ID: " + ID +" Reader_IP: " + Reader_IP + " Antenna_No: " + Antenna_No + " Antenna_Type: " + Antenna_Type + " Fab: "
				+ Fab + " Area: " + Area + " Gate: " + Gate + " Setting_ID: " + Setting_ID + " RFAttenuation: "
				+ RFAttenuation + " Active: " + Active;

	}
}
