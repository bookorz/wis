package com.innolux.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.RF_Tag_Mapping;

public class TagParser {

	public Logger logger = Logger.getLogger(TagParser.class);


	public List<RF_Tag_History> Parse(String Raw_Str, Hashtable<Integer, RF_Antenna_Setting> antSetting) {
		List<RF_Tag_History> result = new ArrayList<RF_Tag_History>();
		String[] TagListStr = Raw_Str.split("\n");
		try {
			for (String each : TagListStr) {
				String[] Attributes = each.split(",");
				if (Attributes.length >= 6) {
					// TagBase eachTag = new TagBase();
					RF_Tag_History tag = new RF_Tag_History();
					RF_Antenna_Setting ant;
					// ${TAGIDW},${MSEC1},${MSEC2},${TX},${COUNT},${RSSI_MAX}
					// eachTag.setTagID(Attributes[0]);
					// eachTag.setDiscoverTime(Attributes[1]);
					// eachTag.setRenewTime(Attributes[2]);
					// eachTag.setReceiveAntenna(Attributes[3]);
					// eachTag.setRenewCount(Attributes[4]);
					// eachTag.setRSSI(Attributes[5]);
					tag.setAntenna_No(Integer.parseInt(Attributes[3]));
					ant = antSetting.get(tag.getAntenna_No());

					tag.setFab(ant.getFab());
					tag.setArea(ant.getArea());
					tag.setGate(ant.getGate());
					tag.setAntenna_Type(ant.getAntenna_Type());
					
					tag.setReader_IP(ant.getReader_IP());
					tag.setRawData(Attributes[0]);
					tag.setTag_ID(Ascii2Alphabet(tag.getRawData()));
					tag.setTag_Type(tag.getTag_ID().substring(0, 1));
					tag.setTag_ID(tag.getTag_ID().substring(1));

					if (tag.getTag_Type().equals(GlobleVar.TruckTag)
							|| tag.getTag_Type().equals(GlobleVar.ContainerTag)) {
						RF_Tag_Mapping tagMapping = ToolUtility.GetTagMapping(tag);
						if (tagMapping != null) {
							tag.setTag_ID(tagMapping.getReal_id());

						} else {
							logger.debug(tag.getReader_IP() + " TagParser : This tag is not in TagMapping table.");
						}
					}

					tag.setCount(Integer.parseInt(Attributes[4]));
					tag.setDiscover_Time(Long.parseLong(Attributes[1]));
					//tag.setReceive_Time(Long.parseLong(Attributes[2]));
					tag.setReceive_Time(System.currentTimeMillis());
					tag.setRSSI(Attributes[5]);
					tag.setTimeStamp(Calendar.getInstance().getTime());

					result.add(tag);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block

			logger.error("Parse message error: " + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	private String Ascii2Alphabet(String hexString) {
		String[] hexTagAry = hexString.split(" ");
		String TagStr = "";
		for (int k = 0; k < hexTagAry.length; k++) { // 16進制Ascii轉字母
			int decStr1 = Integer.parseInt(hexTagAry[k].substring(0, 2), 16);
			int decStr2 = Integer.parseInt(hexTagAry[k].substring(2, 4), 16);

			String aChar1 = new Character((char) decStr1).toString();
			String aChar2 = new Character((char) decStr2).toString();

			TagStr = TagStr + aChar1 + aChar2;

		}
		return TagStr.trim();
	}

}
