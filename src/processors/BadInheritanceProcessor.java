package processors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>>{

	private List<CtVariable<?>> variables;
	private Set<CtTypeReference<?>> itf; 
	private Set<CtTypeReference<?>> cl;
	private Map<Method, CtTypeReference<?>> methods;
	
	@Override
	public void process(CtClass<?> ctClass) {
		//elementList = ctClass.getElements(new FilterImpl());
		variables = Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class));
		methods = new HashMap<Method, CtTypeReference<?>>();

	}
	
	@Override
	 public void processingDone() {
		Set<CtTypeReference<?>> itf = new HashSet<CtTypeReference<?>>();
		Set<CtTypeReference<?>> cl = new HashSet<CtTypeReference<?>>();
		for(CtVariable<?> c : variables){
			//System.out.println("v : " + c.getSignature());
			itf = new HashSet<CtTypeReference<?>>();
			cl = new HashSet<CtTypeReference<?>>();
			
			itf = c.getType().getSuperInterfaces();
			itf.add(c.getType().getSuperclass());
			for(CtTypeReference<?> type : itf){
				//System.out.println("itf : " + type.getSimpleName());
				//System.out.println("itf : " + type);
				for(CtExecutableReference<?> e : type.getDeclaredExecutables()){
					methods.put(e.getActualMethod(), type);
					System.out.println(e.getActualMethod() + ":" +  type);
				}
				//System.out.println(type.getDeclaredExecutables());
				//for(CtMethod<?> m : type.getSimpleName())
				//methods.put(key, type);
			}
			
			
		}
	
		/*for(CtVariable<?> c : variables){
			System.out.println(c.getType());
			if(c.getDefaultExpression() != null){
				System.out.println(c.getType().getSuperInterfaces());
				System.out.println(c.getType().getSuperclass());
			}
		}*/
	 }
}
