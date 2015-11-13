package processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>>{

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private List<CtInvocation<?>> invocations;
	

	
	@Override
	public void process(CtClass<?> ctClass) {
		variables.addAll(Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class)));
	}
	
	@Override
	 public void processingDone() {

		for(CtVariable<?> c : variables){
			invocations = c.getParent(CtClass.class).getElements(new InvocationsByVariableFilter(c));
			int cpt = 0;
				//appel de fonction sur une variable
				for(CtInvocation<?> i : invocations){
//					System.out.println("Invocation : " + i + " : " + i.getTarget().getType().getSuperInterfaces());
					
					// pour chaque super-interface des classes de la variable
					for(CtTypeReference<?> r : i.getTarget().getType().getSuperInterfaces()){
						System.out.println("-----Classe : " + r.getSimpleName());
						
						//pour chaque méthode de la super-interface
						for(CtExecutableReference<?> e : r.getDeclaredExecutables()){
//							System.out.println(i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1] 
//									+ " ----------------- " + e.toString().split("#")[1]);
//							System.out.println(i.getExecutable().getDeclaringType().getQualifiedName() + " ++++++++++ " + e.getDeclaringType());
							if(i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1].equals(e.toString().split("#")[1]) 
									&& !i.getExecutable().getDeclaringType().getQualifiedName().equals(e.getDeclaringType())){
								System.out.println(i + "\n" + i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1] + " <=> " + e.toString().split("#")[1]);
								cpt++;
								System.out.println(r.getQualifiedName());
//							System.out.println(i.getExecutable().toString().replace("E", "java.lang.Object") + " ========== " + e);
//							System.out.println(e.getSimpleName() + " " + e.getParameters());
//							System.out.println("invocation : " + i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1]);
							} 
						}
//						System.out.println(r.getSimpleName());
						
//						System.out.println("invocation : " + i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1]);
				}
			}
			if(cpt == invocations.size())
				System.out.println("OK");
//			System.out.println(c.getSimpleName() + " : " + c.getType().getQualifiedName() + " <=> "+ c.getParent(CtClass.class).getElements(new InvocationsByVariableFilter(c)));
			
		}
		 }
}

class InvocationsByVariableFilter implements Filter<CtInvocation<?>> {
	
	private CtVariable<?> variable;
	
	public InvocationsByVariableFilter(CtVariable<?> v) {
		this.variable = v;
	}

	public boolean matches(CtInvocation<?> inv) {
		if(inv.getTarget() != null 
				&& this.variable != null 
				&& this.variable.getType().getSimpleName().equals(inv.getTarget().getType().getSimpleName()))
			return true;
		return false;
	}

}
