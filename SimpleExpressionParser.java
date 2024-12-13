import java.util.function.*;

public class SimpleExpressionParser implements ExpressionParser {
        /*
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P
	 * A -> A+M | A-M | M
	 * M -> M*E | M/E | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
	 * 10*x - 2*(15+x^3)
	 * 
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression;
		expression = parseAdditiveExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}
	
	protected Expression parseAdditiveExpression (String str) throws ExpressionParseException{
		Expression sum = null;
		Expression product = null;
		for (int i=str.length()-1; i>=0; i--) {
			if(str.charAt(i) == '+'){
				try{
					sum = parseAdditiveExpression(str.substring(0,i));
					product = parseMultiplicativeExpression(str.substring(i+1)); // split as addition
					Sum expression = new Sum(false);
					expression.leftExpression = sum;
					expression.rightExpression = product;
					return expression;
				}catch(ExpressionParseException e){
					// if we can't split it as addition, then it might be a full S -> M -> E -> P -> S | L | V
				}
			}else if(str.charAt(i) == '-'){
				// attempt to parse it as subtraction, but if it fails then try addition or pass on to the next char
				try{
					sum = parseAdditiveExpression(str.substring(0,i));
					product = parseMultiplicativeExpression(str.substring(i+1));
					Sum expression = new Sum(true);
					expression.leftExpression = sum;
					expression.rightExpression = product;
					return expression;
				}catch (ExpressionParseException e){
					// if it wasn't subtraction, then move on
				}
            }
        }

		// if there was not a sum, then parse to see if there is a product
		return parseMultiplicativeExpression(str);
	}
	
	protected Expression parseMultiplicativeExpression (String str) throws ExpressionParseException{
		Expression product = null;
		Expression exponent = null;
		for (int i=str.length()-1; i>=0; i--) {
			if(str.charAt(i) == '/'){
				try{
					product = parseMultiplicativeExpression(str.substring(0,i));
					exponent = parseExponentialExpression(str.substring(i+1));
					Product expression = new Product(true);
					expression.leftExpression = product;
					expression.rightExpression = exponent;
					return expression;
				}catch(ExpressionParseException e){
					// if we can't split it as multiplication, then it might be a full E -> P -> S | L | V
				}
			}else if(str.charAt(i) == '*'){
				try{
					product = parseMultiplicativeExpression(str.substring(0,i));
					exponent = parseExponentialExpression(str.substring(i+1));
					Product expression = new Product(false);
					expression.leftExpression = product;
					expression.rightExpression = exponent;
					return expression;
				}catch(ExpressionParseException e){
					// if we can't split it as multiplication, then it might be a full E -> P -> S | L | V
				}
            }
        }

		// if there was not a product, then parse to see if there is a exponent
		return parseExponentialExpression(str);
	}
	protected Expression parseExponentialExpression (String str) throws ExpressionParseException{
		Expression exponent = null;
		Expression parenthesis = null;
		for (int i=str.length()-1; i>=0; i--) {
			if(str.charAt(i) == '^'){
				try{
					exponent = parseExponentialExpression(str.substring(0,i));
					parenthesis = parseParentheticalExpression(str.substring(i+1));
					Exponent expression = new Exponent();
					expression.leftExpression = exponent;
					expression.rightExpression = parenthesis;
					return expression;
				}catch(ExpressionParseException e){
					// if it wasn't an exponent, look to see if it could be just a parenthesis
				}
			}else if(str.charAt(i) == 'g'){ // check for log somehow
				System.out.println(str.substring(i-2,i+1));
				if(str.substring(i-2,i+1).equals("log")){
					parenthesis = parseParentheticalExpression(str.substring(i+1,str.length()));
					Exponent expression = new Exponent();
					expression.leftExpression = parenthesis;
					return expression;
				}
            }
        }

		return parseParentheticalExpression(str);
	}
	protected Expression parseParentheticalExpression (String str)  throws ExpressionParseException{
		Parenthesis inner;
		// if it has a parenthesis
		if(str.contains("(") || str.contains(")")){
			// if it is actually encapsulated then parse it
			if(str.charAt(0) == '(' && str.charAt(str.length()-1) == ')'){
					inner = new Parenthesis();
					inner.inner = parseAdditiveExpression(str.substring(1, str.length()-1));
					return inner;
			}else{
			// if the parenthesis can't be closed, then it should be split somewhere else
				throw new ExpressionParseException("tried to parse an invalid parenthesis expression");
			}
		}
		if(str == ""){
			throw new ExpressionParseException("tried to parse empty string as a literal");
		}else{
			Expression e = parseVariableExpression(str);
			if(e != null){
				return e;
			}else{
				e = parseLiteralExpression(str);
				if(e != null){
					return e;
				}else{
					throw new ExpressionParseException("tried to parse invalid literal: "+str);
				}
			}
		}
	}

	protected Expression parseVariableExpression (String str) {
		if (str.equals("x")) {
			return new Variable();
		}
		return null;
	}

	protected Literal parseLiteralExpression (String str) {
		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			return new Literal(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		Expression e;
		// e = parser.parse("10/-2*5+20"); // == -5
		// e = parser.parse("5*x+(2/x)");
		// e = parser.parse("2--1");
		// e = parser.parse("2^(x+2)");
		// e = parser.parse("(3)");
		// e = parser.parse("10*x - 2*(15+x^3)");
		// e = parser.parse("10-2*15+3"); // -17
		// e = parser.parse("x^2+52*x");
		e = parser.parse("((2+(((x)))+3))");
		// e.convertToString(0);
		// System.out.println(e.convertToString(0));
		// System.out.println(e.evaluate(5));
		// System.out.println(e.evaluate(2));
		e.convertToString(0);
		// e.differentiate().convertToString(0);
		// System.out.println(e.differentiate().evaluate(4));
	}
}
