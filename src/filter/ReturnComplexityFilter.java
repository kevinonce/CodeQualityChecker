package filter;

import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.visitor.Filter;

public class ReturnComplexityFilter implements Filter<CtReturn<?>>{

	@Override
	public boolean matches(CtReturn<?> arg0) {
		CtExecutable<?> parent = arg0.getParent(CtExecutable.class);
		
		//si le return n'est pas directement contenu dans le bloc de la methode principale		
		// si le dernier statement de la methode et le return courant sont égaux
		if( (!(arg0.getParent() != parent.getBody())) && parent.getBody().getLastStatement().equals(arg0)){
			if(parent.getBody().getLastStatement().getPosition().getLine() == arg0.getPosition().getLine()){
				return false;
			}
		}
		
		return true;
	}

}
