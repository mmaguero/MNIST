package com.evolvingstuff.util;

import java.util.*;

import java.io.*; 

final public class util //mmaguero final disponibility
{
   public static double[] Delta(double[] target, double[] actual) {
		int len = target.length;
		double[] result = new double[len];
		for (int i = 0; i < len; i++) {
			result[i] = target[i] - actual[i];
		}
		return result;
	}
	
   public static double[] ShortenVector(double[] vector, int new_length) {
		double[] result = new double[new_length];
		for (int i = 0; i < new_length; i++) {
			result[i] = vector[i];
		}
		return result;
	}
	
    public static void MatrixToFile(double[][] matrix, String path) throws Exception {
		FileWriter f = new FileWriter(new File(path));
		int lenJ = matrix.length; //mmaguero optimize length
		int lenI = matrix[0].length; //mmaguero optimize length
		for (int j = 0; j < lenJ; j++) {
			if (j > 0) {
				f.write("\n");
			}
			for (int i = 0; i < lenI; i++) {
				if (i > 0) {
					f.write(",");
				}
				f.write(String.valueOf(matrix[j][i]));
			}
		}
		int rows = lenJ; //mmaguero optimeze length
		int cols = lenI; //mmaguero optimeze length
		System.out.println("util.MatrixToFile: " + rows + "x" + cols + " -> " + path);
		//maguero
		writeLog("util.MatrixToFile: " + rows + "x" + cols + " -> " + path);
		//

		f.flush();
		f.close();
	}
	
	public static double[][] FileToMatrix(String path) throws Exception
	{
		int rows = 0;
		int cols = 0;
		
		List<List<Double>> vals = new ArrayList<List<Double>>();
		Scanner sc = new Scanner(new File(path));
		//mmaguero declare outside loops
		String line;
		String[] parts;
		List<Double> row;
		double val = 0.0; 
		int rowSize = 0;
		//
		while (sc.hasNextLine())
		{
			line = sc.nextLine();
			parts = line.split(",");
			row = new ArrayList<Double>();
			val = 0.0;
			for (String part : parts)
			{
				val = Double.parseDouble(part);
				row.add(val);
			}
			rowSize = row.size(); //mmagueor optimize size
			if (cols != rowSize) {
				if (cols == 0) {
					cols = rowSize;
				}
				else {
					throw new Exception("jagged array?");
				}
			}
			vals.add(row);
		}
		rows = vals.size();
		System.out.println("util.FileToMatrix: " + path + " -> " + rows + "x" + cols);
		//mmaguero
		writeLog("util.FileToMatrix: " + path + " -> " + rows + "x" + cols);
		//
		double[][] result = new double[rows][cols];
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				result[j][i] = vals.get(j).get(i);
			}
		}
		return result;
	}
	
	public static double[] ConcatVectors(double[] vec1, double[] vec2) {
		int lenV1 = vec1.length, lenV2 = vec2.length; //mmaguero opimize length
		double[] result = new double[lenV1 + lenV2];
		int loc = 0;
		for (int i = 0; i < lenV1; i++)
			result[loc++] = vec1[i];
		for (int i = 0; i < lenV2; i++)
			result[loc++] = vec2[i];
		return result;
	}
    
   /*mmaguero
    * 3 funciones nuevas
    */
   public static void writeLog(String operacion) {
       FileWriter archivo;
    try{
       if (new File("App.log").exists()==false)
    	   archivo=new FileWriter(new File("App.log"),false);
      
        archivo = new FileWriter(new File("App.log"), true); 
        archivo.write(operacion+"\r\n");
        archivo.flush();
		archivo.close();
    }catch(IOException ioe){
    	System.out.println("Error when writing log archive..." + ioe.toString());
    }
   }
   
	public static void WriteTarget(String target, String path) {
		FileWriter f;
		try {
			if (new File(path).exists()==false)
		    	   f=new FileWriter(new File(path),false);
			
			f = new FileWriter(new File(path), true); 
			f.write(target);
			
			f.flush();
			f.close();
		} catch (IOException ioe) {
			System.out.println("Error when writing target asign..." + ioe.toString());
		}
	}
	
	public static void deletedFilesDirectory(String path) {
		File directorio = new File(path);

		File f;
		if (directorio.isDirectory()) {
			String[] files = directorio.list();
			if (files.length > 0) {
				System.out.println("Path has archives: " + path);
				writeLog("Path has archives: " + path);
				for (String archivo : files) {
					f = new File(path + File.separator + archivo);
					System.out.println("Deleted: " + archivo);
					writeLog("Deleted: " + archivo);
					f.delete();
					f.deleteOnExit();
				}
			}
		}

	}
   //
    
}
