package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_subtitle_setting")   //表名
public class RF_Subtitle_Setting {
	/**
	 * Subtitle Name
	 */
	@Id("subtitle_name")
	private String Subtitle_Name;

	public String getSubtitle_Name() {
		return Subtitle_Name;
	}

	public void setSubtitle_Name(String Subtitle_Name) {
		this.Subtitle_Name = Subtitle_Name;
	}

	/**
	 * Subtitle IP
	 */
	@Column("subtitle_ip")
	private String Subtitle_IP;

	public String getSubtitle_IP() {
		return Subtitle_IP;
	}

	public void setSubtitle_IP(String Subtitle_IP) {
		this.Subtitle_IP = Subtitle_IP;
	}

	/**
	 * Current Subtitle
	 */
	@Column("current_subtitle")
	private String Current_Subtitle;

	public String getCurrent_Subtitle() {
		return Current_Subtitle;
	}

	public void setCurrent_Subtitle(String Current_Subtitle) {
		this.Current_Subtitle = Current_Subtitle;
	}

	@Override
	public String toString() {
		return "Subtitle_Name: " + Subtitle_Name + " Subtitle_IP: " + Subtitle_IP + " Current_Subtitle: "
				+ Current_Subtitle;

	}
}
