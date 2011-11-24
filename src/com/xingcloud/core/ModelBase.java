package com.xingcloud.core;

import java.util.ArrayList;

import com.xingcloud.event.EventDispatcher;
import com.xingcloud.items.spec.AsObject;

/**
 * 模型数据基类。
 * AbstractUserProfile及OwnedItem都继承自此类
 * @author chuckzhang
 *
 */
public class ModelBase extends EventDispatcher {

	protected String uid="";

	/**
	 * 类名，方便自动向服务器发送
	 * */
	public String getClassName() {
		String clsName = this.getClass().getName();
		int dotIndex = clsName.lastIndexOf(".");
		return clsName.substring(dotIndex+1, clsName.length());
	}

	/**
	 * 获取唯一ID
	 */
	public String getUid() {
		return uid;
	}

	public void ModelBase() {
	}

	/**
	 * 将一个普通的Object数据解析到此实例，凡是字段相同的属性都覆盖过来，这时候如果是当前玩家数据更新，要停止下track,见继承
	 * 如果需要批量更新数据而又不想track，请用此方法
	 * */
	public void parseFromObject(AsObject data, ArrayList<String> excluded) {
		Reflection.cloneProperties(data,this,excluded);
	}

	/**
	 * 设置唯一ID
	 */
	public void setUid(String uid) {
		ModelBaseManager.instance().removeModel(this.uid);
		this.uid = uid;
		ModelBaseManager.instance().addModel(this);
	}

}
