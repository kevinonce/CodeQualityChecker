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
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>>{

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private Set<CtTypeReference<?>> itf = new HashSet<CtTypeReference<?>>();
	private List<CtInvocation<?>> invocations = new LinkedList<CtInvocation<?>>();
	private Map<String, CtTypeReference<?>> methods = new HashMap<String, CtTypeReference<?>>();
	Map<CtTypeReference<?>, List<String>> methodsByClass = new HashMap<CtTypeReference<?>, List<String>>();
	
	

	
	@Override
	public void process(CtClass<?> ctClass) {
		//elementList = ctClass.getElements(new FilterImpl());
		variables.addAll(Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class)));
		invocations.addAll(Query.getElements(ctClass, new TypeFilter<CtInvocation<?>>(CtInvocation.class)));
	}
	
	@Override
	 public void processingDone() {

		for(CtVariable<?> c : variables){
			System.out.println("v : " + c.getSignature());
			itf = new HashSet<CtTypeReference<?>>();
			
//			if(c.getType().getSuperclass() != null)
//				itf.add(c.getType().getSuperclass());
			
			if(c.getType().getSuperInterfaces() != null)
				itf.addAll(c.getType().getSuperInterfaces());
			
//			System.out.println(itf);
			List<String> listMethods;

			for(CtTypeReference<?> type : itf){
				//System.out.println("itf : " + type.getSimpleName());
				//System.out.println("itf : " + type);
				listMethods = new LinkedList<String>();

				for(CtExecutableReference<?> e : type.getDeclaredExecutables()){
					if(!e.isConstructor()){
						String method = e.getSimpleName();
						for(CtTypeReference<?> r : e.getParameters())
							method += ":" + r.getSimpleName();
						methods.put(method, type);
						listMethods.add(method);
//						System.out.println(method + ":" +  type);
					}
				}
				System.out.println(type + " : " + listMethods);
				methodsByClass.put(type, listMethods);

			}
			
			//System.out.println(c);
		}
		/*System.out.println(methods);
		for(CtInvocation<?> c : invocations){
			for(String m : methods.keySet()){
				if(m != null && c.getExecutable().getActualMethod() != null){
//				if(m.toString().split(".")[3].equals(c.getExecutable().getActualMethod().toString().split(".")[3]))
//					System.out.print(m);
//					System.out.print(" <=> ");
					String invMethod = c.getExecutable().getSimpleName();
					for(CtTypeReference<?> r : c.getExecutable().getParameters()){
//							System.out.println(r.getSimpleName());
							invMethod += ":" + r.getActualClass().getSimpleName();
					}
//					System.out.print(invMethod+"\n");
					if(m.equals(invMethod)){
//						System.out.println("Match !");
//						System.out.println(m + " <=> " + c.getExecutable().getActualMethod());
//						System.out.println(c.getPosition());
						if(!c.getExecutable().getDeclaringType().getQualifiedName().equals(methods.get(m)))
							System.out.println("---------------------\nVariable declaration type mistake : " + c.getPosition() + "\nused " + c.getExecutable().getDeclaringType().getQualifiedName() + "\ninstead of : " + methods.get(m));
//							System.out.println(c.getExecutable().getActualMethod());

					}
				}
			}
			//System.out.println(c.getExecutable().getActualMethod());
			//System.out.println(c.getExecutable().getSimpleName());
			//System.out.println(c.getTarget());
		}
		
		*/
	 }
}

class InvocationsByVariableFilter implements Filter<CtInvocation<?>> {

	@Override
	public boolean matches(CtInvocation<?> c) {
//		c.get
		return false;
	}

}
