package com.xingcloud.tasks.base;

/**
 * Classes that want to informed by events broadcasted from Tasks should
 * implement this interface.
 * 
 * @author longyangxi
 */
public interface ITaskListener {
	void onTaskAbort(TaskEvent e);
	void onTaskComplete(TaskEvent e);
	void onTaskError(TaskEvent e);
	void onTaskProgress(TaskEvent e);

}
