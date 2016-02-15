package com.lixy.ftapi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

public class Util {
	private static Logger logger = LogManager.getLogger(Util.class);
	private static SimpleDateFormat dateForOrderIdFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // NOSONAR
	
	public static boolean isNullObject(Object object) {
		return object == null;
	}

	public static boolean isNullOrEmptyList(List<?> list) {

		return list == null || list.isEmpty();
	}

	public static boolean isNullOrEmptyHashMap(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isNullOrEmpty(String str) {

		return str == null || str.trim().isEmpty();
	}

	public static String getTrimmedString(String str, int len) {
		if (str == null)
			return "";

		return str.substring(0, len);
	}

	public Double getDouble(String inputString) {
		try {
			return Double.valueOf(inputString);
		} catch (Exception e) {
			logger.warn("getDouble error.", e);
			return null;
		}

	}

	public boolean numeric(String a, String b) {
		return a.equals(b);
	}

	public static String getNowWithFormat(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	public static Date getDateWithoutTime(Date date, int shift) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, shift);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static String getDateWithFormat(Date date, String format) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		} else {
			return null;
		}

	}

	public static Date convertStringToDateWithFormat(String date, String format) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			try {
				return formatter.parse(date);
			} catch (ParseException e) {
				return null;
			}
		} else {
			return null;
		}

	}

	public static String getInputLogs(String inputs, Object... input) {

		StringBuilder sb = new StringBuilder();
		String[] inputsArr = inputs.split(",");
		int inputsArrLength = inputsArr.length;
		int inputValLength = input.length;
		int minCtr = Math.min(inputsArrLength, inputValLength);

		sb.append("[");
		for (int i = 0; i < minCtr; i++) {
			if (input[i] == null) {
				sb.append(inputsArr[i] + "=" + "NVL");
			} else {
				sb.append(inputsArr[i] + "=" + input[i]);
			}

			if (i != minCtr - 1) {
				sb.append(",");
			}
		}
		sb.append("]");

		return sb.toString();
	}

	public static String getInputLogsSimple(String inputs, Object... input) {

		StringBuilder sb = new StringBuilder();
		String[] inputsArr = inputs.split(",");
		int inputsArrLength = inputsArr.length;
		int inputValLength = input.length;
		int minCtr = Math.min(inputsArrLength, inputValLength);

		for (int i = 0; i < minCtr; i++) {
			if (input[i] == null) {
				sb.append(inputsArr[i] + ":" + "NVL");
			} else {
				sb.append(inputsArr[i] + ":" + input[i]);
			}

			if (i != minCtr - 1) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public static String getListString(List<?> list) {
		StringBuilder sb = new StringBuilder();
		if (Util.isNullObject(list))
			return sb.toString();

		sb.append("[");
		for (Object object : list) {
			try {
				sb.append(object.toString());
			} catch (Exception ex) {
				logger.info("String builder error", ex);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static BigDecimal getZeroIfNull(BigDecimal object) {
		if (isNullObject(object))
			return BigDecimal.ZERO;
		else
			return object;
	}

	public static String toch(BigDecimal amount) {
		if (amount != null)
			return new DecimalFormat("#,###,###,##0.00").format(amount);
		return "";
	}

	public static String getUniqueId() {
		String dateForTransaction = dateForOrderIdFormatter.format(new Date());
		return "310633" + dateForTransaction + String.valueOf(Math.random()).substring(2, 9);
	}
	
	public static String getSimpleUniqueId() {
		String dateForTransaction = dateForOrderIdFormatter.format(new Date());
		return dateForTransaction + String.valueOf(Math.random()).substring(2, 3);
	}
	
	public static Long getSimpleNumberUniqueId() {
		String dateForTransaction = dateForOrderIdFormatter.format(new Date());
		return Long.valueOf(dateForTransaction + String.valueOf(Math.random()).substring(2, 3));
	}

	public static java.sql.Date getSQLDate(Date javaDate) {
		if (javaDate != null)
			return new java.sql.Date(javaDate.getTime());
		else
			return null;
	}

	public static String convertToRandomSql(String sql, Long maxRowCount) {
		String sqlLast;

		sql = sql.toUpperCase(); // NOSONAR

		if (sql.contains("DUAL")) {
			sqlLast = sql + " " + "connect by level <= " + maxRowCount;
		} else {

			sqlLast = "SELECT * " + "  FROM (  SELECT * " + "            FROM (" + sql + ") " + "           WHERE ROWNUM < 10000 " + "        ORDER BY DBMS_RANDOM.random) ";

			if (!Util.isNullObject(maxRowCount))
				sqlLast += " WHERE ROWNUM < " + (maxRowCount + 1);
			else
				sqlLast = " WHERE ROWNUM = 1";
		}

		return sqlLast;

	}

	public static String convertToSingleLine(String line) {
		return line == null ? "" : line.replace(System.getProperty("line.separator"), " ");
	}
	
	
	public static String getTimeDescription(Date dt1, Date dt2){
		Period period = new Period(dt1.getTime(), dt2.getTime());		
		return PeriodFormat.getDefault().print(period);
	}
	
	public static String getTimeDescription(long milisec){
		Period period = new Period(milisec);		
		return PeriodFormat.getDefault().print(period);
	}

	public static String getRepeatedChar(String cha, int count) {
		String x = new String(cha);
		for(int i = 0; i < count; i++){
			cha += x; // NOSONAR
		}
		return cha;
	}
	
	public static Object fromByteArray(byte[] yourBytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return in.readObject();
		} catch (IOException e) {
			logger.error("from byte array IOException.", e);
		} catch (ClassNotFoundException e) {
			logger.error("from byte array ClassNotFoundException.", e);
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				logger.error("from byte array ex.", ex);
			}
			try {
				if (!Util.isNullObject(in)) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("from byte array ex.", ex);
			}
		}

		return null;
	}
	
	public static String getExtensionFromName(String name){
		return name.split("\\.")[1];
	}
	
	public static String nvl(String t, String n){
		return isNullOrEmpty(t) ? n : t;
	}

}
