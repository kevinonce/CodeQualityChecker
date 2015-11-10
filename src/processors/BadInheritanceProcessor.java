package processors;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>>{

	private List<CtVariable<?>> variables;
	private Set<CtTypeReference<?>> itf; 
	private List<CtInvocation<?>> invocations;
	private Map<Method, CtTypeReference<?>> methods;
	
	@Override
	public void process(CtClass<?> ctClass) {
		//elementList = ctClass.getElements(new FilterImpl());
		variables = Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class));
		methods = new HashMap<Method, CtTypeReference<?>>();
		invocations = Query.getElements(ctClass, new TypeFilter<CtInvocation<?>>(CtInvocation.class));

	}
	
	@Override
	 public void processingDone() {

		for(CtVariable<?> c : variables){
			//System.out.println("v : " + c.getSignature());
			itf = new HashSet<CtTypeReference<?>>();
			
			itf = c.getType().getSuperInterfaces();
			itf.add(c.getType().getSuperclass());
			for(CtTypeReference<?> type : itf){
				//System.out.println("itf : " + type.getSimpleName());
				//System.out.println("itf : " + type);
				for(CtExecutableReference<?> e : type.getDeclaredExecutables()){
					if(e.getActualMethod() != null){
						methods.put(e.getActualMethod(), type);
						//System.out.println(e.getActualMethod() + ":" +  type);
					}
				}
			}
			
			//System.out.println(c);
		}
		
		for(CtInvocation<?> c : invocations){
			for(Method m : methods.keySet()){
				if(m != null && c.getExecutable().getActualMethod() != null){
//				if(m.toString().split(".")[3].equals(c.getExecutable().getActualMethod().toString().split(".")[3]))
					System.out.print(m.getName());
						for(Parameter p : m.getParameters()){
							System.out.print(p.getType().getSimpleName() + ",");
						}
					System.out.println(")"  + " <=> " + c.getExecutable().getActualMethod().getName());
				}
			}
			//System.out.println(c.getExecutable().getActualMethod());
			//System.out.println(c.getExecutable().getSimpleName());
			//System.out.println(c.getTarget());
		}
		
		
	 }
}
