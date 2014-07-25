/**
 * $RCSfile: StringUtils.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2007/03/01 00:17:45 $
 *
 * Copyright (C) 2000 CoolServlets.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        CoolServlets.com (http://www.Yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@Yasna.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of CoolServlets.com. For more information
 * on CoolServlets.com, please see <http://www.Yasna.com>.
 */
package com.mooo.mycoz.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class CalendarUtils {

	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

	public static final String YMDHM = "yyyy-MM-dd HH:mm";

	public static final String NYMDHM = "yyyyMMddHHmm";

	public static final String YMDH = "yyyy-MM-dd HH";

	public static final String NYMDH = "yyyyMMddHH";

	public static final String YMD = "yyyy-MM-dd";

	public static final String NYMD = "yyyyMMdd";

	public static String dtformat(Date date,String sformat){
		return new SimpleDateFormat(sformat).format(date);
	}

	public static Date dtparse(String dateValue,String sformat) throws ParseException{
		return new SimpleDateFormat(sformat).parse(dateValue);
	}

	//日期偏移量
	public static String dtoffset(String dtbegin,String dtformat,
				int yoffset,int moffset,int doffset,int hoffset) {
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(dtparse(dtbegin,dtformat));

			now.add(Calendar.YEAR, yoffset);
			now.add(Calendar.MONTH, moffset);
			now.add(Calendar.DAY_OF_MONTH, doffset);
			now.add(Calendar.HOUR_OF_DAY, hoffset);

			return dtformat(now.getTime(),dtformat);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String dtformat(Date date){
		return dtformat(date,YMDHMS);
	}

	public static Date dtparse(String dateValue) throws ParseException{
		return dtparse(dateValue,YMDHMS);
	}
	
	public static String dtformat2(Date date){
		return dtformat(date,YMDHM);
	}
	public static Date dtparse2(String dateValue) throws ParseException{
		return dtparse(dateValue,YMDHM);
	}
	
	public static String dformat(Date date){
		return dtformat(date,YMD);
	}
	
	public static Date dparse(String dateValue) throws ParseException{
		return dtparse(dateValue,YMD);
	}
	
	public static String dformat2(Date date){
		return dtformat(date,NYMD);
	}
	
	public static Date dparse2(String dateValue) throws ParseException{
		return dtparse(dateValue,NYMD);
	}
}
