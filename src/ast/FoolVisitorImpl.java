package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import parser.*;
import parser.FOOLParser.AsmContext;
import parser.FOOLParser.BaseExpContext;
import parser.FOOLParser.BoolValContext;
import parser.FOOLParser.ClassExpContext;
import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.DecContext;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FactorContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.FunDeclarationContext;
import parser.FOOLParser.FunExpContext;
import parser.FOOLParser.IfStmContext;
import parser.FOOLParser.IfExpContext;
import parser.FOOLParser.IntValContext;
import parser.FOOLParser.LetContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.MethodExpContext;
import parser.FOOLParser.NewContext;
import parser.FOOLParser.SingleExpContext;
import parser.FOOLParser.StmContext;
import parser.FOOLParser.StmsContext;
import parser.FOOLParser.TermContext;
import parser.FOOLParser.ThisContext;
import parser.FOOLParser.TypeContext;
import parser.FOOLParser.VarAssignmentContext;
import parser.FOOLParser.VarExpContext;
import parser.FOOLParser.VarasmContext;
import parser.FOOLParser.VardecContext;
import sun.net.ProgressListener;
import util.SemanticError;

public class FoolVisitorImpl extends FOOLBaseVisitor<Node> {
	
	
	
	@Override
	public Node visitClassdec(ClassdecContext ctx) {
		ClassNode res;
		System.out.println("AAAAA");

		if (ctx.ID(1)!=null) {
			res=new ClassNode(ctx.ID(0).getText(),ctx.ID(1).getText());
		}
		else {
			res=new ClassNode(ctx.ID(0).getText());
		}
		
		for(VardecContext vardecCtx:ctx.vardec()) {
			ParNode parNode=new ParNode(vardecCtx.ID().getText(),visit(vardecCtx.type()));
			res.addParameterNode(parNode);
		}
		if (ctx.fun()!=null) {
			for (FunContext funCtx:ctx.fun()) {
				res.addMetNode(visit(funCtx));
			}
		}
		
		
		return res;
	}
	@Override
	public Node visitLetInExp(LetInExpContext ctx) {
		
		//resulting node of the right type
		Node res;		
		//list of declarations in @res
		ArrayList<Node> declarations = new ArrayList<Node>();
		//visit all nodes corresponding to declarations inside the let context and store them in @declarations
		//notice that the ctx.let().dec() method returns a list, this is because of the use of * or + in the grammar
		//antlr detects this is a group and therefore returns a list
		
		for(DecContext dc : ctx.let().dec()){
			declarations.add( visit(dc) );
		}
		
		//visit exp context
		if (ctx.stms()!=null) {
			ArrayList<Node>stmsNode=new ArrayList<>();
			for (StmContext stm: ctx.stms().stm()) {
				stmsNode.add(visit(stm));
			}
			res=new ProgStmNode(declarations, stmsNode);
		}
		else {
			Node exp = visit( ctx.exp());
			res=new ProgExpNode(declarations, exp);
		}
		//build @res accordingly with the result of the visits to its content
		
		return res;
	}
	
	@Override
	public Node visitSingleExp(SingleExpContext ctx) {
		
		//simply return the result of the visit to the inner exp
		return visit(ctx.exp());
		
	}
	
	
	@Override
	public Node visitVarasm(VarasmContext ctx) {
		
		//declare the result node
		//VarNode result;
		
		//visit the type
		Node typeNode = visit(ctx.vardec().type());
		
		//visit the exp
		Node expNode = visit(ctx.exp());
		
		//build the varNode
		return new VarNode(ctx.vardec().ID().getText(), typeNode, expNode);
	}

	
	/*@Override
	public Node visitClas(ClasContext ctx) {
		String clId=ctx.clId.getText();
		String clExt=ctx.extId==null?null:ctx.extId.getText();
		ArrayList<Node> typeFields=new ArrayList<Node>();
		ArrayList<Node> expFields=new ArrayList<Node>();
		ArrayList<Node> varAsms=new ArrayList<Node>();
		if (ctx.varasm()!=null) {
			for (VarasmContext var : ctx.varasm()) {
				typeFields.add(visit(var.vardec().type()));
				expFields.add(visit(var.exp()));
				varAsms.add(visit(var));
			}
		}
		ArrayList<Node> methods=new ArrayList<Node>();
		if (ctx.fun()!=null) {
			for (FunContext fun : ctx.fun()) {
				methods.add(visit(fun));
			}
		}
		return new CreateClassNode(clId,clExt,varAsms,typeFields,methods);
		return null;
	}*/
	
	
	
