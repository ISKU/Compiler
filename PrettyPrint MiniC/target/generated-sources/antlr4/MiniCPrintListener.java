import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Pretty Print MiniC Compiler 
 * 컴파일러개론 201201356 김민호
 * @author KMH
 */
public class MiniCPrintListener extends MiniCBaseListener {

	private final String SPACE = "....";

	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
	private int depth = 0;
	private boolean braceForElse = false;

	@Override
	public void exitProgram(MiniCParser.ProgramContext ctx) {
		StringBuilder newCode = new StringBuilder();
		for (int index = 0, count = ctx.getChildCount(); index < count; index++)
			newCode.append(newTexts.get(ctx.getChild(index)) + "\n");
		System.out.print(newCode.toString());
	}

	@Override
	public void exitDecl(MiniCParser.DeclContext ctx) {
		newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
	}

	@Override
	public void exitVar_decl(MiniCParser.Var_declContext ctx) {
		StringBuilder code = new StringBuilder();
		int childCount = ctx.getChildCount() - 1;

		if (childCount < 5)
			appendCode(4, code, ctx, 0, childCount);
		else {
			appendCode(3, code, ctx, 0, 1);
			appendCode(0, code, ctx, 2, childCount);
		}

		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
		newTexts.put(ctx, new StringBuilder()
				.append(ctx.type_spec().getText() + " ")
				.append(ctx.IDENT().getText())
				.append(ctx.getChild(2).getText())
				.append(newTexts.get(ctx.params()))
				.append(ctx.getChild(4).getText())
				.append(newTexts.get(ctx.compound_stmt()))
				.toString());
	}

	@Override
	public void exitType_spec(MiniCParser.Type_specContext ctx) {
		newTexts.put(ctx, ctx.getText());
	}

	@Override
	public void exitParams(MiniCParser.ParamsContext ctx) {
		StringBuilder code = new StringBuilder();
		int childCount = ctx.getChildCount() - 1;

		if (childCount >= 1) {
			code.append(newTexts.get(ctx.param(0)));
			for (int index = 1, params = ctx.param().size(); index < params; index++)
				code.append(", " + newTexts.get(ctx.param(index)));
		} else
			appendCode(0, code, ctx, 0, childCount);

		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitParam(MiniCParser.ParamContext ctx) {
		StringBuilder code = new StringBuilder(newTexts.get(ctx.type_spec()) + " ");
		for (int index = 1, childCount = ctx.getChildCount(); index < childCount; index++)
			code.append(ctx.getChild(index).getText());
		newTexts.put(ctx, code.toString());
	}

	@Override
	public void enterCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		if (!braceForElse)
			depth++;
	}

	@Override
	public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		StringBuilder code = new StringBuilder();

		code.append("\n" + indent(true) + ctx.getChild(0).getText() + "\n");
		for (int index = 0, decls = ctx.local_decl().size(); index < decls; index++)
			code.append(indent(false) + newTexts.get(ctx.local_decl(index)) + "\n");
		if (ctx.local_decl().size() != 0)
			code.append("\n");
		for (int index = 0, stmts = ctx.stmt().size(); index < stmts; index++)
			code.append(indent(false) + newTexts.get(ctx.stmt(index)) + "\n");
		code.append(indent(true) + ctx.getChild(ctx.getChildCount() - 1).getText());

		if (!braceForElse)
			depth--;
		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
		StringBuilder code = new StringBuilder();
		int childCount = ctx.getChildCount() - 1;

		if (childCount < 5)
			appendCode(4, code, ctx, 0, childCount);
		else {
			appendCode(3, code, ctx, 0, 1);
			appendCode(0, code, ctx, 2, childCount);
		}

		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitStmt(MiniCParser.StmtContext ctx) {
		newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
	}

