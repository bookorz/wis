package com.innolux.receiver;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alien.enterpriseRFID.notify.Message;
import com.alien.enterpriseRFID.notify.MessageListener;
import com.alien.enterpriseRFID.notify.MessageListenerService;
import com.innolux.common.GlobleVar;
import com.innolux.common.Port;
import com.innolux.common.TagParser;
import com.innolux.common.ToolUtility;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Tag_History;


public class AlienReader implements MessageListener{

	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();
	private RF_Reader_Setting setting;
	private Hashtable<Integer,RF_Antenna_Setting> antSetting = new Hashtable<Integer,RF_Antenna_Setting>();
	
	public AlienReader(RF_Reader_Setting _setting){
		setting = _setting;
		Initial();
		
		MessageListenerService service = new MessageListenerService(setting.getListen_Port());

		service.setMessageListener(this);
		try {
			service.startService();
		} catch (Exception e) {
		
			logger.error(setting.getReader_IP() + " " + "Exception:" + tools.StackTrace2String(e));
		}
		
		
	}
	
	@Override
	public void messageReceived(Message message) {
		String MessageRawData;
		MessageRawData = message.getRawData();
		logger.debug(setting.getReader_IP() + " " + MessageRawData);
		List<RF_Tag_History> tagList = new TagParser().Parse(MessageRawData, antSetting);

		if (tagList.size() != 0) {
			new Port().DataHandle(tagList, antSetting);
		}
		
	}

	private void Initial(){
		GenericDao<RF_Antenna_Setting> antenna_Setting_Dao = new JdbcGenericDaoImpl<RF_Antenna_Setting>(GlobleVar.WIS_DB);  

	    Map<String,Object> sqlWhereMap = new HashMap<String, Object>(); 
        sqlWhereMap.put("reader_ip", setting.getReader_IP());  
        
        try {
			List<RF_Antenna_Setting> ants = antenna_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Antenna_Setting.class);
			for(RF_Antenna_Setting eachAnt : ants){
				if(!antSetting.containsKey(eachAnt.getAntenna_No())){
					antSetting.put(eachAnt.getAntenna_No(), eachAnt);
				}
			}
			
		} catch (Exception e) {
			logger.error(setting.getReader_IP() + " " + "Exception:" + tools.StackTrace2String(e));
		}
	}
	
	

}
