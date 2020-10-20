package com.nets.nps.paynow.utils;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAdjusters;

import com.nets.upos.commons.logger.ApsLogger;

public class PaynowUtil {
	private static final ApsLogger logger = new ApsLogger(PaynowUtil.class);

	public static Timestamp getSettlementDate(LocalTime cutoffTime) {
		DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("MMdd").toFormatter();
		SimpleDateFormat settlementDateFormat = new SimpleDateFormat("yyyyMMdd");
		DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
		String settlementDateStr;

		LocalTime localTime = LocalTime.now();
		if (localTime.compareTo(cutoffTime) <= 0) {
			settlementDateStr = dateFormat.format(LocalDate.now());
		} else {
			settlementDateStr = dateFormat.format(LocalDate.now().plusDays(1));
		}

		if (settlementDateStr != null) {
			LocalDate currentDate = LocalDate.now();
			LocalDate settlementDate = LocalDate.parse(currentDate.getYear() + settlementDateStr, DATE_TIME_FORMATTER);

			if (currentDate.equals(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()))
					&& settlementDate.equals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))) {
				try {
					return (new Timestamp(
							(settlementDateFormat.parse((currentDate.getYear() + 1) + settlementDateStr)).getTime()));
				} catch (ParseException e) {
					logger.error("Error Parsing Settlement Date:" + settlementDateStr);
				}
			} else {
				try {
					return (new Timestamp(
							(settlementDateFormat.parse((currentDate.getYear()) + settlementDateStr)).getTime()));
				} catch (ParseException e) {
					logger.error("Error Parsing Settlement Date:" + settlementDateStr);
				}

			}
		} else {
			return (new Timestamp(System.currentTimeMillis()));
		}
		return (new Timestamp(System.currentTimeMillis()));

	}

	public static String getSettlementDate(Timestamp timestamp) {
		return new SimpleDateFormat("yyyyMMdd").format(timestamp);
	}

	public static String getTandemSettlementDate(Timestamp timestamp) {
		return new SimpleDateFormat("MMdd").format(timestamp);
	}

	public static String hashSHA2String(String text) throws Exception {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
		md.update(text.getBytes());
		byte[] sha2hash = md.digest();
		return convertByteArrToHexString(sha2hash);
	}

	public static String convertByteArrToHexString(byte[] data) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			hexString.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
		}
		return hexString.toString();
	}

}
