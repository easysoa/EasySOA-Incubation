package proto.bdaccess;

import java.util.Calendar;
import java.util.Date;

public class HoursPeriod {
	//begining is the bgining of the period 
	//it represent an hour of the day in minute
	//ending is the end of the period
	//in minute too
	private int begining,ending;
	
	//
	/**
	 * constructor for hoursperiod 
	 * setting it s beginning and it s ending in minute
	 * @param begining
	 * @param ending
	 */
	public HoursPeriod(int begining,int ending){
		this.begining=begining;
		this.ending=ending;
	}
	/**
	 * true if the hour of the date is in this period
	 * else false 
	 * @param date
	 * @return
	 */
	public boolean inPeriod(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int nbMinutes=cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
		return nbMinutes>begining&& nbMinutes<ending  ;
	}
	
	public String toString(){
		return "begining:"+begining+" ending:"+ending;
	}
	
	
	
	
	
	public boolean equals(Object p){
		if(p instanceof HoursPeriod)
		return ((HoursPeriod)p).begining==begining&&((HoursPeriod)p).ending==ending;
		return false;
	}

}
