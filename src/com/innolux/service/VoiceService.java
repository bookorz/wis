package com.innolux.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.innolux.common.ToolUtility;

public class VoiceService {
	public static Logger logger = Logger.getLogger(VoiceService.class);

	public static boolean Send(String path, String Text) {
		boolean result = false;
		logger.debug(" Send to VoiceService: path=" + path + " text=" + Text);
		if (!path.equals("")) {

			path += System.currentTimeMillis() + ".txt";

			
			PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				out.println(Text);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(" Send to VoiceService Error: path=" + path + " text=" + Text + " error msg="
						+ ToolUtility.StackTrace2String(e));

			}
			result = true;
		}
		return result;
	}
}
