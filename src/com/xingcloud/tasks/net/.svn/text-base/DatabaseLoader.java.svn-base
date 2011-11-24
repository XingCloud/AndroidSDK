package com.xingcloud.tasks.net;

import com.xingcloud.items.ItemsParser;

public class DatabaseLoader extends AbstractLoader {

	public DatabaseLoader(String url) {
		super(url);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.Task#complete()
	 */
	protected void complete()
	{
		ItemsParser.parse(this.response.getContent());
		super.complete();
	}	
}
