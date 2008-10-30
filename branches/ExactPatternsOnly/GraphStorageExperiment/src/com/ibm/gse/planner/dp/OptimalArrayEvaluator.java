package com.ibm.gse.planner.dp;

/**
 * This class is responsible for evaluating the cost of a plan 
 * @author Tian Yuan
 *
 */
public class OptimalArrayEvaluator {
	public int evaluate(OptimalArrayElem e) {
//		System.out.println(e.getPlan().diskIO());
		return e.getPlan().diskIO();
	}
}
