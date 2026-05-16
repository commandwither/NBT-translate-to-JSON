package io.commandwither.nbtedit;
import java.io.*;
import java.lang.annotation.*;
import java.nio.charset.*;
import java.util.*;
import org.json.*;

public class NBTReader {
	public static boolean lendian = false;
	private DataInputStream inputStream;
	private TAG_Base allTag = null;
	private final String TAGTYPE = " bs lf   ";
	public NBTReader(FileInputStream file) {
		inputStream = new DataInputStream(file);
	}
	public NBTReader(File file) throws FileNotFoundException {
		inputStream = new DataInputStream(new FileInputStream(file));
	};
	public JSONObject readAsJSON() throws Exception {
		TAG_Base tag = readAllTag();
		JSONObject result = (JSONObject)readAsJSON(tag);
		return result;
	};
	private Object readAsJSON(TAG_Base tag) throws JSONException {
		return readAsJSON(tag, false, true);
	};
	private Object readAsJSON(TAG_Base tag, boolean isList, boolean isOutCompound) throws JSONException {
		if (tag == null) {
			System.out.println("null");
			return null;
		} ;
		switch (tag.type) {
			case TAG_End.type :
				return null;
			case TAG_Byte.type :
			case TAG_Short.type :
			case TAG_Int.type :
			case TAG_Long.type :
			case TAG_Float.type :
			case TAG_Double.type :
			case TAG_String.type :
				char type = TAGTYPE.charAt(tag.type);
				if(type == ' '){
					return tag.value;
				} else {
					return tag.value + String.valueOf(type);
				}
			case TAG_Byte_Array.type :
				JSONArray byteArray = new JSONArray((((TAG_Byte_Array)tag).value));
				
				return (JSONObject)((Object)byteArray);
			case TAG_List.type : {
				TAG_List list = (TAG_List) tag;
				JSONArray jsonList = new JSONArray();
				for (int index = 0; index < list.value.size(); index++) {
					jsonList.put(readAsJSON(list.value.get(index), true, false));
				}
				return jsonList;
			}
			case TAG_Compound.type : {
				TAG_Compound tagCompound = (TAG_Compound) tag;
				JSONObject json = new JSONObject();
				for (int index = 0; index < tagCompound.value.size(); index++) {
					json.put(tagCompound.value.get(index).name, readAsJSON(tagCompound.value.get(index), false, false));
				} ;
				return json;
			}
			case TAG_Int_Array.type : {
				JSONArray intArray = new JSONArray((((TAG_Int_Array)tag).value));
				return intArray;
			}
		}
		return null;
	};
	public TAG_Base readAllTag(){
		if(allTag != null){
			return allTag;
		};
		allTag = readTag();
		return allTag;
	};
	private TAG_Base readTag() {
		try {
			byte tagType = inputStream.readByte();
			if (tagType == TAG_End.type)
				return new TAG_End();
			short nameLength = inputStream.readShort();
			if (lendian) {
				nameLength = Short.reverseBytes(nameLength);
			} ;
			byte[] nameByte = new byte[nameLength];
			inputStream.read(nameByte);
			String tagName = new String(nameByte, StandardCharsets.UTF_8);
			return getTag(tagType, tagName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	};
	private TAG_Base getTag(byte type, String name) {
		try {
			switch (type) {
				case TAG_Byte.type :
					return new TAG_Byte(name, inputStream.readByte());
				case TAG_Short.type :
					return new TAG_Short(name,
							(lendian ? Short.reverseBytes(inputStream.readShort()) : inputStream.readShort()));
				case TAG_Int.type :
					return new TAG_Int(name,
							(lendian ? Integer.reverseBytes(inputStream.readInt()) : inputStream.readInt()));
				case TAG_Long.type :
					return new TAG_Long(name,
							(lendian ? Long.reverseBytes(inputStream.readLong()) : inputStream.readLong()));
				case TAG_Float.type :
					return new TAG_Float(name, Float.intBitsToFloat(
							(lendian ? Integer.reverseBytes(inputStream.readInt()) : inputStream.readInt())));
				case TAG_Double.type :
					return new TAG_Double(name, Double.longBitsToDouble(
							(lendian ? Long.reverseBytes(inputStream.readLong()) : inputStream.readLong())));
				case TAG_Byte_Array.type :
					return new TAG_Byte_Array(name, inputStream.readNBytes(
							(lendian ? Integer.reverseBytes(inputStream.readInt()) : inputStream.readInt())));
				case TAG_String.type :
					short strLength = (lendian ? Short.reverseBytes(inputStream.readShort()) : inputStream.readShort());
					byte[] strbyte = new byte[strLength];
					inputStream.read(strbyte);
					return new TAG_String(name, new String(strbyte, StandardCharsets.UTF_8));
				case TAG_List.type :
					byte listType = inputStream.readByte();
					int listLength = (lendian ? Integer.reverseBytes(inputStream.readInt()) : inputStream.readInt());
					return readListTag(name, listType, listLength);
				case TAG_Compound.type :
					TAG_Base tagCurrent = null;
					ArrayList tags = new ArrayList();
					while ((tagCurrent = readTag()).type != TAG_End.type) {
						tags.add(tagCurrent);
					}
					//tags.add(new TAG_End());
					return new TAG_Compound(name, tags);
				case TAG_Int_Array.type :
					int length = (lendian ? Integer.reverseBytes(inputStream.readInt()) : inputStream.readInt());
					int[] intArray = new int[length];
					for (int index = 0; index < length; index++) {
						intArray[index] = (lendian
								? Integer.reverseBytes(inputStream.readInt())
								: inputStream.readInt());
					}
					return new TAG_Int_Array(name, intArray);
				default :
					return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	};
	private TAG_List readListTag(String name, byte type, int length) {
		ArrayList<TAG_Base> tagArray = new ArrayList();
		for (int index = 0; index < length; index++) {
			tagArray.add(getTag(type, ""));
		} ;
		return new TAG_List(name, type, tagArray);
	};
	public void skip(int bytelength) {
		try {
			inputStream.skipBytes(bytelength);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

