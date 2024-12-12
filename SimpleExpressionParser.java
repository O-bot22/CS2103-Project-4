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
		// int separator = -1; // was gonna use these to make it cleaner
		// boolean subtraction = false;
		for (int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '+'){
				System.out.println("found addition sign");
                sum = parseAdditiveExpression(str.substring(0,i));
                product = parseMultiplicativeExpression(str.substring(i+1));
				System.out.println(sum.convertToString(0));
				System.out.println(product.convertToString(0));
				Sum expression = new Sum();
				expression.leftExpression = sum;
				expression.rightExpression = product;
				return expression;
            }else if(str.charAt(i) == '-'){
				System.out.println("found subtraction sign");
                sum = parseAdditiveExpression(str.substring(0,i));
				// if there is a -, but not a +, then include the - sign in the expression, so that it registers as a negative value
                product = parseMultiplicativeExpression(str.substring(i));
				System.out.println(sum.convertToString(0));
				System.out.println(product.convertToString(0));
				Sum expression = new Sum();
				expression.leftExpression = sum;
				expression.rightExpression = product;
				return expression;
			}
        }
		// if there was not a sum, then parse to see if there is a product
		if(sum == null){
			System.out.println("could not parse additive expression, looking for multiplicative: "+str);
			return parseMultiplicativeExpression(str);
		}
		
		return null;
	}
	
	protected Expression parseMultiplicativeExpression (String str) {
		Expression expression;
		Expression sum = null;
		Expression product = null;
		for (int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '*'){
                sum = parseMultiplicativeExpression(str.substring(0,i));
                product = parseExponentialExpression(str.substring(i+1));
            }
        }
		// if there was not a product, then parse to see if there is an exponential
		if(sum == null){
			System.out.println("could not parse mult. expression, looking for exp.: "+str);
			return parseExponentialExpression(str);
		}
		
		return null;
	}
	protected Expression parseExponentialExpression (String str) {
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
	protected Expression parseParentheticalExpression (String str) {
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
			return parseLiteralExpression(str);
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
		// System.out.println(parser.parse("10*2+12-4.").convertToString(0));
		Expression f = parser.parse("5+-7");
		System.out.println(f.convertToString(0));
		System.out.println(f.evaluate(0.0));
	}
}
