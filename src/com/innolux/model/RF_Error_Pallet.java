package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_error_pallet") // 表名
public class RF_Error_Pallet {

	@Id("error_id")
	private String Error_ID;

	public String getError_ID() {
		return Error_ID;
	}

	public void setError_ID(String Error_ID) {
		this.Error_ID = Error_ID;
	}

	/**
	 * Container ID
	 */
	@Column("container_id")
	private String Container_ID;

	public String getContainer_ID() {
		return Container_ID;
	}

	public void setContainer_ID(String Container_ID) {
		this.Container_ID = Container_ID;
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

		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeStamp);
	}

	@Override
	public String toString() {
		return "Error_ID: " + Error_ID + "Container_ID: " + Container_ID + " Pallet_ID: " + Pallet_ID + " Reason: "
				+ Reason + " Opreation_Mode: " + Opreation_Mode + " TimeStamp: " + getTimeStampString();
	}
}
