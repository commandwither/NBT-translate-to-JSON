package io.commandwither.nbtedit;

public abstract class TAG_Base<T>
{
	public int type;
	public String name;
	public T value;
	public void setValue(T value){
		this.value = value;
	};
	protected TAG_Base(int type, String TagName, T value){
		this.type = type;
		this.value = value;
		this.name = TagName;
	};
}
