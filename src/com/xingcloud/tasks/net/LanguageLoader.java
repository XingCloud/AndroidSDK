package com.xingcloud.tasks.net;

import android.util.Log;

import com.xingcloud.language.LanguageManager;
import com.xingcloud.utils.XingCloudLogger;

public class LanguageLoader extends AbstractLoader {

	public LanguageLoader(String url) {
		super(url);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.Task#complete()
	 */
	protected void complete()
	{
		String source = this.response.getContent();
		if(source==null || source.trim().length()==0)
		{
			XingCloudLogger.log(XingCloudLogger.ERROR,"LanguageLoader->complete : null language content!");
		}
		else
		{
			LanguageManager.parse(source);
		}
		super.complete();
	}

}
