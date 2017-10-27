package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_ext_setting") // 表名
public class RF_Ext_Setting {
	/**
	 * Ext ID
	 */
	@Id("ext_id")
	private String Ext_ID;
	
	public String getExt_ID() {  
        return Ext_ID;  
    }  
  
    public void setExt_ID(String Ext_ID) {  
        this.Ext_ID = Ext_ID;  
    }  

	/**
	 * Value Type
	 */
	@Column("value_type")
	private String Value_Type;

	public String getValue_Type() {
		return Value_Type;
	}

	public void setValue_Type(String Value_Type) {
		this.Value_Type = Value_Type;
		
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
        return " Ext_ID: " + Ext_ID+
        		" Value_Type: " + Value_Type+
        		" Name: " + Name+
        		" Value: " + Value;
        		
    }  
}
