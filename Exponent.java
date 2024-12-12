public class Exponent implements Expression{

    Expression leftExpression;
    Expression rightExpression;
	char exp = '^';

    public Exponent() {
	}
    
    public Expression deepCopy(){
		Exponent e = new Exponent();
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
        return leftExpression.convertToString(indentLevel) + exp + rightExpression.convertToString(indentLevel);
    };

	/**
	 * Given the value of the independent variable x, compute the value of this expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x){
		if(exp == '^'){
			return Math.pow(leftExpression.evaluate(x), rightExpression.evaluate(x));
		}else{
			// treat it as a log maybe?
			return leftExpression.evaluate(x) / rightExpression.evaluate(x);

		}
    };

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate (){
        return new Literal("s");
    };


}