package schedulegeneration;

// import java.lang.*;

// import java.awt.*;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

public class ScheduleTime {
	int hour;
	int minute;
	public ScheduleTime(String time) {
		if(time.indexOf("am") != -1 || time.indexOf("pm") != -1) {
			time = convert12To24Hour(time);
		}
		this.hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
		this.minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
	}
	
	public ScheduleTime() {
		this("00:00");
	}

	public ScheduleTime(int hour, int minute) {
		this.hour   = hour;
		this.minute = minute;
	}

	public int getMinuteValue() {
		return this.hour * 60 + this.minute;
	}
	
	static public ScheduleTime roundUp(ScheduleTime time, int minuteVal) {
		if(time.minute > minuteVal) {
			time.hour += 1;
		}
		time.minute = minuteVal;
		return time;
	}
	
	static public ScheduleTime roundUp(ScheduleTime time) {
		return roundUp(time, 0);
	}
	
	static public ScheduleTime roundDown(ScheduleTime time, int minuteVal) {
		if(time.minute < minuteVal) {
			time.hour -= 1;
		}
		time.minute = minuteVal;
		return time;
	}
	
	static public ScheduleTime roundDown(ScheduleTime time) {
		return roundDown(time, 0);
	}

	static public ScheduleTime roundToPrecision(ScheduleTime time, int precisionMinutes) {
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(int minutes = 0; minutes <= 60; minutes += precisionMinutes) {
			distances.add(minutes - time.minute);
		}
		int i = 0;
		for(i = 0; i < distances.size() - 1; ++i) {
			if(Math.abs(distances.get(i + 1)) < Math.abs(distances.get(i))) {
				continue;
			}
			break;
		}
		if(distances.get(i) > 0) {
			return roundUp(time, precisionMinutes * i);
		}
		else if(distances.get(i) < 0) {
			return roundDown(time, precisionMinutes * i);
		}
		else {
			return time;
		}
	}

	static public int compareTimes(ScheduleTime a, ScheduleTime b) {
		if(a.hour != b.hour) {
			return a.hour - b.hour;
		}
		else {
			return a.minute - b.minute;
		}
	}
	
	static public String convert12To24Hour(String time) {
		if(time.indexOf(':') != -1 && time.indexOf(' ') != -1) {
			time = time.trim();
			int hour = Integer.parseInt(time.substring(0, time.indexOf(':')).trim());
			int min  = Integer.parseInt(time.substring(time.indexOf(':') + 1, time.indexOf(' ')).trim());
			if(hour < 12 && time.indexOf("pm") != -1) {
				hour += 12;
			}
			else if(hour == 12 && time.indexOf("am") != -1) {
				hour -= 12;
			}
			String newTime = String.format("%02d:%02d", hour, min);
			return newTime;
		}
		return time;
	}
	
	static public String convert24To12Hour(String time) {
		if(time.indexOf(':') != -1) {
			time = time.trim();
			int hour = Integer.parseInt(time.substring(0, time.indexOf(':')).trim());
			int min  = Integer.parseInt(time.substring(time.indexOf(':') + 1).trim());
			String newTime;
			if(hour > 12) {
				newTime = String.format("%02d:%02d pm", hour - 12, min);
			}
			else if(hour == 12) {
				newTime = String.format("%02d:%02d pm", hour, min);
			}
			else if(hour == 0) {
				newTime = String.format("%02d:%02d am", hour + 12, min);
			}
			else {
				newTime = String.format("%02d:%02d am", hour, min);
			}
			return newTime;
		}
		return time;
	}
}