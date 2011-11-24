package com.xingcloud.tasks.base;

import java.util.TimerTask;

public class TaskTimer extends TimerTask {

	private Task task;
	
	public TaskTimer(Task t)
	{
		task = t;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		task.onFinalTimeOut();
	}

}
