package com.innolux.receiver;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.io.*;
import java.util.jar.JarException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.innolux.common.IRHandle;
import com.innolux.common.ToolUtility;
import com.innolux.common.base.IR_MessageBase;
import com.innolux.common.base.ResponseBase;



@Path("/DISWebService")
public class WebApiController {
	public static Logger logger =Logger.getLogger(WebApiController.class); 
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}

	@POST
	@Path("setcaption")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String setCaption(InputStream is) throws JarException {
		StringBuffer buffer = null;
		if (is != null) {
			buffer = new StringBuffer();
			try {
				// InputStreamReader isr = ;
				Reader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				in.close();
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
		
		
		
		
		return new JSONObject().put("statuscode", 200).toString();
		
	}
	@POST
	@Path("setGate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String setGate(InputStream is) throws JarException {
		StringBuffer buffer = null;
		if (is != null) {
			buffer = new StringBuffer();
			try {
				// InputStreamReader isr = ;
				Reader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				in.close();
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
		
		
		
		
		return new JSONObject().put("statuscode", 200).toString();
		
	}
	@POST
	@Path("IR")
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String IR(InputStream is) throws JarException {
		StringBuffer buffer = null;
		if (is != null) {
			buffer = new StringBuffer();
			try {
				// InputStreamReader isr = ;
				Reader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				in.close();
			} catch (IOException e) {
				new JSONObject().put("statuscode", 500).put("Message", ToolUtility.StackTrace2String(e)).toString();
			}
		} else {
			new JSONObject().put("statuscode", 500).put("Message", "content is null").toString();
		}
		IR_MessageBase irMsg = ToolUtility.Parse_T1_IR(buffer.toString());
		
		ResponseBase<String> response = IRHandle.Data(irMsg);
		
		return new JSONObject().put("statuscode", response.getStatus()).put("Message", response.getMessage()).toString();
		
	}
	
	@POST
	@Path("T2IR")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String T2IR(InputStream is) throws JarException {
		StringBuffer buffer = null;
		if (is != null) {
			buffer = new StringBuffer();
			try {
				// InputStreamReader isr = ;
				Reader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				in.close();
			} catch (IOException e) {
				new JSONObject().put("statuscode", 500).put("Message", ToolUtility.StackTrace2String(e)).toString();
			}
		} else {
			new JSONObject().put("statuscode", 500).put("Message", "content is null").toString();
		}
		
		IR_MessageBase irMsg = ToolUtility.Parse_T2_IR(buffer.toString());
		
		ResponseBase<String> response = IRHandle.Data(irMsg);
		
		return new JSONObject().put("statuscode", response.getStatus()).put("Message", response.getMessage()).toString();
		
	}
}
