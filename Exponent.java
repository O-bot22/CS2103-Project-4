public class Exponent implements Expression{

    Expression leftExpression;
    Expression rightExpression;

    public Exponent() {}

    /**
	 * Creates a seperate instance of the expression
	 * with the same properties and child expressions
	 * @return a deep copy of this expression
	 */
    public Expression deepCopy(){
		Exponent e = new Exponent();
		e.leftExpression = leftExpression.deepCopy();
		if(e.rightExpression!=null){
			e.rightExpression = leftExpression.deepCopy();
		}
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
		if(rightExpression == null){
			s += "log";
			System.out.println(s);
			leftExpression.convertToString(indentLevel+1);
		}else{
			s += '^';
			System.out.println(s);
			leftExpression.convertToString(indentLevel+1);
			rightExpression.convertToString(indentLevel+1);
		}
        return s;
    };

	/**
	 * Given the value of the independent variable x, compute the value of this expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x){
		if(rightExpression == null){ // then it must be a logarithm
			// treat it as a log maybe?
			return Math.log(leftExpression.evaluate(x));
		}else{
			return Math.pow(leftExpression.evaluate(x), rightExpression.evaluate(x));
		}
    };

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate (){
		if(rightExpression == null){
			// differentiation rule for log
			Product p = new Product(true);
			p.leftExpression = leftExpression.differentiate();
			p.rightExpression = leftExpression.deepCopy();
			return p;
		}else{
			// check if the base is a constant
			if(leftExpression instanceof Literal){
				Product p1 = new Product(false); // ((log C) C^h(x)) * h'(x)
				
				Product p2 = new Product(false); // (log C) * C^h(x)
				
				Exponent log = new Exponent(); // log C
				log.leftExpression = leftExpression.deepCopy();
				
				Exponent exp = new Exponent(); // C^h(x)
				exp.leftExpression = leftExpression.deepCopy();
				exp.rightExpression = rightExpression.deepCopy();

				p2.leftExpression = log;
				p2.rightExpression = exp;

				p1.leftExpression = p2;
				p1.rightExpression = rightExpression.differentiate();
				return p1;
			}else if(rightExpression instanceof Literal){
				Product p1 = new Product(false); // f'(x)= C * (g(x)^C-1 g'(x))

				Product p2 = new Product(false); // g(x)^C-1 * g'(x)

				Exponent exp = new Exponent(); // g(x)^C-1
				
				Sum s = new Sum(true); // C-1
				s.leftExpression = rightExpression.deepCopy(); // C
				s.rightExpression = new Literal("1");
				
				exp.leftExpression = leftExpression.deepCopy(); //g(x)
				exp.rightExpression = s;
				
				p2.leftExpression = exp;
				p2.rightExpression = leftExpression.differentiate(); //g'(x)

				p1.leftExpression = rightExpression.deepCopy(); //C
				p1.rightExpression = p2;

				return p1;
			}else{
				throw new UnsupportedOperationException();
			}
		}
    };


}