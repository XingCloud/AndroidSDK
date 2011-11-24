package com.xingcloud.tasks.base;

import java.util.ArrayList;

import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;

/**
 * A CompositeCommand is a composite command for serialCommand or parallelCommand.
 * 
 * @author longyangxi
 */
public class CompositeTask extends Task implements ITaskListener {

	/** @private */
	protected ArrayList<String> _messages = new ArrayList<String>();
	////////////////////////////////////////////////////////////////////////////////////////
	// Properties                                                                         //
	////////////////////////////////////////////////////////////////////////////////////////
	/** @private */
	protected ArrayList<Task> _tasks = new ArrayList<Task>();
	
	
	protected IEventListener taskAbortEventListener = new IEventListener() {
		
		/*
		 * (non-Javadoc)
		 * @see com.xingcloud.event.IEventListener#performEvent(com.xingcloud.event.XingCloudEvent)
		 */
		public void performEvent(XingCloudEvent evt) {
			removeTaskListeners(((TaskEvent)evt).task);
			notifyTotalProgress();
			next(((TaskEvent)evt).task);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};
	protected IEventListener taskCompleteEventListener = new IEventListener() {
		
		/*
		 * (non-Javadoc)
		 * @see com.xingcloud.event.IEventListener#performEvent(com.xingcloud.event.XingCloudEvent)
		 */
		public void performEvent(XingCloudEvent evt) {
			removeTaskListeners(((TaskEvent)evt).task);
			notifyTotalProgress();
			next(((TaskEvent)evt).task);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};
	
	protected IEventListener taskErrorEventListener = new IEventListener() {
		
		/*
		 * (non-Javadoc)
		 * @see com.xingcloud.event.IEventListener#performEvent(com.xingcloud.event.XingCloudEvent)
		 */
		public void performEvent(XingCloudEvent evt) {
			removeTaskListeners(((TaskEvent)evt).task);
			notifyTotalProgress();
			notifyError(((TaskEvent)evt).message,null);
			next(((TaskEvent)evt).task);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Getters & Setters                                                                  //
	////////////////////////////////////////////////////////////////////////////////////////
	
	protected IEventListener taskProgressEventListener = new IEventListener() {
		
		/*
		 * (non-Javadoc)
		 * @see com.xingcloud.event.IEventListener#performEvent(com.xingcloud.event.XingCloudEvent)
		 */
		public void performEvent(XingCloudEvent evt) {
			((CompositeTask)evt.getTarget()).onTaskProgress((TaskEvent)evt);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Public Methods                                                                     //
	////////////////////////////////////////////////////////////////////////////////////////
	public CompositeTask()
	{
		super();
		this._total = 0;
	}
	
	public CompositeTask(int delay,int timeOut,int retryCount)
	{
		super(delay,timeOut,retryCount);
		this._total=0;
	}
	
	protected void addTaskListeners(Task cmd)
	{
		cmd.addEventListener(TaskEvent.TASK_COMPLETE, taskCompleteEventListener);
		cmd.addEventListener(TaskEvent.TASK_ABORT, taskAbortEventListener);
		cmd.addEventListener(TaskEvent.TASK_ERROR, taskErrorEventListener);
		cmd.addEventListener(TaskEvent.TASK_PROGRESS,taskProgressEventListener);			
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.Task#complete()
	 */
	protected void complete()
	{
		_tasks.clear();
		_messages.clear();
		super.complete();
	}
	
	/**
	 * Executes the composite command. Abstract method. Be sure to call super.execute()
	 * first in subclassed execute methods.
	 */ 
	
	/*
	 * 
	 */
	protected void doExecute()
	{
        super.doExecute();
		enqueueTasks();
	}
	
	/**
	 * Enqueues a Taskfor use in the composite Task's execution sequence.
	 */
	public void enqueue(Task task, String progressMsg)
	{
		if(task==null) return;
		_tasks.add(task);
		_messages.add(progressMsg);
		_total++;
	}
	
	/**
	 * Abstract method. This is the place where you enqueue single Tasks.
	 * @private
	 */
	protected void enqueueTasks()
	{
	}
	
	
	/**
	 * The name identifier of the Task.
	 */
	public String getName()
	{
		return "compositeTask";
	}
	
	/**
	 * Executes the next enqueued Task.
	 * @private
	 */
	protected Boolean next(Task oldTask)
	{
		if(_isAborted||_tasks.size()==0){
			if(!_hasError)
				complete();
			return false;
		}
		return true;
	}
	////////////////////////////////////////////////////////////////////////////////////////
	// Private Methods                                                                    //
	////////////////////////////////////////////////////////////////////////////////////////
	protected void  notifyTotalProgress()
	{
		this._completed++;
		
		dispatchEvent(new TaskEvent(TaskEvent.TOTAL_PROGRESS, this,this._progressMsg));
	}
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.ITaskListener#onTaskAbort(com.xingcloud.tasks.base.TaskEvent)
	 */
	public void onTaskAbort(TaskEvent e)
	{
		removeTaskListeners(e.task);
		notifyTotalProgress();
		next(e.task);
	}
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.ITaskListener#onTaskComplete(com.xingcloud.tasks.base.TaskEvent)
	 */
	public void onTaskComplete(TaskEvent e)
	{
		removeTaskListeners(e.task);
		notifyTotalProgress();
		next(e.task);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.ITaskListener#onTaskError(com.xingcloud.tasks.base.TaskEvent)
	 */
	public void onTaskError(TaskEvent e)
	{
		removeTaskListeners(e.task);
		notifyTotalProgress();
		notifyError(e.message,null);
		next(e.task);
	}
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.ITaskListener#onTaskProgress(com.xingcloud.tasks.base.TaskEvent)
	 */
	public void onTaskProgress(TaskEvent e)
	{
		this.notifyProgress(e.task);
	}

	/**
	 * removeTaskListeners
	 * @private
	 */
	protected void removeTaskListeners(Task cmd)
	{
		cmd.removeEventListener(TaskEvent.TASK_COMPLETE, taskCompleteEventListener);
		cmd.removeEventListener(TaskEvent.TASK_ABORT, taskAbortEventListener);
		cmd.removeEventListener(TaskEvent.TASK_ERROR, taskErrorEventListener);
		cmd.removeEventListener(TaskEvent.TASK_PROGRESS,taskProgressEventListener);
	}
}