	@Override
	public Node visitFun(FunContext ctx) {
		
		//initialize @res with the visits to the type and its ID
		Node res;
		ArrayList<Node> paramsNode=new ArrayList<>();
		ArrayList<Node> innerDec = new ArrayList<Node>();
		ArrayList<Node> body=new ArrayList<>();
		
		
		//add argument declarations
		//we are getting a shortcut here by constructing directly the ParNode
		//this could be done differently by visiting instead the VardecContext
		for(VardecContext vc : ctx.vardec())
			paramsNode.add( new ParNode(vc.ID().getText(), visit( vc.type() )) );
		
		//add body
		//create a list for the nested declarations
		
		//check whether there are actually nested decs
		if(ctx.let() != null){
			//if there are visit each dec and add it to the @innerDec list
			for(DecContext dc : ctx.let().dec())
				innerDec.add(visit(dc));
		}
		
		//get the exp body
		if (ctx.exp()!=null) {
			body.add(visit(ctx.exp()));
		}
		else {
			for (StmContext stm: ctx.stms().stm()) {
				body.add(visit(stm));
			}
		}
		
		if (ctx.type()!=null) {
			res=visit(ctx.type());
		}
		else {
			res=new VoidNode();
		}
		
		res=new FunNode(ctx.ID().getText(),res,paramsNode,innerDec,body);
		//add the body and the inner declarations to the function
		//res.addDecBody(innerDec, exp);
		
		return res;		
		
	}
	
	@Override
	public Node visitType(TypeContext ctx) {
		Node res;
		if(ctx.getText().equals("int"))
			 res=new IntTypeNode();
		else if(ctx.getText().equals("bool"))
			 res=new BoolTypeNode();
		else
			 res=new ClassTypeNode(ctx.getText());
			//return new ClassTypeNode();
		
		//this will never happen thanks to the parser
		return res;

	}
	
	@Override
	public Node visitExp(ExpContext ctx) {
		Node res=null;
		//this could be enhanced
		
		//check whether this is a simple or binary expression
		//notice here the necessity of having named elements in the grammar
		if (ctx.NOT()!=null) {
			res=new NotNode(visit(ctx.left));
		}
		else {
			if (ctx.right!=null) {
				if (ctx.PLUS()!=null) {
					res=new PlusNode(visit(ctx.left),visit(ctx.right));
				}
				else {
					res=new MinusNode(visit(ctx.left), visit(ctx.right));
				}
			}
			else {
				res=visit(ctx.left);
				if (ctx.MINUS(0)!=null) {
					res=new MinusNode(new IntNode(0),res);
				}
				
			}
		}
		return res;
		
		
		
	}
	
	@Override
	public Node visitTerm(TermContext ctx) {
		//check whether this is a simple or binary expression
		//notice here the necessity of having named elements in the grammar
		Node res;
		if(ctx.right == null){
			//it is a simple expression
			res= visit( ctx.left );
		}
		else {
			res=new MultNode(visit(ctx.left), visit(ctx.right));
		}
		return res;
	}
	
	
	@Override
	public Node visitFactor(FactorContext ctx) {
		//check whether this is a simple or binary expression
		//notice here the necessity of having named elements in the grammar
		Node res;
		if(ctx.right == null){
			//it is a simple expression
			res=visit( ctx.left );
		}else{
			if (ctx.OR()!=null) {
				res=new OrNode(visit(ctx.left), visit(ctx.right));
			}
			else if (ctx.AND()!=null) {
				res=new AndNode(visit(ctx.left), visit(ctx.right));
			}
			else if (ctx.EQ()!=null) {
				res=new EqualNode(visit(ctx.left), visit(ctx.right));
			}
			else if (ctx.GEQ()!=null) {
				res=new GEqualNode(visit(ctx.left), visit(ctx.right));
			}
			else {
				res=new LEqualNode(visit(ctx.left), visit(ctx.right));
			}
		}
		return res;
	}
	
	
	@Override
	public Node visitIntVal(IntValContext ctx) {
		// notice that this method is not actually a rule but a named production #intVal
		
		//there is no need to perform a check here, the lexer ensures this text is an int
		return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
	}
	
	@Override
	public Node visitBoolVal(BoolValContext ctx) {
		
		//there is no need to perform a check here, the lexer ensures this text is a boolean
		return new BoolNode(Boolean.parseBoolean(ctx.getText())); 
	}
	
	@Override
	public Node visitBaseExp(BaseExpContext ctx) {
		
		//this is actually nothing in the sense that for the ast the parenthesis are not relevant
		//the thing is that the structure of the ast will ensure the operational order by giving
		//a larger depth (closer to the leafs) to those expressions with higher importance
		
		//this is actually the default implementation for this method in the FOOLBaseVisitor class
		//therefore it can be safely removed here
		
		return visit(ctx.exp());

	}
	
	@Override
	public Node visitIfExp(IfExpContext ctx) {
		
		//create the resulting node
		IfNode res;
		
		//visit the conditional, then the then branch, and then the else branch
		//notice once again the need of named terminals in the rule, this is because
		//we need to point to the right expression among the 3 possible ones in the rule
		
		Node condExp = visit (ctx.cond);
		
		Node thenExp = visit (ctx.thenBranch);
		
		Node elseExp=null;
		if (ctx.elseBranch!=null)
			elseExp = visit (ctx.elseBranch);
		
		//build the @res properly and return it
		res = new IfNode(condExp, thenExp, elseExp);
		
		return res;
	}
	
	@Override
	public Node visitVarExp(VarExpContext ctx) {
		
		//this corresponds to a variable access
		return new IdNode(ctx.ID().getText());

	}
	
