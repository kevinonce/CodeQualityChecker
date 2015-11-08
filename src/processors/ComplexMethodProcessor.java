package processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import filter.BinaryOperateurComplexityFilter;
import filter.ReturnComplexityFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

public class ComplexMethodProcessor extends AbstractProcessor<CtMethod<?>> {
	
	Map<CtMethod<?>, Integer> methodMap = new HashMap<>();
	private static final Integer COMPLEXITY_MAX = 10;

	/**
	 * Keywords incrementing the complexity: if, for, while, case, catch, throw, return (that is not the last statement of a method), &&, ||, ?
	 * */
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(CtMethod<?> arg0) {
		Integer complexity = Integer.valueOf(1);
		
		List<CtElement> liste = Query.getElements(arg0, new CompositeFilter(FilteringOperator.UNION, new TypeFilter<CtLoop>(CtLoop.class), new TypeFilter<CtIf>(CtIf.class),
				new TypeFilter<CtCase<?>>(CtCase.class), new TypeFilter<CtCatch>(CtCatch.class), new TypeFilter<CtThrow>(CtThrow.class),new TypeFilter<CtConditional<?>>(CtConditional.class),new BinaryOperateurComplexityFilter(), new ReturnComplexityFilter()));
		
		complexity += liste.size();
		
		methodMap.put(arg0, complexity);
	}
	
	 @Override
	 public void processingDone() {
		 
		 for(CtMethod<?> method : methodMap.keySet()){
			 	Integer complexity = methodMap.get(method);
			 	if(complexity > COMPLEXITY_MAX){
			 		System.out.println(method.getSignature()+" in "+method.getPosition() + " complexity of "+complexity+" is too high !");
			 	}
		 }
		 
	 }

}
