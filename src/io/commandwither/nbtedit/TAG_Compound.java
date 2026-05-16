package io.commandwither.nbtedit;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class TAG_Compound extends TAG_Base<ArrayList<TAG_Base>>
{
	public static final int type = 10;
	public TAG_Compound(String name, ArrayList<TAG_Base> value){
		super(10, name, value);
	};
	public TAG_Compound(String name){
		this(name, new ArrayList());
	};
	public TAG_Base get(String name){
		int index = this.value.indexOf(new TAG_IndexUse(name));
		if(index == -1){
			return null;
		}
		return this.value.get(index);
	}

	public boolean setTag(TAG_Base tag)
	{
		// TODO: Implement this method
		int index = this.value.indexOf(new TAG_IndexUse(tag.name));
		if(index == -1){
			this.value.add(tag);
			return true;
		};
		this.value.set(index, tag);
		return true;
	};
	public void removeTag(String name){
		int index = this.value.indexOf(name);
		if(index != -1){
			this.value.remove(index);
		}
	}
};

class TAG_IndexUse extends TAG_Base {
	public TAG_IndexUse(String name){
		super(0, name, null);
	}
};
