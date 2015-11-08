package filter;

import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.visitor.Filter;

public class ReturnComplexityFilter implements Filter<CtReturn<?>>{

	@Override
	public boolean matches(CtReturn<?> arg0) {
		CtExecutable<?> parent = arg0.getParent(CtExecutable.class);
		if(parent.getBody().getLastStatement().equals(arg0)){
			return false;
		}
		
		return true;
	}

}
