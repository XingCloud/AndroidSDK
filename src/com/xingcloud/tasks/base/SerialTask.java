package com.xingcloud.tasks.base;

/**
 * A CompositeCommand is a composite command that consists of several single
 * commands which are executed in sequential order.
 * 
 * @author longyangxi
 */
public class SerialTask extends CompositeTask {
	/** @private */
	protected String _currentMsg;
	////////////////////////////////////////////////////////////////////////////////////////
	// Properties                                                                         //
	////////////////////////////////////////////////////////////////////////////////////////
	/** @private */
	protected Task _currentTask;


	////////////////////////////////////////////////////////////////////////////////////////
	// Public Methods                                                                     //
	////////////////////////////////////////////////////////////////////////////////////////

	public SerialTask()
	{
		super();
	}
	public SerialTask(int delay,int timeOut,int retryCount)
	{
		super(delay,timeOut,retryCount);
	}

	/**
	 * Aborts the command's execution.
	 */
	public void abort()
	{
		super.abort();
		if (_currentTask!=null)
			_currentTask.abort();
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.CompositeTask#complete()
	 */
	protected void complete()
	{
		_currentTask = null;
		_currentMsg = null;
		super.complete();
	}


	public Task currentTask()
	{
		return _currentTask;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// Getters & Setters                                                                  //
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the composite command. Abstract method. Be sure to call super.execute()
	 * first in subclassed execute methods.
	 */ 
	protected void doExecute()
	{
		super.doExecute();
		next(null);
	}

	/**
	 * The name identifier of the command.
	 */
	public String getName()
	{
		return "serialCommand";
	}
	
	/**
	 * The Message associated to the command's progress.
	 */
	public String getProgressMsg()
	{
		if(_currentMsg!=null)
			return _currentMsg;
		return _currentTask.getProgressMsg();
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.CompositeTask#next(com.xingcloud.tasks.base.Task)
	 */
	protected Boolean next(Task oldTask)
	{
		Boolean hasNext=super.next(oldTask);
		if(!hasNext) return false;
		_currentMsg = (String) _messages.remove(0);
		_currentTask = (Task) _tasks.remove(0);
		this.addTaskListeners(_currentTask);
		_currentTask.execute();
		return true;
	}

}
