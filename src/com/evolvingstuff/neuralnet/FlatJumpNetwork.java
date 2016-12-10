package com.evolvingstuff.neuralnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.evolvingstuff.util.util;

public class FlatJumpNetwork implements ISupervised {
	
	private List<Layer> layers = new ArrayList<Layer>();
	private Layer readout;
	//mmaguero final disponibility
	final private Random r;
	final private int input_dimension;
	final private int output_dimension;
	final private int hidden_per_layer;
	final private Neuron neuron;
	final private double init_weight_range;
	final private double learning_rate;
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////

	public FlatJumpNetwork(Random r, int input_dimension, int output_dimension, int hidden_per_layer, Neuron neuron, double init_weight_range, double learning_rate) {
		this.r = r;
		this.input_dimension = input_dimension;
		this.output_dimension = output_dimension;
		this.hidden_per_layer = hidden_per_layer;
		this.neuron = neuron;
		this.init_weight_range = init_weight_range;
		this.learning_rate = learning_rate;
		
		AddHiddenLayer();
		CreateNewReadoutLayer();
	}
	
	private int GetEffectiveInputDimension() {
		int result = input_dimension;
		for (Layer layer : layers) {
			result += layer.output_dimension;
		}
		return result;
	}
	
	private void AddHiddenLayer() {
		int effective_input_dimension = GetEffectiveInputDimension();
		Layer layer = new Layer(r, effective_input_dimension, hidden_per_layer, neuron, init_weight_range, learning_rate);
		layers.add(layer);
	}
	
	private void CreateNewReadoutLayer() {
		int effective_input_dimension = GetEffectiveInputDimension();
		readout = new Layer(r, effective_input_dimension, output_dimension, new IdentityNeuron(), init_weight_range, learning_rate);
	}

	public double[] Next(double[] input, double[] target_output) throws Exception {
		int len = input.length; //mmaguero len optimize
		double[] extended_input = new double[len];
		for (int i = 0; i < len; i++) {
			extended_input[i] = input[i];
		}
		double[] hidden_act; //mmaguero declare outside variable
		for (Layer layer : layers) {
			hidden_act = layer.Forward(extended_input);
			extended_input = util.ConcatVectors(hidden_act, extended_input);
		}
		double[] output = readout.Forward(extended_input);
		if (target_output != null) {
			//backprop readout
			double[] delta = util.Delta(target_output, output);
			delta = readout.Backprop(delta);
			//backprop last hidden layer
			double[] short_delta = util.ShortenVector(delta, hidden_per_layer);
			layers.get(layers.size()-1).Backprop(short_delta);
		}
		return output;
	}
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////

	public double[] Next(double[] input) throws Exception {
		return Next(input, null);
	}
	
	public void Save(String path) throws Exception {
		util.MatrixToFile(layers.get(0).weights, path + "hidden.mtrx");
		util.MatrixToFile(readout.weights, path + "readout.mtrx");
	}
	
	public void Load(String path) throws Exception {
		layers.get(0).weights = util.FileToMatrix(path + "hidden.mtrx");
		readout.weights = util.FileToMatrix(path + "readout.mtrx");
	}

}
