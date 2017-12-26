package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("rf_tag_setting") // 表名
public class RF_Tag_Setting {
	/**
	 * Setting ID
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
	 * Tag Type
	 */
	@Column("tag_type")
	private String Tag_Type;

	public String getTag_Type() {
		return Tag_Type;
	}

	public void setTag_Type(String Tag_Type) {
		this.Tag_Type = Tag_Type;
		
	}

	/**
	 * Name
	 */
	@Column("name")
	private String Name;

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
		
	}

	/**
	 * Value
	 */
	@Column("value")
	private String Value;

	public String getValue() {
		return Value;
	}

	public void setValue(String Value) {
		this.Value = Value;
		
	}
	
	

	@Override  
    public String toString() {  
        return " Setting_ID: " + Setting_ID+
        		" Tag_Type: " + Tag_Type+
        		" Name: " + Name+
        		" Value: " + Value;
        		
    }  
}