	@Override
	public Node visitFunExp(FunExpContext ctx) {
		//this corresponds to a function invocation

		//declare the result
		Node res;

		//get the invocation arguments
		ArrayList<Node> args = new ArrayList<Node>();
		
		for(ExpContext exp : ctx.exp())
			args.add(visit(exp));
		
		//especial check for stdlib func
		//this is WRONG, THIS SHOULD BE DONE IN A DIFFERENT WAY
		//JUST IMAGINE THERE ARE 800 stdlib functions...
		if(ctx.ID().getText().equals("print"))
			res = new PrintNode(args.get(0));
		
		else
			//instantiate the invocation
			res = new CallNode(ctx.ID().getText(), args);
		
		return res;
	}
	
	@Override
	public Node visitAsm(AsmContext ctx) {
		// TODO Auto-generated method stub

		return new AssNode(ctx.ID().getText(),visit(ctx.exp()),ctx);
	}

	

	@Override
	public Node visitVarAssignment(VarAssignmentContext ctx) {
		// TODO Auto-generated method stub

		Node typeNode=visit(ctx.varasm().vardec().type());
		Node expNode=visit(ctx.varasm().exp());
		
		return new VarNode(ctx.varasm().vardec().ID().getText(),typeNode,expNode);
	}

	@Override
	public Node visitFunDeclaration(FunDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return super.visitFunDeclaration(ctx);
	}

	/*@Override
	public Node visitPrefExp(PrefExpContext ctx) {
		// TODO Auto-generated method stub
		return super.visitPrefExp(ctx);
	}*/

	/*@Override
	public Node visitOpExp(OpExpContext ctx) {
		// TODO Auto-generated method stub
		return super.visitOpExp(ctx);
	}*/

	@Override
	public Node visitMethodExp(MethodExpContext ctx) {
		// TODO Auto-generated method stub
		Node res;
		ArrayList<Node> listParamaters=new ArrayList<>();
		for (ExpContext expContext:ctx.exp()) {
			listParamaters.add(visit(expContext));
		}
		if(ctx.THIS()==null) {
			res=new MetExpNode(ctx.ID(0).getText(), ctx.ID(1).getText(),listParamaters);
		}
		else {
			res=new MetExpNode(ctx.ID(0).getText(),listParamaters);
		}
		
		return res;
	}

	@Override
	public Node visitIfStm(IfStmContext ctx) {
		// TODO Auto-generated method stub
		Node res;
		
		ArrayList<Node> listThenStm=new ArrayList<>();
		ArrayList<Node> listElseStm=new ArrayList<>();
		Node expNode;
		for(StmContext stmContext:ctx.thenBranch.stm()) {
			listThenStm.add(visit(stmContext));
		}
		
		if(ctx.elseBranch!=null) {
		for (StmContext stmContext: ctx.elseBranch.stm()) {
			listElseStm.add(visit(stmContext));
		}
		}
		expNode=visit(ctx.exp());
		res=new IfStmNode(expNode, listThenStm, listElseStm);
		
		return res;
	}


	@Override
	public Node visitClassExp(ClassExpContext ctx) {
		// TODO Auto-generated method stub
		
		ProgExpClassNode resExp;
		ProgStmClassNode resStm;
		boolean isResExp;
		ArrayList<Node> listDeclaration=new ArrayList<>();
		ArrayList<Node> listStm=new ArrayList<>();
		Node expNode;
		
		
		if (ctx.exp()!=null) {
			isResExp=true;
			expNode=visit(ctx.exp());
			
			
			if (ctx.let()!=null) {
				for (DecContext decContext:ctx.let().dec()) {
					listDeclaration.add(visit(decContext));
				}
				resExp=new ProgExpClassNode(listDeclaration, expNode);
			}
			else {
				resExp=new ProgExpClassNode(expNode);
			}
			for (ClassdecContext classdecCtx:ctx.classdec()) {
				resExp.addClassNode(visit(classdecCtx));
			}
			return resExp;
		}
		else {
			isResExp=false;
			for(StmContext stm:ctx.stms().stm()) {
				listStm.add(visit(stm));
			}
				
			if (ctx.let()!=null) {
				for (DecContext decContext:ctx.let().dec()) {
					listDeclaration.add(visit(decContext));
				}
				resStm=new ProgStmClassNode(listDeclaration, listStm);
			}
			else {
				resStm=new ProgStmClassNode(listStm);
			}
			for (ClassdecContext classdecCtx:ctx.classdec()) {
				resStm.addClassNode(visit(classdecCtx));
			}
			return resStm;

		}
	}

	@Override
	public Node visitNew(NewContext ctx) {
		// TODO Auto-generated method stub
		Node res;
		ArrayList<Node> listParameters=new ArrayList<>();
		for(ExpContext expContext:ctx.exp()) {
			listParameters.add(visit(expContext));
	
		}
		res=new NewNode(ctx.ID().getText(), listParameters);
		return res;
	}

	@Override
	public Node visitThis(ThisContext ctx) {
		// TODO Auto-generated method stub
		return new IdNode("this");
	}
	
	
}
