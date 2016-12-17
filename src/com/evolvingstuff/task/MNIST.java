package com.evolvingstuff.task;

import java.io.*;

import com.evolvingstuff.evaluator.*;
import com.evolvingstuff.neuralnet.ISupervised;

//mmaguero
import com.evolvingstuff.util.util;
//

final public class MNIST implements IInteractiveEvaluatorSupervised {
	/*
	The data is stored in a very simple file format designed for storing vectors and multidimensional matrices. General info on this format is given at the end of this page, but you don't need to read that to use the data files.

	All the integers in the files are stored in the MSB first (high endian) format used by most non-Intel processors. Users of Intel processors and other low-endian machines must flip the bytes of the header.

	There are 4 files:
	
	train-images-idx3-ubyte: training set images
	train-labels-idx1-ubyte: training set labels
	t10k-images-idx3-ubyte:  test set images
	t10k-labels-idx1-ubyte:  test set labels
	
	The training set contains 60000 examples, and the test set 10000 examples.  
	
	TRAINING SET LABEL FILE (train-labels-idx1-ubyte):
	[offset] [type]          [value]          [description]
	0000     32 bit integer  0x00000801(2049) magic number (MSB first)
	0004     32 bit integer  60000            number of items
	0008     unsigned byte   ??               label
	0009     unsigned byte   ??               label
	........
	xxxx     unsigned byte   ??               label
	
	The labels values are 0 to 9.
	TRAINING SET IMAGE FILE (train-images-idx3-ubyte):
	[offset] [type]          [value]          [description]
	0000     32 bit integer  0x00000803(2051) magic number
	0004     32 bit integer  60000            number of images
	0008     32 bit integer  28               number of rows
	0012     32 bit integer  28               number of columns
	0016     unsigned byte   ??               pixel
	0017     unsigned byte   ??               pixel
	........
	xxxx     unsigned byte   ??               pixel
	
	Pixels are organized row-wise. Pixel values are 0 to 255. 0 means background (white), 255 means foreground (black).
	TEST SET LABEL FILE (t10k-labels-idx1-ubyte):
	[offset] [type]          [value]          [description]
	0000     32 bit integer  0x00000801(2049) magic number (MSB first)
	0004     32 bit integer  10000            number of items
	0008     unsigned byte   ??               label
	0009     unsigned byte   ??               label
	........
	xxxx     unsigned byte   ??               label
	
	The labels values are 0 to 9.
	TEST SET IMAGE FILE (t10k-images-idx3-ubyte):
	[offset] [type]          [value]          [description]
	0000     32 bit integer  0x00000803(2051) magic number
	0004     32 bit integer  10000            number of images
	0008     32 bit integer  28               number of rows
	0012     32 bit integer  28               number of columns
	0016     unsigned byte   ??               pixel
	0017     unsigned byte   ??               pixel
	........
	xxxx     unsigned byte   ??               pixel
	
	Pixels are organized row-wise. Pixel values are 0 to 255. 0 means background (white), 255 means foreground (black). 
	*/
	
	private final int height = 28;
	private final int width = 28;
	private final int task_action_dimension = 10; //0-9
	private final int task_observation_dimension = height * width; //28x28=784
	private final int total_train = 60000;
	private final int total_test = 10000;
	private final String train_images = "train-images-idx3-ubyte";
	private final String train_labels = "train-labels-idx1-ubyte";
	private final String test_images = "t10k-images-idx3-ubyte";
	private final String test_labels = "t10k-labels-idx1-ubyte";
	private String path;
	private boolean validation_mode = false;
	
	public MNIST(String path) {
		this.path = path;
	}
	
	private double InnerEval(double[] agent_output, double[] input_to_agent, int target_loc) throws Exception {
		double high = Double.NEGATIVE_INFINITY;
		int high_loc = -1;
		int len = agent_output.length;
		for (int i = 0; i < len; i++) {
			if (agent_output[i] > high) {
				high = agent_output[i];
				high_loc = i;   
			}
		}
		/*if (high_loc == target_loc) {
			return 1.0;
		}
		else {
			return 0.0;
		}*/
		return (high_loc == target_loc ? 1.0 : 0.0); //mmaguero optimize if else short
	}
	
	private double EvaluateSampleSupervised(byte[] bimg, byte[] blbl, ISupervised agent, boolean give_target, 
			int epoches, boolean validation_mode) throws Exception {//mmaguero add epoches validation_mode
		double[] input_to_agent = new double[task_observation_dimension];
		int loc = 0;
		int k = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				input_to_agent[loc] = (double)((int) bimg[k] & 0xFF)/255.0;
				loc++;
				k++;
			}
		}
		int target_loc = (int) blbl[0] & 0xFF;
		double[] target_vec;
		
		target_vec = new double[task_action_dimension];
		target_vec[target_loc] = 1;

		double[] agent_output;
		if (give_target) {
			agent_output = agent.Next(input_to_agent, target_vec);
		}
		else {
			agent_output = agent.Next(input_to_agent);
		}
		//mmaguero 
		double innerEval = InnerEval(agent_output, input_to_agent, target_loc);
		if(validation_mode){
			if (innerEval >= 1) 
				util.WriteTarget(String.valueOf(target_loc), "saved-target/target"+(epoches++)+".int");
			else util.WriteTarget("-", "saved-target/target"+(epoches++)+".int");
		}
		//
		return innerEval;//mmaguero InnerEval(agent_output, input_to_agent, target_loc);
	}

	public int GetActionDimension() {
		return task_action_dimension;
	}

	public int GetObservationDimension() {
		return task_observation_dimension;
	}

	public void SetValidationMode(boolean validation) {
		validation_mode = validation;
	}

	public double EvaluateFitnessSupervised(ISupervised agent, int epoches) throws Exception {
		byte[] bimg = new byte[task_observation_dimension];
		byte[] blbl = new byte[1];
		
		String path_images, path_labels, display;
		int total_examples;
		boolean apply_training;
		
		if (validation_mode == false) { //TRAIN
			path_images = path + train_images;
			path_labels = path + train_labels;
			total_examples = total_train;
			apply_training = true;
			display = "TRAIN";
		}
		else { //TEST
			path_images = path + test_images;
			path_labels = path + test_labels;
			total_examples = total_test;
			apply_training = false;
			display = "TEST";
		}
		
		double tot_fit = 0;
		int total_errors = 0;
		FileInputStream images = new FileInputStream(path_images);
		FileInputStream labels = new FileInputStream(path_labels);
		images.skip(16);
		labels.skip(8);
		double fit = 0.0; //mmaguero declare outside loops
		for (int n = 0; n < total_examples; n++) {
			if (n % 1000 == 999) {
				System.out.print(".");
			}
			images.read(bimg);
			labels.read(blbl);
			fit = EvaluateSampleSupervised(bimg, blbl, agent, apply_training, epoches, validation_mode); //mmaguero add epoches validation_mode
			if (fit < 1) {
				total_errors++;
			}
			tot_fit += fit;
		}
		images.close();
		labels.close();
		tot_fit /= total_examples;
		System.out.println("\n"+display+" ERRORS: " + total_errors + " (of "+total_examples+")");
		util.writeLog("\n"+display+" ERRORS: " + total_errors + " (of "+total_examples+")");//mmaguero
		return tot_fit;
	}
}

