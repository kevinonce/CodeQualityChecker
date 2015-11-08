package processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtExecutable;

public class DuplicatedBlockProcessor extends AbstractProcessor<CtBlock<?>> {

	Map<Integer, List<CtBlock<?>>> blockHash = new HashMap<>();
	private static final int NBR_STATEMENT_MIN = 10;
	
	@Override
	public void process(CtBlock<?> arg0) {
			Integer hash = arg0.hashCode();
			List<CtBlock<?>> liste = blockHash.get(hash);
			if(liste == null){
				liste = new ArrayList<>();
				blockHash.put(hash, liste);
			}
			liste.add(arg0);
		
	}
	
	 @Override
	 public void processingDone() {
		 Set<Integer> keys = blockHash.keySet();
		 System.out.println("\n-------Block duplication-------\n");

		 for(Integer key : keys){
			 List<CtBlock<?>> liste = blockHash.get(key);
			 if(liste.size() > 1 && liste.get(0).getStatements().size() > NBR_STATEMENT_MIN){
				 System.out.println("Dupliction block found in: ");
				 for(CtStatement statement : liste){
					 System.out.print("\t");
					 System.out.println(statement.getPosition());
					 if(statement.getParent(CtExecutable.class) != null){
						 System.out.println(statement.getParent(CtExecutable.class));
					 }else{
						 System.out.println(statement.toString());				 
					 }				 }
				 System.out.println();
			 }
		 }
	 }
}
