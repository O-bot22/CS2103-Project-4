public class Variable implements Expression{
	private float _value;

    public Variable (String s) {
        _value = Float.parseFloat(s);
    }
    
    public Expression deepCopy (){
		return new Variable(Float.toString(_value));
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
		s += Float.toString(_value);
		return s;
	};

	/**
	 * Given the value of the independent variable x, compute the value of this expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x){
		return x;
	};

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate (){
		return new Literal("1");
	};


}