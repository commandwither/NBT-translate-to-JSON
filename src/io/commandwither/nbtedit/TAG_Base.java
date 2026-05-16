package io.commandwither.nbtedit;
import java.util.*;
import java.util.stream.*;

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
	@Override
	public boolean equals(Object tag)
	{
		return ((TAG_Base)tag).name.equals(this.name);
	}

	@Override
	public int hashCode()
	{
		// TODO: Implement this method
		return Objects.hashCode(this.name);
	};
}