	@Override
	public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
		newTexts.put(ctx, new StringBuilder()
				.append(newTexts.get(ctx.expr()))
				.append(ctx.getChild(1).getText())
				.toString());
	}

	@Override
	public void exitExpr(MiniCParser.ExprContext ctx) {
		StringBuilder code = new StringBuilder();
		
		if (isAssignmentOperation(ctx)) {
			code.append(ctx.getChild(0).getText() + " ")
				.append(ctx.getChild(1).getText() + " ")
				.append(newTexts.get(ctx.expr(0)));
		} 
		else if (isBinaryOperation(ctx)) {
			code.append(newTexts.get(ctx.expr(0)) + " ")
				.append(ctx.getChild(1).getText() + " ")
				.append(newTexts.get(ctx.expr(1)));
		} 
		else if (isPrefixOperation(ctx)) {
			code.append(ctx.getChild(0).getText())
				.append(newTexts.get(ctx.expr(0)));
		} 
		else if (isBracketOperation(ctx)) {
			code.append(ctx.getChild(0).getText())
				.append(newTexts.get(ctx.expr(0)))
				.append(ctx.getChild(2).getText());
		} 
		else if (isArrayOperation(ctx)) {
			code.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.expr(0)))
				.append(ctx.getChild(3).getText());
		} 
		else if (isFunctionOperation(ctx)) {
			code.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.args()))
				.append(ctx.getChild(3).getText());
		} 
		else if (isArrayAssignmentOperation(ctx)) {
			code.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.expr(0)))
				.append(ctx.getChild(3).getText() + " ")
				.append(ctx.getChild(4).getText() + " ")
				.append(newTexts.get(ctx.expr(1)));
		} 
		else if (ctx.getChildCount() == 1)
			code.append(ctx.getText());
		
		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitArgs(MiniCParser.ArgsContext ctx) {
		StringBuilder code = new StringBuilder();
		int childCount = ctx.getChildCount() - 1;

		if (childCount >= 1) {
			code.append(newTexts.get(ctx.expr(0)));
			for (int index = 1, args = ctx.expr().size(); index < args; index++)
				code.append(", " + newTexts.get(ctx.expr(index)));
		} else
			appendCode(0, code, ctx, 0, childCount);

		newTexts.put(ctx, code.toString());
	}

	@Override
	public void enterWhile_stmt(MiniCParser.While_stmtContext ctx) {
		if (ctx.stmt().compound_stmt() == null)
			depth++;
	}

	@Override
	public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
		newTexts.put(ctx, new StringBuilder()
				.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.expr()))
				.append(ctx.getChild(3).getText())
				.append(ctx.stmt().compound_stmt() == null ? whileBraceStmt(ctx) : newTexts.get(ctx.stmt()))
				.toString());

		if (ctx.stmt().compound_stmt() == null)
			depth--;
	}

	@Override
	public void enterIf_stmt(MiniCParser.If_stmtContext ctx) {
		if (ctx.stmt(0).compound_stmt() == null)
			depth++;
		if (isElseCompoundStmt(ctx))
			braceForElse = true;
	}

	@Override
	public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
		StringBuilder code = new StringBuilder();

		if (ctx.getChildCount() < 7) {
			code.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.expr()))
				.append(ctx.getChild(3).getText())
				.append(ctx.stmt(0).compound_stmt() == null ? ifBraceStmt(ctx, 0) : newTexts.get(ctx.stmt(0)));
		} else {
			code.append(ctx.getChild(0).getText())
				.append(ctx.getChild(1).getText())
				.append(newTexts.get(ctx.expr()))
				.append(ctx.getChild(3).getText())
				.append(ctx.stmt(0).compound_stmt() == null ? ifBraceStmt(ctx, 0) : newTexts.get(ctx.stmt(0)));

			if (ctx.stmt(0).compound_stmt() != null)
				depth++;
			
			code.append("\n" + indent(true) + ctx.getChild(5).getText())
				.append(ctx.stmt(1).compound_stmt() == null ? ifBraceStmt(ctx, 1) : newTexts.get(ctx.stmt(1)));

			if (isElseCompoundStmt(ctx))
				braceForElse = false;
			if (ctx.stmt(0).compound_stmt() != null)
				depth--;
		}

		if (ctx.stmt(0).compound_stmt() == null)
			depth--;
		newTexts.put(ctx, code.toString());
	}

	@Override
	public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
		newTexts.put(ctx, 
				(ctx.getChildCount() == 2)
				? ctx.getChild(0).getText() + ctx.getChild(1).getText()
				: ctx.getChild(0).getText() + " " + newTexts.get(ctx.expr()) + ctx.getChild(2).getText());
	}

	private String whileBraceStmt(MiniCParser.While_stmtContext ctx) {
		return new StringBuilder()
				.append("\n" + indent(true) + "{\n")
				.append(indent(false) + newTexts.get(ctx.stmt()))
				.append("\n" + indent(true) + "}")
				.toString();
	}

	private String ifBraceStmt(MiniCParser.If_stmtContext ctx, int stmtIndex) {
		return new StringBuilder()
				.append("\n" + indent(true) + "{\n")
				.append(indent(false) + newTexts.get(ctx.stmt(stmtIndex)))
				.append("\n" + indent(true) + "}")
				.toString();
	}
	
	private boolean isElseCompoundStmt(MiniCParser.If_stmtContext ctx) {
		return (ctx.getChildCount() > 6)
				&& (ctx.stmt(0).compound_stmt() == null) 
				&& (ctx.stmt(1).compound_stmt() != null);
	}

	private boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 3) 
				&& (ctx.getChild(1) != ctx.expr());
	}

	private boolean isPrefixOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 2);
	}

	private boolean isAssignmentOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 3) 
				&& (ctx.getChild(1).getText().equals("="));
	}

	private boolean isBracketOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChild(0).getText().equals("(")) 
				&& (ctx.getChild(2).getText().equals(")"));
	}

	private boolean isArrayOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 4) 
				&& (ctx.getChild(1).getText().equals("["))
				&& (ctx.getChild(3).getText().equals("]"));
	}

	private boolean isFunctionOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 4)
				&& (ctx.getChild(1).getText().equals("("))
				&& (ctx.getChild(3).getText().equals(")"));
	}

	private boolean isArrayAssignmentOperation(MiniCParser.ExprContext ctx) {
		return (ctx.getChildCount() == 6)
				&& (ctx.getChild(4).getText().equals("="));
	}

	private String indent(boolean brace) {
		StringBuilder code = new StringBuilder();
		for (int depth = 1; depth < this.depth; depth++)
			code.append(SPACE);
		code.append(brace ? "" : SPACE);
		return code.toString();
	}

	private void appendCode(int option, StringBuilder code, ParserRuleContext ctx, int start, int end) {
		switch (option) {
		case 0:
			while (start <= end)
				code.append(ctx.getChild(start++).getText());
			return;
		case 1:
			while (start <= end)
				code.append(" " + ctx.getChild(start++).getText());
			return;
		case 2:
			while (start <= end)
				code.append(ctx.getChild(start++).getText() + " ");
			return;
		case 3:
			while (start <= end - 1)
				code.append(ctx.getChild(start++).getText() + " ");
			code.append(ctx.getChild(start).getText());
			return;
		case 4:
			while (start <= end - 2)
				code.append(ctx.getChild(start++).getText() + " ");
			code.append(ctx.getChild(start++).getText());
			code.append(ctx.getChild(start).getText());
			return;
		default:
			return;
		}
	}
}