package io.commandwither.nbtedit;
import java.io.*;
import java.lang.annotation.*;
import java.nio.charset.*;
import java.util.*;

public class NBTReader {
	private DataInputStream inputStream;
	private boolean lendian = true;
	public NBTReader(FileInputStream file) {
		inputStream = new DataInputStream(file);
	}
	public NBTReader(File file) throws FileNotFoundException {
		inputStream = new DataInputStream(new FileInputStream(file));
	};
	public String readAsJSON() {
		TAG_Base tag = readTag();
		return readAsJSON(tag);
	};
	private String readAsJSON(TAG_Base tag) {
		return readAsJSON(tag, false, true);
	};
	private String readAsJSON(TAG_Base tag, boolean isList, boolean isOutCompound) {
		if (tag == null) {
			System.out.println("null");
			return "null";
		} ;
		String connectStr = isList ? "" : ": ";
		switch (tag.type) {
			case TAG_End.type :
				return "";
			case TAG_Byte.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Short.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Int.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Long.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Float.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Double.type :
				return tag.name + connectStr + tag.value.toString();
			case TAG_Byte_Array.type :
				String byteStr = tag.name + ": [";
				for (int index = 0; index < ((TAG_Byte_Array) tag).value.length; index++) {
					if (index == 0) {
						byteStr += ((TAG_Byte_Array) tag).value[index];
					} else {
						byteStr += ", " + ((TAG_Byte_Array) tag).value[index];
					}

				}
				return byteStr + "]";
			case TAG_String.type :
				return tag.name + ": \"" + ((TAG_String) tag).value.replace("\"", "\\\"") + "\"";
			case TAG_List.type : {
				TAG_List list = (TAG_List) tag;
				String snbt = list.name + ": [";
				for (int index = 0; index < list.value.size(); index++) {
					snbt += readAsJSON(list.value.get(index), true, false);
					if (index != list.value.size()) {
						snbt += ",";
					}
				}
				return snbt + "]";
			}
			case TAG_Compound.type : {
				TAG_Compound tagCompound = (TAG_Compound) tag;
				String snbt = tag.name + (isOutCompound ? "{" : ": {");
				boolean isFirst = true;
				for (int index = 0; index < tagCompound.value.size(); index++) {
					if (tagCompound.value.get(index).type == TAG_End.type) {
						break;
					}
					snbt += (isFirst ? "" : ", ") + readAsJSON(tagCompound.value.get(index), false, false);
					isFirst = false;
				} ;
				return snbt + "}";
			}
			case TAG_Int_Array.type : {
				TAG_Int_Array tagIntArray = (TAG_Int_Array) tag;
				String snbt = "{" + tagIntArray.name + ": [";
				for (int index = 0; index < tagIntArray.value.length; index++) {
					if (index == 0) {
						snbt += tagIntArray.value[index];
					} else {
						snbt += ", " + tagIntArray.value[index];
					}
				}
				return snbt + "]";
			}
		}
		return null;
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
					tags.add(new TAG_End());
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

