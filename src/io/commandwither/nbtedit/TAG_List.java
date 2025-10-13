package io.commandwither.nbtedit;
import io.commandwither.nbtedit.*;
import java.util.*;

public class TAG_List extends TAG_Base<ArrayList<TAG_Base>>
{
	public static final int type = 9;
	public int tagType = 0;
	public TAG_List (String name, int type, ArrayList<TAG_Base> value){
		super(9, name, value);
		this.tagType = type;
	}
}
