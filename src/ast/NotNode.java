package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class NotNode implements Node{

	private Node left;
	
	public NotNode(Node not) {
		this.left=not;
	}
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return indent+"Not \n"+left.toPrint(indent+" ");
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		if(!(left instanceof BoolTypeNode)) {
			System.out.println("Non boolean in not");
			System.exit(0);
		}
		return new BoolTypeNode();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		
		String l1 = FOOLlib.freshLabel();
        String l2 = FOOLlib.freshLabel();
        
        return left.codeGeneration()+ "push 1\n" + 
        		"beq " + l1 + "\n"+
        		"push 1\n" +
        		"b " + l2 + "\n"+ 
        		l1 + ":\n" + 
        		"push 0\n" + 
        		l2 +":\n";
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		ArrayList<SemanticError> res=new ArrayList<>();
		res.addAll(left.checkSemantics(env));
		
		return res;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node not) {
		this.left = not;
	}

	
}
