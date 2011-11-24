package com.xingcloud.tasks.base;

import java.util.ArrayList;

public class ParallelTask extends CompositeTask {
	public ParallelTask()
	{
		super();
	}
	public ParallelTask(int delay,int timeOut,int retryCount)
	{
		super(delay,timeOut,retryCount);
	}

	/**
	 * Aborts the command's execution.
	 */
	public void abort()
	{
		super.abort();
		for (Task t:_tasks){
			t.abort();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.CompositeTask#doExecute()
	 */
	protected void doExecute()
	{
		super.doExecute();
		if(_tasks.size()==0) {
			this.complete();
			return;
		}
		//复制一个，以免实时执行命令后_commands被删一个后出错
		ArrayList<Task> tempTasks=(ArrayList<Task>) _tasks.clone();
		for(int i=0;i<_tasks.size();i++)
		{
			Task task=tempTasks.get(i);
			this.addTaskListeners(task);
			task.execute();
		}
		tempTasks=null;
	}
	
	/**
	 * The name identifier of the task.
	 */
	public String getName()
	{
		return "parallelTask";
	}
	
	/**
	 * Executes the next enqueued Task.
	 * @private
	 */
	protected Boolean next(Task oldTask)
	{
		if(oldTask!=null){
			int i=_tasks.indexOf(oldTask);
			if(i>=0)
				_tasks.remove(i);
		}
		return super.next(oldTask);
	}
}
