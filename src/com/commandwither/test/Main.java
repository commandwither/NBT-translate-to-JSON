package com.commandwither.test;
import io.commandwither.nbtedit.*;
import java.io.*;
import org.json.*;
import java.nio.file.*;
import java.nio.charset.*;
//Test
public class Main{
	public static void main(String[] args){
		File file = new File("/storage/emulated/0/aaaaaa/self/Java_pack/Java_nbt_io/test/db/000035.ldb");
		try
		{
			
			NBTReader nbtR = new NBTReader(file);
			nbtR.skip(8);
			Files.write(Paths.get("/storage/emulated/0/aaaaaa/self/Java_pack/Java_nbt_io/test/test.txt"), nbtR.readAsJSON().getBytes(StandardCharsets.UTF_8));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
