package processors;

import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.ReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>>{
	private Set<CtMethod<?>> methods;
	private List<CtVariable<?>> variables;
	
	@Override
	public void process(CtClass<?> ctClass) {
		methods = ctClass.getAllMethods();
		//elementList = ctClass.getElements(new FilterImpl());
		variables = Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class));
	}
	
	@Override
	 public void processingDone() {
		CtInheritanceScanner sc = new CtInheritanceScanner() {
		};
			for(CtVariable<?> c : variables){
				System.out.println(c.getDefaultExpression().getType());
				System.out.println(c.getType());

			}
	 }
}
