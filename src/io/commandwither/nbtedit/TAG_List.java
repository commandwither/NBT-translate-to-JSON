package io.commandwither.nbtedit;
import io.commandwither.nbtedit.*;
import java.util.*;

public class TAG_List extends TAG_Base<ArrayList<TAG_Base>>
{
	public static final int type = 9;
	public int tagType = 0;
	public TAG_List (String name, int type){
		this(name, type, new ArrayList());
	};
	public TAG_List (String name, int type, ArrayList<TAG_Base> value){
		super(9, name, value);
		this.tagType = type;
	};
	public TAG_Base get(int index){
		return (TAG_Base)this.value.get(index);
	};
	public boolean addTag(TAG_Base tag){
		this.value.add(tag);
		return true;
	};
	public boolean setTag(int index, TAG_Base tag){
		this.value.set(index, tag);
		return true;
	};
}
