package filter;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Filter;

public class InvocationsByVariableFilter implements Filter<CtInvocation<?>> {

	private CtVariable<?> variable;

	public InvocationsByVariableFilter(CtVariable<?> v) {
		this.variable = v;
	}

	public boolean matches(CtInvocation<?> inv) {
		try {
			if (inv.getTarget() != null && this.variable != null
					&& this.variable.getType().getSimpleName().equals(inv.getTarget().getType().getSimpleName()))
				return true;
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}

}
