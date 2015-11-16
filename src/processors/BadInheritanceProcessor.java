package processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import filter.InvocationsByVariableFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>> {

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private List<CtInvocation<?>> invocations;
	private Set<CtTypeReference<?>> supItf = new HashSet<CtTypeReference<?>>();
	private Map<String, List<String>> mapItf = new HashMap<String, List<String>>();
	private List<String> possibleCorrections;

	@Override
	public void process(CtClass<?> ctClass) {
		variables.addAll(Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class)));
	}

	@Override
	public void processingDone() {
		int cpt = 0;
		for (CtVariable<?> c : variables) {
			if (c.getType() != null && !c.getType().isPrimitive()) {
				possibleCorrections = new LinkedList<String>();
				supItf = new HashSet<CtTypeReference<?>>();
				mapItf = new HashMap<String, List<String>>();
				invocations = c.getParent(CtClass.class).getElements(new InvocationsByVariableFilter(c));
				List<String> listInv = new LinkedList<String>();
				for (CtInvocation<?> i : invocations) {
					listInv.add(i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1]);
				}
				try {
					supItf.addAll(c.getType().getSuperInterfaces());
					boolean allIn = true;
					Set<CtTypeReference<?>> tmpItf = new HashSet<CtTypeReference<?>>();
					tmpItf.addAll(supItf);
					while (allIn && !supItf.isEmpty()) {
						for (CtTypeReference<?> r : tmpItf) {
							if (!r.getSuperInterfaces().isEmpty() && !supItf.containsAll(r.getSuperInterfaces())) {
								supItf.addAll(r.getSuperInterfaces());
								allIn = true;
							} else
								allIn = false;
						}
						tmpItf.addAll(supItf);
					}
					for (CtTypeReference<?> r : supItf) {
						List<String> listItf = new LinkedList<String>();
						for (CtExecutableReference<?> e : r.getDeclaredExecutables()) {
							listItf.add(e.toString().split("#")[1]);
						}
						mapItf.put(r.getQualifiedName(), listItf);
					}

					for (String str : mapItf.keySet()) {
						if (!listInv.isEmpty() && mapItf.get(str).containsAll(listInv)
								&& !c.getType().getQualifiedName().equals(str)) {

							possibleCorrections.add(str);
						}
					}
					if (!possibleCorrections.isEmpty()) {
						cpt++;
						System.out.println("---------------TROUVÉ---------------");
						System.out.println("Source : " + c.getPosition());
						System.out.println("Variable : " + c);
						System.out.print("Correction(s) possible(s) : \n");
						for (String s : possibleCorrections)
							System.out.println("	- " + s);
					}
				} catch (SpoonClassNotFoundException e) {
					System.out.println("Cannot load class " + e);
				}
			}
		}
		System.out.println("====================\nAnalyse terminée !");
		System.out.println(cpt + " corrections possibles identifiées !");

	}
}
