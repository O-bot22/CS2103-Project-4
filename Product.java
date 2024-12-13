public class Product implements Expression{

    Expression leftExpression;
    Expression rightExpression;
	boolean division = false;

	/**
	 * @param div flag to set the product to muliplication or division
	 */
    public Product(boolean div) {
		division = div;
	}

    /**
	 * Creates a seperate instance of the expression
	 * with the same properties and child expressions
	 * @return a deep copy of this expression
	 */
    public Expression deepCopy(){
		Product e = new Product(division);
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
		if(division){
			s += '/';
		}else{
			s += '*';
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
		if(division){
			return leftExpression.evaluate(x) / rightExpression.evaluate(x);
		}else{
			return leftExpression.evaluate(x) * rightExpression.evaluate(x);
		}
    };

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate(){
		if(division){
			System.out.println("differentiating division");
			//  f'(x)=g'(x)/h(x) - g(x)h'(x)/h(x)2
			Sum d = new Sum(true); // subtraction

			Product gPrime_h = new Product(true);  // first expression is division
			gPrime_h.leftExpression = leftExpression.differentiate();
			gPrime_h.rightExpression = rightExpression.deepCopy();

			Product expression2 = new Product(true); // (g(x)*h'(x)) / (h(x)^2)

			Product g_hPrime = new Product(false);   // g(x)*h'(x)
			g_hPrime.leftExpression = leftExpression.deepCopy();
			g_hPrime.rightExpression = rightExpression.differentiate();

			Exponent hSquared = new Exponent();			 // h(x)^2
			hSquared.leftExpression = rightExpression.deepCopy();
			hSquared.rightExpression = new Literal("2");

			expression2.leftExpression = g_hPrime;
			expression2.rightExpression = hSquared;

			d.leftExpression = gPrime_h;
			d.rightExpression = expression2;

			return d;
		}else{
			Sum d = new Sum(false);

			Product g_hPrime = new Product(false);
			g_hPrime.leftExpression = leftExpression.deepCopy(); // g
			g_hPrime.rightExpression = rightExpression.differentiate(); // h'

			Product h_gPrime = new Product(false);
			h_gPrime.leftExpression = leftExpression.differentiate(); // g
			h_gPrime.rightExpression = rightExpression.deepCopy(); // h'

			d.leftExpression = g_hPrime;
			d.rightExpression = h_gPrime;
			return d;
		}
    };


}