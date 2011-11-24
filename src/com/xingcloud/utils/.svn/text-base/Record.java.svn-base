package com.xingcloud.utils;

/*
 * (non-Javadoc)
 */
public class Record<K,V>
{
	private Object[] record;
	
	public Record(K key,V value)
	{
		record = new Object[2];
		record[0] = key;
		record[1] = value;
	}
	
	public K getKey()
	{
		return (K)record[0];
	}
	
	public V getValue()
	{
		return (V)record[1];
	}
}
