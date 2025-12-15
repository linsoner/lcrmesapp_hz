package com.dyg.siginprint.app;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * 运行的列表
 */
public class RunningActivityList {

	private static List<Activity> activityList = new LinkedList<Activity>();
	private static RunningActivityList instance;

	public static RunningActivityList getInstance() {
		if (null == instance) {
			instance = new RunningActivityList();
		}
		return instance;

	}

	public void addActivity(Activity activity) {
		if(activityList!=null) {
			if (!activityList.contains(activity)) {
				activityList.add(activity);
			}
		}
	}
	
	public void removeActivity(Activity activity) {
		if(activityList!=null) {
			if (activityList.contains(activity)) {
				activityList.remove(activity);
			}
		}
	}
	

	public static void exit() {
		finishAllActivity();	
		System.exit(0);
	}
	
	
	public static void finishAllActivity() {
		if(activityList!=null) {
			for (Activity activity : activityList) {
				if (activity != null) {
					activity.finish();
				}
			}
		}
	}

	/**
	 * 关闭除了thisActivity之外的其它所有界面
	 * @param thisActivity
	 */
	public static void finishOtherAllActivity(Activity thisActivity) {
		if(activityList!=null) {
			for (Activity activity : activityList) {
				try {
					if (activity != null && !activity.getLocalClassName().equals(thisActivity.getLocalClassName())) {
						activity.finish();
					}
				}catch (Exception e){}

			}
		}
	}
}
