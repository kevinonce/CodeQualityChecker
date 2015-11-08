package filter;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.visitor.Filter;

public class BinaryOperateurComplexityFilter implements Filter<CtBinaryOperator<?>>{

	@Override
	public boolean matches(CtBinaryOperator<?> arg0) {
		if(arg0.getKind().equals(BinaryOperatorKind.AND) || arg0.getKind().equals(BinaryOperatorKind.OR)){
			return true;
		}
		
		return false;
	}

}
