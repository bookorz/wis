package com.innolux.model;

import java.util.Date;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;

@Entity("rf_tag_history") // 表名
public class RF_Tag_History {
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
	 * Tag ID
	 */
	@Column("tag_id")
	private String Tag_ID;

	public String getTag_ID() {
		return Tag_ID;
	}

	public void setTag_ID(String Tag_ID) {
		this.Tag_ID = Tag_ID;
	}

	/**
	 * Reader report this tag's read count
	 */
	@Column("count")
	private String Count;

	public String getCount() {
		return Count;
	}

	public void setCount(String Count) {
		this.Count = Count;
	}

	/**
	 * Tag type
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
	 * RSSI
	 */
	@Column("rssi")
	private String RSSI;

	public String getRSSI() {
		return RSSI;
	}

	public void setRSSI(String RSSI) {
		this.RSSI = RSSI;
	}

	/**
	 * RawData
	 */
	@Column("rawdata")
	private String RawData;

	public String getRawData() {
		return RawData;
	}

	public void setRawData(String RawData) {
		this.RawData = RawData;
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
	 * Discover Time
	 */
	@Column("discover_time")
	private long Discover_Time;
	
	public long getDiscover_Time() {  
        return Discover_Time;  
    }  
  
    public void setDiscover_Time(long Discover_Time) {  
        this.Discover_Time = Discover_Time;  
    }  
    
    /**
	 * Receive Time
	 */
	@Column("receive_time")
	private long Receive_Time;
	
	public long getReceive_Time() {  
        return Receive_Time;  
    }  
  
    public void setReceive_Time(long Receive_Time) {  
        this.Receive_Time = Receive_Time;  
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
    
    @Override  
    public String toString() {  
        return " Fab: " + Fab+
        		" Area: " + Area+
        		" Gate: " + Gate+
        		" Reader_IP: " + Reader_IP+     		
        		" Tag_ID: " + Tag_ID+
        		" Count: " + Count+
        		" Tag_Type: " + Tag_Type+
        		" RSSI: " + RSSI+
        		" RawData: " + RawData+
        		" Antenna_Type: " + Antenna_Type+
        		" Discover_Time: " + Discover_Time+
        		" Receive_Time: " + Receive_Time+
        		" TimeStamp: " + TimeStamp;   		
    }  
}
