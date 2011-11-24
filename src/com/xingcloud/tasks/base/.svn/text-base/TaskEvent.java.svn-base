package com.xingcloud.tasks.base;

import com.xingcloud.event.XingCloudEvent;

public class TaskEvent extends XingCloudEvent {

	/**
	 * A constant for task events which signals that the task has been aborted.
	 */
	public static String TASK_ABORT		= "task_abort";
	/**
	 * A constant for command events which signals that the command has completed
	 * execution.
	 */
	public static String TASK_COMPLETE	= "task_complete";
	/**
	 * A constant for task events which signals that an error occured during the the
	 * task execution.
	 */
	public static String TASK_ERROR		= "task_error";
	/**
	 *简单task的progress
	 */
	public static String TASK_PROGRESS	= "task_progress";
		
	/**
	 * 对于复合task的总progress，比如5个子task执行了3个
	 * */
	public static String TOTAL_PROGRESS="total_progress";
	public String message;
	
	public Task task;
	
	//public TaskEvent(String type){
	//	super(type);
	//}
	public TaskEvent(String type,Task t, String msg) {
		super(type,null);
		task = t;
		message = msg;
	}

}
