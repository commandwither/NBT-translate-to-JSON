package com.commandwither.test;

import io.commandwither.nbtedit.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;

public class Main
{	//把JE结构转BE结构（示例）
	public static void main(String[] args){
		File files[] = {};
		files = getFiles("./source/").toArray(files);
		try
		{
			for(int index = 0; index < files.length; index++){
				NBTReader nbtR = new NBTReader(files[index]);
				File mkd = new File(files[index].getParent().replace("./source/", "./result/"));
				mkd.mkdirs();
				TAG_Compound structureJE = (TAG_Compound)nbtR.readAllTag();
				TAG_Compound structureBE = new TAG_Compound("");
				int structureSize[] = new int[]{((TAG_Int)((TAG_List)structureJE.get("size")).get(0)).value, ((TAG_Int)((TAG_List)structureJE.get("size")).get(1)).value, ((TAG_Int)((TAG_List)structureJE.get("size")).get(2)).value};
				structureBE.setTag(structureJE.get("size"));
				TAG_List structure_world_origin = new TAG_List("structure_world_origin", TAG_Int.type, new ArrayList());
				structure_world_origin.value.add(new TAG_Int("", 0));
				structure_world_origin.value.add(new TAG_Int("", 0));
				structure_world_origin.value.add(new TAG_Int("", 0));
				structureBE.setTag(new TAG_Int("format_version", 1));
				structureBE.setTag(structure_world_origin);
				TAG_Compound structure = new TAG_Compound("structure");
				structure.setTag(new TAG_List("entities", TAG_End.type));
				TAG_Compound palette = new TAG_Compound("palette");
				structure.setTag(palette);
				palette.setTag(new TAG_Compound("default"));
				TAG_Base tagPoint = palette.get("default");
				((TAG_Compound)tagPoint).setTag(new TAG_Compound("block_position_data"));
				((TAG_Compound)tagPoint).setTag(new TAG_List("block_palette", TAG_Compound.type));
				tagPoint = ((TAG_Compound)tagPoint).get("block_palette");
				TAG_List paletteJE = (TAG_List)structureJE.get("palette");
				int airIndex = -1;
				for(int paletteIndex = 0; paletteIndex < paletteJE.value.size(); paletteIndex ++){
					TAG_Compound paletteBE = new TAG_Compound("");
					paletteBE.setTag(new TAG_Int("version", 18000000));
					paletteBE.setTag(new TAG_Compound("states"));
					paletteBE.setTag(new TAG_String("name", ((TAG_String)((TAG_Compound)paletteJE.get(paletteIndex)).get("Name")).value));
					((TAG_List)tagPoint).addTag(paletteBE);
					if(((TAG_String)((TAG_Compound)paletteJE.get(paletteIndex)).get("Name")).value.equals("minecraft:air")){
						airIndex = paletteIndex;
					}
				};
				if(airIndex == -1){
					TAG_Compound paletteBE = new TAG_Compound("");
					paletteBE.setTag(new TAG_Int("version", 18000000));
					paletteBE.setTag(new TAG_Compound("states"));
					paletteBE.setTag(new TAG_String("name", "minecraft:air"));
					((TAG_List)tagPoint).addTag(paletteBE);
					airIndex = ((TAG_List)tagPoint).value.size() - 1;
				};
				TAG_List blockPositionsList = (TAG_List)structureJE.get("blocks");
				structure.setTag(new TAG_List("block_indices", TAG_List.type));
				tagPoint = structure.get("block_indices");
				((TAG_List)tagPoint).addTag(new TAG_List("", TAG_Int.type));
				((TAG_List)tagPoint).addTag(new TAG_List("", TAG_Int.type));
				TAG_List blockStateList = (TAG_List)((TAG_List)tagPoint).get(0);
				while(blockStateList.value.size() < structureSize[0] * structureSize[1] * structureSize[2]){
					blockStateList.addTag(new TAG_Int("", airIndex));
				};
				for(int positions = 0; positions < blockPositionsList.value.size(); positions++){
					TAG_Compound blockState = (TAG_Compound)blockPositionsList.get(positions);
					TAG_List position = (TAG_List)blockState.get("pos");
					int positionIndex = ((TAG_Int)position.get(0)).value * structureSize[1] * structureSize[2] + ((TAG_Int)position.get(1)).value * structureSize[2] + ((TAG_Int)position.get(2)).value;
					blockStateList.setTag(positionIndex, (TAG_Int)blockState.get("state"));
				};
				int blockStateListLength = blockPositionsList.value.size();
				blockStateList = (TAG_List)((TAG_List)tagPoint).get(1);
				while(blockStateList.value.size() < structureSize[0] * structureSize[1] * structureSize[2]){
					blockStateList.addTag(new TAG_Int("", -1));
				};
				structureBE.setTag(structure);
				new File("./result/").mkdir();
				NBTWriter nbtW = new NBTWriter(new File(files[index].getPath().replace("/source/", "/result/") + ".mcstructure"));
				Files.write(Paths.get(files[index].getPath().replace("/source/", "/result/") + ".json"), nbtR.readAsJSON().toString().getBytes(StandardCharsets.UTF_8));
				nbtW.writeTagWithHeader(structureBE);
			}
			System.out.println("success");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	};
	public static ArrayList<File> getFiles(String path){
		File files[] = new File(path).listFiles();
		ArrayList fileList = new ArrayList<File>();
		for(int index = 0; index < files.length; index++){
			System.out.println(files[index].getPath());
			if(files[index].isDirectory()){
				ArrayList tempList = getFiles(path + "/" + files[index].getName());
				for(int mndex = 0; mndex < tempList.size(); mndex++){
					fileList.add(tempList.get(mndex));
				};
			} else {
				fileList.add(files[index]);
			}
		};
		return fileList;
	};
}
