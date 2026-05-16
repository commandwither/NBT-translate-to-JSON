package io.commandwither.nbtedit;

import java.io.*;

public class NBTWriter {
	public static boolean lendian = true;
	private DataOutputStream outputStream;

	public NBTWriter(FileOutputStream file) {
		outputStream = new DataOutputStream(file);
	};
	public NBTWriter(File file) throws FileNotFoundException{
		outputStream = new DataOutputStream(new FileOutputStream(file));
	};
	public boolean writeTagWithHeader(TAG_Base tag) throws IOException {
		outputStream.writeByte(tag.type);
		if(tag.type == TAG_End.type){
			return true;
		}
		short tagNameLength = (short) (tag.name.length());
		if (lendian) {
			tagNameLength = Short.reverseBytes(tagNameLength);
		};
		outputStream.writeShort(tagNameLength);
		outputStream.writeBytes(tag.name);
		writeData(tag);
		return true;
	};

	private boolean writeData(TAG_Base tag) throws IOException {
		switch (tag.type) {
			case TAG_End.type:
				//outputStream.writeByte(0);
				break;
			case TAG_Byte.type:
				outputStream.writeByte((byte) (tag.value));
				break;
			case TAG_Short.type:
				outputStream.writeShort((lendian ? Short.reverseBytes((short)tag.value) : (short) tag.value));
				break;
			case TAG_Int.type:
				outputStream.writeInt((lendian ? Integer.reverseBytes((int)tag.value) : (int)tag.value));
				break;
			case TAG_Long.type:
				outputStream.writeLong((lendian ? Long.reverseBytes((long)tag.value) : (long) tag.value));
				break;
			case TAG_Float.type:
				outputStream.writeInt((lendian ? Integer.reverseBytes(Float.floatToIntBits((float)tag.value)) : Float.floatToIntBits((float)tag.value)));
				break;
			case TAG_Double.type:
				outputStream.writeLong((lendian ? Long.reverseBytes(Double.doubleToLongBits((double)tag.value)) : Double.doubleToLongBits((double)tag.value)));
				break;
			case TAG_String.type:
				outputStream.writeShort((lendian ? Short.reverseBytes((short) ((TAG_String) tag).value.length()) : (short) ((TAG_String) tag).value.length()));
				outputStream.writeBytes(((String)tag.value));
				break;
			case TAG_Byte_Array.type:
				outputStream.writeInt((lendian ? Integer.reverseBytes(((TAG_Byte_Array) tag).value.length) : ((TAG_Byte_Array) tag).value.length));
				outputStream.write((byte[]) tag.value);
				break;
			case TAG_List.type:
				TAG_List tagList = (TAG_List) tag;
				outputStream.writeByte(tagList.tagType);
				outputStream.writeInt((lendian ? Integer.reverseBytes(tagList.value.size()) : tagList.value.size()));
				for (int index = 0; index < tagList.value.size(); index++) {
					writeData(tagList.value.get(index));
				}
				break;
			case TAG_Compound.type:
				TAG_Compound tagCompound = (TAG_Compound)tag;
				for(int index = 0; index < tagCompound.value.size(); index++){
					writeTagWithHeader(tagCompound.value.get(index));
				};
				writeTagWithHeader(new TAG_End());
				break;
			case TAG_Int_Array.type:
				TAG_Int_Array tagIntArray = (TAG_Int_Array)tag;
				outputStream.writeInt((lendian ? Integer.reverseBytes(tagIntArray.value.length) : tagIntArray.value.length));
				for(int index = 0; index < tagIntArray.value.length; index++){
					outputStream.writeInt((lendian ? Integer.reverseBytes(tagIntArray.value[index]) : tagIntArray.value[index]));
				};
				break;
		}
		return true;
	};
};
