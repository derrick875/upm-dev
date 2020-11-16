package com.nets.nps.paynow.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.nets.upos.core.entity.SysTrace;

public class UtilComponents {

	public static final String UPI_TIME_FORMAT = "yyyyMMddHHmmss";
	public static final String UPI_SUFFIX = "1";

	public static String getStringFromObject(Object obz) {
		Gson gson = new Gson();
		return gson.toJson(obz);
	}

	public static Object getObjectFromString(String response, Class<?> clazz){
		Gson gson = new Gson();
		return gson.fromJson(response, clazz);
	}

	public static String getTraceNum() {
		return addZero(String.valueOf(SysTrace.getInstance().getAndIncrement()),6);
	}

	public static String addZero(String orgStr, int len) {
		if (orgStr != null && !"".equals(orgStr)) {
			int orgStrLen = orgStr.length();
			for (int i = 0; i < len - orgStrLen; i++) {
				orgStr = "0" + orgStr;
			}
		}
		return orgStr;
	}

	public static String getMsgId(String insID) {
		String msgId = "A" +insID + getNowUPIFormatDate() + getTraceNum()+UPI_SUFFIX;
		return msgId;
	}

	public static String getNowUPIFormatDate() {

		SimpleDateFormat sdf = new SimpleDateFormat(UPI_TIME_FORMAT);
		String date = sdf.format(new Date());
		return date;
	}
}
