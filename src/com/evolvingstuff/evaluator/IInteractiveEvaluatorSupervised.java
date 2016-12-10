package com.evolvingstuff.evaluator;

import com.evolvingstuff.neuralnet.*;

public interface IInteractiveEvaluatorSupervised 
{
	double EvaluateFitnessSupervised(ISupervised agent, int epoches) throws Exception;//mmaguero add epoches
	int GetActionDimension();
	int GetObservationDimension();
	void SetValidationMode(boolean validation);
	
}
