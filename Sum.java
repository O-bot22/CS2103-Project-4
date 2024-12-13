public class Sum implements Expression{

    Expression leftExpression;
    Expression rightExpression;
	boolean subtraction;

	/**
	 * @param div flag to set the product to subtraction or addition
	 */
    public Sum(boolean sub) {
		subtraction = sub;
	}

    /**
	 * Creates a seperate instance of the expression
	 * with the same properties and child expressions
	 * @return a deep copy of this expression
	 */
    public Expression deepCopy(){
		Sum e = new Sum(subtraction);
		e.leftExpression = leftExpression.deepCopy();
		e.rightExpression = leftExpression.deepCopy();
        return e;
    };

	/**
	 * Creates a String representation of this expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel){
		String s = "";
		for(int i=0; i<indentLevel; i++){
			s += '\t';
		}
		if(subtraction){
			s += '-';
		}else{
			s += '+';
		}
		System.out.println(s);
		leftExpression.convertToString(indentLevel+1);
		rightExpression.convertToString(indentLevel+1);
        return s;
    };

	/**
	 * Given the value of the independent variable x, compute the value of this expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x){
		if(subtraction){
	        return leftExpression.evaluate(x) - rightExpression.evaluate(x);
		}else{
	        return leftExpression.evaluate(x) + rightExpression.evaluate(x);
		}
    };

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate (){
		Sum s = new Sum(subtraction);
		s.leftExpression = leftExpression.differentiate();
		s.rightExpression = rightExpression.differentiate();
		return s;
    };


}