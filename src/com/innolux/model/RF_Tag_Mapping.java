package com.innolux.model;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;

@Entity("rf_tag_mapping") // 表名
public class RF_Tag_Mapping {

	/**
	 * tag id
	 */
	@Id("tag_id")
	private String tag_id;

	public String getTag_id() {
		return tag_id;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	/**
	 * real id
	 */
	@Column("real_id")
	private String real_id;

	public String getReal_id() {
		return real_id;
	}

	public void setReal_id(String real_id) {
		this.real_id = real_id;
	}

	@Override
	public String toString() {
		return "tag_id: " + tag_id + " real_id: " + real_id;
	}
}