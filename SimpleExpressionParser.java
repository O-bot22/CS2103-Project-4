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
		Expression expression = parseAdditiveExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}
	
	protected Expression parseAdditiveExpression (String str) throws ExpressionParseException{
		// Expression expression;
		Expression sum = null;
		Expression product = null;
		for (int i=str.length()-1; i>=0; i--) {
			if(str.charAt(i) == '-'){
				// attempt to parse it as subtraction, but if it fails then try addition or pass on to the next char
				try{
					System.out.println("found subtraction sign at "+i);
					sum = parseAdditiveExpression(str.substring(0,i));
					product = parseMultiplicativeExpression(str.substring(i)); // treat the subraction as a negative
					System.out.println(sum.convertToString(0));
					System.out.println(product.convertToString(0));
					Sum expression = new Sum();
					expression.leftExpression = sum;
					expression.rightExpression = product;
					return expression;
				}catch (ExpressionParseException e){
					// if it wasn't subtraction, then move on
					System.out.println("could not parse as subtraction");
				}
			}else if(str.charAt(i) == '+'){
				System.out.println("found addition sign in "+str);
				sum = parseAdditiveExpression(str.substring(0,i));
				product = parseMultiplicativeExpression(str.substring(i+1)); // split as addition
				System.out.println(sum.convertToString(0));
				System.out.println(product.convertToString(0));
				Sum expression = new Sum();
				expression.leftExpression = sum;
				expression.rightExpression = product;
				return expression;
            }
        }

		// if there was not a sum, then parse to see if there is a product
		System.out.println("could not parse additive expression, looking for multiplicative in: "+str);
		return parseMultiplicativeExpression(str);
	}
	
	protected Expression parseMultiplicativeExpression (String str) throws ExpressionParseException{
		// Expression expression;
		Expression product = null;
		Expression exponent = null;
		for (int i=str.length()-1; i>=0; i--) {
			if(str.charAt(i) == '/'){
				System.out.println("found division sign at "+i);
				product = parseMultiplicativeExpression(str.substring(0,i));
				exponent = parseExponentialExpression(str.substring(i+1));
				Product expression = new Product();
				expression.leftExpression = product;
				expression.rightExpression = exponent;
				expression.division = '/';
				return expression;
				
			}else if(str.charAt(i) == '*'){
				System.out.println("found multiplication sign at "+i);
				product = parseMultiplicativeExpression(str.substring(0,i));
				exponent = parseExponentialExpression(str.substring(i+1));
				Product expression = new Product();
				expression.leftExpression = product;
				expression.rightExpression = exponent;
				expression.division = '*';
				return expression;
            }
        }

		// if there was not a sum, then parse to see if there is a product
		System.out.println("could not parse multi. expression, looking for exp. in: "+str);
		return parseExponentialExpression(str);
	}
	protected Expression parseExponentialExpression (String str) throws ExpressionParseException{
		Expression expression;
		Expression sum = null;
		Expression product = null;
		for (int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '^'){
                sum = parseMultiplicativeExpression(str.substring(0,i));
                product = parseParentheticalExpression(str.substring(i+1));
            }
        }
		// if there was not an exponential, then parse to see if there is are parenthesis
		if(sum == null){
			System.out.println("could not parse exp. expression, looking for paren.: "+str);
			return parseParentheticalExpression(str);
		}
		
		return null;
	}
	protected Expression parseParentheticalExpression (String str)  throws ExpressionParseException{
		Expression expression;
		Expression sum = null;
		Expression product = null;
		for (int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '('){
                sum = parseMultiplicativeExpression(str.substring(0,i));
                product = parseParentheticalExpression(str.substring(i+1));
            }
        }
		// if there was not a sum, then parse to see if there is a product
		if(sum == null){
			System.out.println("could not parse parenthetical expression, returning a literal: "+str);
			if(str == ""){
				throw new ExpressionParseException("tried to parse empty string as a literal");
			}else{
				return parseLiteralExpression(str);
			}
		}
		
		return null;
	}

        // TODO: once you implement a VariableExpression class, fix the return-type below.
        protected /*Variable*/Expression parseVariableExpression (String str) {
                if (str.equals("x")) {
                        // TODO implement the VariableExpression class and uncomment line below
                        // return new VariableExpression();
                }
                return null;
        }

        // TODO: once you implement a LiteralExpression class, fix the return-type below.
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
			// return null;
			// TODO: Once you implement LiteralExpression, replace the line above with the line below:
			return new Literal(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		Expression e = parser.parse("10*-2");
		System.out.println(e.convertToString(0));
		System.out.println(e.evaluate(0));
		// Expression f = parser.parse("-5+7-2");
		// System.out.println(f.convertToString(0));
		// System.out.println(f.evaluate(0.0));
	}
}
