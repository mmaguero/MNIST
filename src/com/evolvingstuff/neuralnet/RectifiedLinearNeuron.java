package com.evolvingstuff.neuralnet;

public class RectifiedLinearNeuron extends Neuron { 

	private double slope;
	
	public RectifiedLinearNeuron(double slope) {
		this.slope = slope;
	}
	
	@Override
	public double Activate(double x) {
		/*if (x >= 0) {
			return x;
		}
		else {
			return x * slope;
		}*/
		return (x >= 0 ? x : x * slope); //mmaguero optimize if else short
	}

	@Override
	public double Derivative(double x) {
		/*if (x >= 0) {
			return 1;
		}
		else {
			return slope;
		}*/
		return (x >= 0 ? 1 : slope); //mmaguero optimize if else short
	}
}
