package io.commandwither.nbtedit;
import java.util.*;

public class TAG_Compound extends TAG_Base<ArrayList<TAG_Base>>
{
	public static final int type = 10;
	public TAG_Compound(String name, ArrayList<TAG_Base> value){
		super(10, name, value);
	};
}
