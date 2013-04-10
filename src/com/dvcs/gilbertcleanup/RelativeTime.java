package com.dvcs.gilbertcleanup;

import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

public class RelativeTime
{
	Date thisDate;
	public RelativeTime(Date date) {
		thisDate = date;
	}
	public  String getRelativeTime()
	{
		PrettyTime p = new PrettyTime();

		return p.format(thisDate);
		//prints: “10 minutes from now”
	}
}