package com.rkrzmail.nikita.data;
 
import java.math.*;
import java.util.*;
 

public class MathEval extends Object {

 
private Operator[]                      operators;                                                  // operators in effect for this parser
private final SortedMap<String,Object>  constants;                                                  // external constants
private Map<String,Object>  variables;                                                        // external variables
private final SortedMap<String,FunctionHandler> pureFunctions;                                      // external pureFunctions
private final SortedMap<String,FunctionHandler> impureFunctions;                                    // external pureFunctions
private boolean                         relaxed;                                                    // allow variables to be undefined
private String                          separators;                                                 // cache of the operators, used for separators for getVariablesWithin()

private String                          expression;                                                 // expression being evaluated
private int                             offset;                                                     // used when returning from a higher precedence sub-expression evaluation
private boolean                         isConstant;                                                 // last expression evaluated is constant

// *************************************************************************************************
// INSTANCE CREATE/DELETE
// *************************************************************************************************

/**
 * Create a math evaluator.
 */
public MathEval() {
    super();

    operators=new Operator[256];
    DefaultImpl.registerOperators(this);

    constants=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
    variables= new HashMap<String,Object> ();
    setConstant("E"     ,Math.E);
    setConstant("Euler" ,0.577215664901533D);
    setConstant("LN2"   ,0.693147180559945D);
    setConstant("LN10"  ,2.302585092994046D);
    setConstant("LOG2E" ,1.442695040888963D);
    setConstant("LOG10E",0.434294481903252D);
    setConstant("PHI"   ,1.618033988749895D);
    setConstant("PI"    ,Math.PI);

    pureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    impureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    DefaultImpl.registerFunctions(this);

    relaxed=false;
    separators=null;

    offset=0;
    isConstant=false;
   
    }
 public MathEval(Map var) {
    super();

    operators=new Operator[256];
    DefaultImpl.registerOperators(this);

    constants=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
    variables= var;
    setConstant("E"     ,Math.E);
    setConstant("Euler" ,0.577215664901533D);
    setConstant("LN2"   ,0.693147180559945D);
    setConstant("LN10"  ,2.302585092994046D);
    setConstant("LOG2E" ,1.442695040888963D);
    setConstant("LOG10E",0.434294481903252D);
    setConstant("PHI"   ,1.618033988749895D);
    setConstant("PI"    ,Math.PI);

    pureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    impureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    DefaultImpl.registerFunctions(this);

    relaxed=false;
    separators=null;

    offset=0;
    isConstant=false;
   
    }
public MathEval(MathEval oth) {
    super();

    operators=oth.operators;

    constants=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
    constants.putAll(oth.constants);
    variables=new Hashtable();
    //variables=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
    variables.putAll(oth.variables);

    pureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    impureFunctions=new TreeMap<String,FunctionHandler>(String.CASE_INSENSITIVE_ORDER);
    pureFunctions.putAll(oth.pureFunctions);
    impureFunctions.putAll(oth.impureFunctions);

    relaxed=oth.relaxed;
    separators=oth.separators;

    offset=0;
    isConstant=false;
    }

// *************************************************************************************************
// INSTANCE METHODS - ACCESSORS
// *************************************************************************************************

/** Set a named constant (constant names are not case-sensitive).  Constants are like variables but are not cleared by clear(). Variables of the same name have precedence over constants. */
public Object getConstant(String nam) {
    Object                              val=constants.get(nam);

    return (val==null ? 0 : val);
    }

/** Gets an unmodifiable iterable of the constants in this evaluator. */
public Iterable<Map.Entry<String,Object>> getConstants() {
    return Collections.unmodifiableMap(constants).entrySet();
    }

/** Set a named constant (constants names are not case-sensitive).  Constants are like variables but are not cleared by clear(). Variables of the same name have precedence over constants. */
public MathEval setConstant(String nam, double val) {
    return setConstant(nam, Double.valueOf(val));
    }

/** Set a named constant (constants names are not case-sensitive).  Constants are like variables but are not cleared by clear(). Variables of the same name have precedence over constants. */
public MathEval setConstant(String nam, Object val) {
    if(constants.get(nam)!=null) { throw new IllegalArgumentException("Constants may not be redefined"); }
    validateName(nam);
    constants.put(nam,val);
    return this;
    }

/**
 * Set a custom operator, replacing any existing operator with the same symbol. Operators cannot be removed, only replaced.
 */
public MathEval setOperator(Operator opr) {
    if(opr.symbol>=operators.length) {                                                              // extend the array if necessary
        Operator[] noa=new Operator[opr.symbol+(opr.symbol%255)+1];                                 // use allocation pages of 256
        System.arraycopy(operators,0,noa,0,operators.length);
        operators=noa;
        }
    operators[opr.symbol]=opr;
    return this;
    }

/**
 * Set a pure function handler for the specific named function, replacing any existing handler for the given name; if the handler is null the function handler is removed.
 * <p>
 * Pure functions have results which depend purely on their arguments; given constant arguments they will have a constant result.  Impure functions are rare.
 */
public MathEval setFunctionHandler(String nam, FunctionHandler hdl) {
    return setFunctionHandler(nam,hdl,false);
}
private FunctionHandler unhandlerFunctions ;
public void setUnFunctionHandler(FunctionHandler hdl) {
    this.unhandlerFunctions=hdl;    
}
/**
 * Set a function handler for the specific named function optionally tagging the function as impure, replacing any existing handler for the given name; if the handler is null the function handler is removed.
 * <p>
 * Pure functions have results which depend purely on their arguments; given constant arguments they will have a constant result.  Impure functions are rare.
 */
public MathEval setFunctionHandler(String nam, FunctionHandler hdl, boolean impure) {
    validateName(nam);
    if     (hdl==null) { pureFunctions.remove(nam);     impureFunctions.remove(nam);     }
    else if(impure   ) { pureFunctions.remove(nam);     impureFunctions.put   (nam,hdl); }
    else               { pureFunctions.put   (nam,hdl); impureFunctions.remove(nam);     }
    return this;
    }
public Map getVariables(){
    return variables;
}
/** Set a named variable (variables names are not case-sensitive). */
public Object getVariable(String nam) {
    Object                              val=variables.get(nam);

    return (val==null ? 0 : val);
    }

/** Gets an unmodifiable iterable of the variables in this evaluator. */

private Iterable<Map.Entry<String,Object>> getVariablesEntry() {
    return Collections.unmodifiableMap(variables).entrySet();
}
/** Set a named variable (variables names are not case-sensitive). */
public MathEval setVariable(String nam, double val) {
    return setVariable(nam, Double.valueOf(val));
    }

/** Set a named variable (variables names are not case-sensitive). If the value is null, the variable is removed. */
public MathEval setVariable(String nam, Object val) {
    validateName(nam);
    if(val==null) { variables.remove(nam);  }
    else          { variables.put(nam,val); }
    return this;
    }

/** Clear all variables (constants are not affected). */
public MathEval clear() {
    variables.clear();
    return this;
    }

/** Clear all variables prefixed by the supplied string followed by a dot, such that they match "Prefix.xxx". */
public MathEval clear(String pfx) {
    //variables.subMap((pfx+"."),(pfx+"."+Character.MAX_VALUE)).clear();
    variables.clear();
    return this;
}

/** Get whether a variable which is used in an expression is required to be explicitly set. If not explicitly set, the value 0.0 is assumed. */
public boolean getVariableRequired() {
    return relaxed;
    }

/** Set whether a variable which is used in an expression is required to be explicitly set. If not explicitly set, the value 0.0 is assumed. */
public MathEval setVariableRequired(boolean val) {
    relaxed=(!val);
    return this;
    }

private void validateName(String nam) {
    if(!Character.isLetter(nam.charAt(0))          ) { throw new IllegalArgumentException("Names for constants, variables and functions must start with a letter"     ); }
    if(nam.indexOf('(')!=-1 || nam.indexOf(')')!=-1) { throw new IllegalArgumentException("Names for constants, variables and functions may not contain a parenthesis"); }
    }

// *************************************************************************************************
// INSTANCE METHODS - PUBLIC API
// *************************************************************************************************

/**
 * Evaluate this expression.
 */
public Object evaluate(String exp) throws NumberFormatException, ArithmeticException {
    expression=exp;
    isConstant=true;
    offset=0;
    return _evaluate(0,(exp.length()-1));
    }
 
        

/**
 * Return whether the previous expression evaluated was constant (i.e. contained no variables).
 * This is useful when optimizing to store the result instead of repeatedly evaluating a constant expression like "2+2".
 */
public boolean previousExpressionConstant() {
    return isConstant;
    }

/** Return a set of the variables in the supplied expression. Note: Substitutions which are in the constant table are not included. */
public Set<String> getVariablesWithin(String exp) {
    Set<String>                         all=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    String                              add=null;

    if(separators==null) {
        StringBuilder sep=new StringBuilder(10);
        for(char chr=0; chr<operators.length; chr++) {
            if(operators[chr]!=null && !operators[chr].internal) { sep.append(chr); }
            }
        sep.append("()");
        separators=sep.toString();
        }

    for(StringTokenizer tkz=new StringTokenizer(exp,separators,true); tkz.hasMoreTokens(); ) {
        String                          tkn=tkz.nextToken().trim();

        if     (tkn.length()!=0 && Character.isLetter(tkn.charAt(0))) { add=tkn;      }
        else if(tkn.length()==1 && tkn.charAt(0)=='('               ) { add=null;     }
        else if(add!=null       && !constants.containsKey(add)      ) { all.add(add); }
        }
    if(add!=null && !constants.containsKey(add)) { all.add(add); }
    return all;
    }

// *************************************************************************************************
// INSTANCE METHODS - PRIVATE IMPLEMENTATION
// *************************************************************************************************

/**
 * Evaluate a complete (sub-)expression.
 * @param beg       Inclusive begin offset for subexpression.
 * @param end       Inclusive end offset for subexpression.
 */
private Object _evaluate(int beg, int end) throws NumberFormatException, ArithmeticException {
    return _evaluate(beg,end,0.0,OPERAND,getOperator('='));
    }

/**
 * Evaluate the next operand of an expression.
 * @param beg       Inclusive begin offset for subexpression.
 * @param end       Inclusive end offset for subexpression.
 * @param pnd       Pending operator (operator previous to this subexpression).
 * @param lft       Left-value with which to initialize this subexpression.
 * @param cur       Current operator (the operator for this subexpression).
 */
private Object _evaluate(int beg, int end, Object lft, Operator pnd, Operator cur) throws NumberFormatException, ArithmeticException {
    Operator                            nxt=OPERAND;                                                // next operator
    int                                 ofs;                                                        // current expression offset

    for(ofs=beg; (ofs=skipWhitespace(expression,ofs,end))<=end; ofs++) {
        boolean                         fnc=false;
        Object                          rgt=Double.NaN;                                             // next operand (right-value) to process

        for(beg=ofs; ofs<=end; ofs++) {
            char chr=expression.charAt(ofs);
            if((nxt=getOperator(chr))!=OPERAND) {
                if(nxt.internal) { nxt=OPERAND; }                                                   // must kill operator to prevent spurious "Expression ends with a blank sub-expression" at end of function
                else             { break;       }
                }
            else if(chr==')' || chr==',') {                                                         // end of subexpression or function argument.
                break;
                }
            }

        EvaluateOperand: {
            char                        ch0=expression.charAt(beg);
            boolean                     alp=Character.isLetter(ch0);

            if(cur.unary!=LEFT_SIDE) {
                if(ch0=='+') { continue;             }                                              // unary '+': no-op; i.e. +(-1) == -1
                if(ch0=='-') { nxt=getOperator('±'); }                                              // unary '-': right-binding, high precedence operation (different from subtract)
            }

            if(beg==ofs && (cur.unary==LEFT_SIDE || nxt.unary==RIGHT_SIDE)) {
                rgt=Double.NaN;                                                                     // left-binding unary operator; right value will not be used and should be blank
            } else if(ch0=='(') {
                rgt=_evaluate(beg+1,end);
                ofs=skipWhitespace(expression,offset+1,end);                                        // skip past ')' and any following whitespace
                nxt=(ofs<=end ? getOperator(expression.charAt(ofs)) : OPERAND);                     // modify next operator
            } else if(alp && nxt.symbol=='(') {
                rgt=doFunction(beg,end);
                ofs=skipWhitespace(expression,offset+1,end);                                        // skip past ')' and any following whitespace
                nxt=(ofs<=end ? getOperator(expression.charAt(ofs)) : OPERAND);                     // modify next operator
            }  else if(alp) {
                rgt=doNamedVal(beg,(ofs-1));
            } else {
                try {
                    if(stringOfsEq(expression,beg,"0x")) { rgt=(Object)Long.parseLong(expression.substring(beg+2,ofs).trim(),16); }
                    else                                 { rgt=  (expression.substring(beg  ,ofs).trim()   ); }
                    }
                catch(NumberFormatException thr) {
                    throw exception(beg,"Invalid numeric value \""+expression.substring(beg,ofs).trim()+"\"");
                    }
                }
            }

        if(opPrecedence(cur,LEFT_SIDE)<opPrecedence(nxt,RIGHT_SIDE)) {                              // correct even for last (non-operator) character, since non-operators have the artificial "precedence" zero
            rgt=_evaluate((ofs+1),end,rgt,cur,nxt);                                                 // from after operator to end of current subexpression
            ofs=offset;                                                                             // modify offset to after subexpression
            nxt=(ofs<=end ? getOperator(expression.charAt(ofs)) : OPERAND);                         // modify next operator
         }

        lft=doOperation(beg,lft,cur,rgt);

        cur=nxt;
        if(opPrecedence(pnd,LEFT_SIDE)>=opPrecedence(cur,RIGHT_SIDE)) { break; }
        if(cur.symbol=='(') { ofs--; }                                                              // operator omitted for implicit multiplication of subexpression
    }
    if(ofs>end && cur!=OPERAND) {
        if(cur.unary==LEFT_SIDE) { lft=doOperation(beg,lft,cur,Double.NaN);                                                     }
        else                     { throw exception(ofs,"Expression ends with a blank operand after operator '"+nxt.symbol+"'"); }
        }
    offset=ofs;
    return lft;
    }

private Operator getOperator(char chr) {
    if(chr<operators.length) {
        Operator opr=operators[chr];
        if(opr!=null) { return opr; }
    }
    return OPERAND;
    }

private int opPrecedence(Operator opr, int sid) {
    if     (opr==null)                            { return Integer.MIN_VALUE;                                    } // not an operator
    else if(opr.unary==NO_SIDE || opr.unary!=sid) { return (sid==LEFT_SIDE ? opr.precedenceL : opr.precedenceR); } // operator is binary or is unary and bound to the operand on the other side
    else                                          { return Integer.MAX_VALUE;                                    } // operator is unary and associates with the operand on this side
    }

private Object doOperation(int beg, Object lft, Operator opr, Object rgt) {
  //  if(opr.unary!=RIGHT_SIDE && Double.isNaN(0)) { throw exception(beg,"Mathematical NaN detected in right-operand"); }
   // if(opr.unary!=LEFT_SIDE  && Double.isNaN(0)) { throw exception(beg,"Mathematical NaN detected in left-operand" ); }

    try { return opr.handler.evaluateOperator(lft,opr.symbol,rgt); }
    catch(ArithmeticException thr) {
        throw exception(beg,"Mathematical expression \""+expression+"\" failed to evaluate",thr);
        }
    catch(UnsupportedOperationException thr) {
        int tmp=beg;
        while(tmp>0 && getOperator(expression.charAt(tmp))==null) { tmp--; }                        // set up for offset of the offending operator
        throw exception(tmp,"Operator \""+opr.symbol+"\" not handled by math engine (Programmer error: The list of operators is inconsistent within the engine)");
        }
    }

private Object doFunction(int beg, int end) {
    int                                 argbeg;

    for(argbeg=beg; argbeg<=end && expression.charAt(argbeg)!='('; argbeg++) {;}

    String                              fncnam =expression.substring (beg,argbeg).trim();
    ArgParser                           fncargs=new ArgParser(argbeg,end);
    FunctionHandler                     fnchdl =null;

    try {
        if((fnchdl=pureFunctions.get(fncnam))!=null) {
            return fnchdl.evaluateFunction(fncnam,fncargs);
        } else if((fnchdl=impureFunctions.get(fncnam))!=null) {
            isConstant=false;                                                                       // impure functions cannot be guaranteed to be constant
            return fnchdl.evaluateFunction(fncnam,fncargs);
        }else  if (unhandlerFunctions!=null){
           Object result  = unhandlerFunctions.evaluateFunction(fncnam, fncargs);;
            if (result!=null) {
                return result;
            }
        }
        fncargs=null;                                                                               // suppress check for too many fncargs
    }
    catch(ArithmeticException           thr) { fncargs=null; throw thr;                                                                      }
    catch(NoSuchMethodError             thr) { fncargs=null; throw exception(beg,"Function not supported in this JVM: \""+fncnam+"\"");  }
    catch(UnsupportedOperationException thr) { fncargs=null; throw exception(beg,thr.getMessage());                                      }
    catch(Throwable                     thr) { fncargs=null; throw exception(beg,"Unexpected exception parsing function arguments",thr); }
    finally {
        if(fncargs!=null) {
            if(fncargs.hasNext()) { throw exception(fncargs.getIndex(),"Function has too many arguments"); }
            offset=fncargs.getIndex();
            }
        }
    throw exception(beg,"Function \""+fncnam+"\" not recognized");
    }
public static double asDouble(Object object){
        if (object instanceof Number) {
            return Double.valueOf( String.valueOf(object) ).doubleValue();
        }else if (isNumeric(String.valueOf(object))) {
            return Double.valueOf( String.valueOf(object) ).doubleValue();
        }
        return 0;
    }
    protected static boolean isNumeric(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
    }
private Object doNamedVal(int beg, int end) {
    while(beg<end && Character.isWhitespace(expression.charAt(end))) { end--; }                     // since a letter triggers a named value, this can never reduce to beg==end

    String                  nam=expression.substring(beg,(end+1));
    Object                  val;

    if     ((val=constants.get(nam))!=null) {                   return val; }
    else if((val=variables.get(nam))!=null) { isConstant=false; return val; }
    else if(relaxed                       ) { isConstant=false; return 0.0;               }

    throw exception(beg,"Unrecognized constant or variable \""+nam+"\"");
    }

private ArithmeticException exception(int ofs, String txt) {
    return new ArithmeticException(txt+" at offset "+ofs+" in expression \""+expression+"\"");
    }

private ArithmeticException exception(int ofs, String txt, Throwable thr) {
    return new ArithmeticException(txt+" at offset "+ofs+" in expression \""+expression+"\""+" (Cause: "+(thr.getMessage()!=null ? thr.getMessage() : thr.toString())+")");
    }

private boolean stringOfsEq(String str, int ofs, String val) {
    return str.regionMatches(true,ofs,val,0,val.length());
    }

private int skipWhitespace(String exp, int ofs, int end) {
    while(ofs<=end && Character.isWhitespace(exp.charAt(ofs))) { ofs++; }
    return ofs;
    }

// *************************************************************************************************
// INSTANCE INNER CLASSES - FUNCTION ARGUMENT PARSER
// *************************************************************************************************


    public final class ArgParser {
    final int                           exEnd;

    int                                 index;

    ArgParser(int excstr, int excend) {
        exEnd=excend;

        index=(excstr+1);

        index=skipWhitespace(expression,index,exEnd-1);
    }

    /**
     * Parse the next argument, throwing an exception if there are no more arguments.
     * @throws ArithmeticException If there are no more arguments.
     */
    public double nextAsDouble() {
        if(!hasNext()) { throw exception(index,"Function has too few arguments"); }
          
        return MathEval.asDouble(_next()) ;
    }
    
    public Object next() {
        if(!hasNext()) { throw exception(index,"Function has too few arguments"); }
        return _next();
   }

    /**
     * Parse the next argument, returning the supplied default if there are no more arguments.
     */
    public Object next(Object dft) {
        if(!hasNext()) { return dft; }
        return _next();
        }

    private Object _next() {
        if(expression.charAt(index)==',') { index++; }
            Object ret=_evaluate(index,exEnd);
            index=offset;
        return ret;
    }

    /** Test whether there is another argument to parse. */
    public boolean hasNext() {
        
        return (expression.charAt(index)!=')');
    }

    int getIndex() {
        return index;
        }
    }

// *************************************************************************************************
// STATIC NESTED CLASSES - OPERATOR
// *************************************************************************************************
 
    static public final class Operator  extends Object {
    final char                          symbol;                                                     // parser symbol for this operator
    final int                           precedenceL;                                                // precedence when on the left
    final int                           precedenceR;                                                // precedence when on the right
    final int                           unary;                                                      // unary operator binding: left, right, or neither
    final boolean                       internal;                                                   // internal pseudo operator
    final OperatorHandler               handler;

    /**
     * Create a binary operator with the same precedence on the left and right.
     */
    public Operator(char sym, int prc, OperatorHandler hnd) {
        this(sym,prc,prc,NO_SIDE,false,hnd);
    }

    /**
     * Create an operator which may have different left and right precedence and/or may be unary.
     * <p>
     * Using different precedence for one side allows affinity binding such that consecutive operators are evaluated left to right.
     * <p>
     * Marking an operator as unary binds the precedence for the specified side such that it always has maximum precedence when considered from the opposite side.
     */
    public Operator(char sym, int prclft, int prcrgt, int unibnd, OperatorHandler hnd) {
        this(sym,prclft,prcrgt,unibnd,false,hnd);

        if(prclft<0 || prclft>99) { throw new IllegalArgumentException("Operator precendence must be 0 - 99"); }
        if(prcrgt<0 || prcrgt>99) { throw new IllegalArgumentException("Operator precendence must be 0 - 99"); }
        if(handler==null) { throw new IllegalArgumentException("Operator handler is required"); }
        }

    Operator(char sym, int prclft, int prcrgt, int unibnd, boolean intern, OperatorHandler hnd) {
        symbol=sym;
        precedenceL=prclft;
        precedenceR=prcrgt;
        unary=unibnd;
        internal=intern;
        handler=hnd;
        }

    public String toString() {
        return ("MathOperator['"+symbol+"']");
        }
    }

// *************************************************************************************************
// STATIC NESTED CLASSES - OPERATION EVALUATOR INTERFACE
// *************************************************************************************************

    static public interface OperatorHandler {
    public Object evaluateOperator(Object lft, char opr, Object rgt) throws ArithmeticException;
    }

// *************************************************************************************************
// STATIC NESTED CLASSES - FUNCTION EVALUATOR INTERFACE
// *************************************************************************************************

    static public interface FunctionHandler {
        public Object evaluateFunction(String fncnam, ArgParser fncargs) throws ArithmeticException;
    }

// *************************************************************************************************
// STATIC NESTED CLASSES - DEFAULT OPERATOR/FUNCTION IMPLEMENTATION
// *************************************************************************************************

    /**
     * An implementation of the default supported operations and functions.
     */
    static class DefaultImpl  extends Object implements OperatorHandler, FunctionHandler {
    private DefaultImpl() {
       
    }
    private boolean equal(Object object , Object object1){
        if (object instanceof Number || object1 instanceof Number) {
            if (isDecimalNumber(String.valueOf(object)) && isDecimalNumber(String.valueOf(object1))) {
                return Double.valueOf(String.valueOf(object)).doubleValue()== Double.valueOf(String.valueOf(object1)).doubleValue();
            }
        }else{
            return String.valueOf(object).equals(String.valueOf(object1));
        }
        return false;
    }
    private boolean or(Object object , Object object1){
        if (object instanceof Boolean && object1 instanceof Boolean) {
            return Boolean.valueOf(String.valueOf(object)).booleanValue() || Boolean.valueOf(String.valueOf(object1)).booleanValue() ;
        }else{
            return String.valueOf(object).equals(String.valueOf(object1));
        }
    }
    private boolean and(Object object , Object object1){
        if (object instanceof Boolean && object1 instanceof Boolean) {
            return Boolean.valueOf(String.valueOf(object)).booleanValue() && Boolean.valueOf(String.valueOf(object1)).booleanValue() ;
        }else{
            return String.valueOf(object).equals(String.valueOf(object1));
        }
    }
    private static boolean isDecimalNumber(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
     }
    // To add/remove operators change evaluateOperator() and registration
    public Object evaluateOperator(Object lft1, char opr, Object rgt1) {
        double rgt = asDouble(rgt1);
        double lft = asDouble(lft1);
        switch(opr) {
            case '=' : return rgt1;                                                                  // simple assignment, used as the final operation, must be maximum precedence
            case '^' : return Math.pow(lft,rgt);                                                    // power
            case '±' : return -rgt;                                                                 // unary negation
            case '*' : return lft*rgt;                                                              // multiply (classical)
            case '×' : return lft*rgt;                                                              // multiply (because it's a Unicode world out there)
            case '·' : return lft*rgt;                                                              // multiply (because it's a Unicode world out there)
            case '(' : return lft*rgt;                                                              // multiply (implicit due to brackets, e.g "(a)(b)")
            case '/' : return lft/rgt;                                                              // divide (classical computing)
            case '÷' : return lft/rgt;                                                              // divide (because it's a Unicode world out there)
            case '%' : return lft%rgt;                                                              // remainder
            case '+' : return lft+rgt;                                                              // add/unary-positive
            case '-' : return lft-rgt;                                                              // subtract/unary-negative
            case ';' : return rgt1;    //nope
                
            case '!' : return !equal(rgt1, rgt1);                                                          
            case ':' : return equal(lft1, rgt1);                                                             
            case '|' : return or(lft1, rgt1);                                                            
            case '&' : return and(lft1, rgt1);                                                             
            case '>' : return asDouble(lft1) > asDouble(rgt1);                                                              // >
            case '<' : return asDouble(lft1) < asDouble(rgt1);     
            case '«' : return asDouble(lft1) <= asDouble(rgt1);  
            case '»' : return asDouble(lft1) >= asDouble(rgt1); 
            case 'þ' : return likeSQL(lft1, rgt1);                
            case '‡' : return String.valueOf(lft1)+String.valueOf(rgt1);    
                
            default  : throw new UnsupportedOperationException("MathEval internal operator setup is incorrect - internal operator \""+opr+"\" not handled");
            }
        }
    private Object likeSQL(Object left, Object right){
        String l = String.valueOf(left);
        String r = String.valueOf(right);
        if (r.startsWith("%")&&r.endsWith("%")) {
            return l.contains(r.substring(1,r.length()-1));
        }else  if (r.startsWith("%")) {
            return l.endsWith(r.substring(1));
        }else  if (r.endsWith("%")) {
            return l.startsWith(r.substring(0, r.length()-1));
        }else{
            return l.equals(r);
        }
    }
    // To add/remove functions change evaluateOperator() and registration
    public Object evaluateFunction(String fncnam, ArgParser fncargs) throws ArithmeticException {        
        switch(Character.toLowerCase(fncnam.charAt(0))) {
            case 'a' : {
                if(fncnam.equalsIgnoreCase("abs"           )) { return Math.abs        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("acos"          )) { return Math.acos       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("asin"          )) { return Math.asin       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("atan"          )) { return Math.atan       (fncargs.nextAsDouble());                }
                } break;
            case 'c': {
                if(fncnam.equalsIgnoreCase("cbrt"          )) { return Math.cbrt       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("ceil"          )) { return Math.ceil       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("cos"           )) { return Math.cos        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("cosh"          )) { return Math.cosh       (fncargs.nextAsDouble());                }
                } break;
            case 'e': {
                if(fncnam.equalsIgnoreCase("exp"           )) { return Math.exp        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("expm1"         )) { return Math.expm1      (fncargs.nextAsDouble());                }
                } break;
            case 'f': {
                if(fncnam.equalsIgnoreCase("floor"         )) { return Math.floor      (fncargs.nextAsDouble());                }
                } break;
            case 'p': {
                if(fncnam.equalsIgnoreCase("pow"           )) { return Math.pow(fncargs.nextAsDouble(),fncargs.nextAsDouble()); }  
                } break;
            case 'l': {
                if(fncnam.equalsIgnoreCase("log"           )) { return Math.log        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("log10"         )) { return Math.log10      (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("log1p"         )) { return Math.log1p      (fncargs.nextAsDouble());                }
                } break;
            case 'm': {
                if(fncnam.equalsIgnoreCase("max"           )) { return Math.max        (fncargs.nextAsDouble(),fncargs.nextAsDouble()); }
                if(fncnam.equalsIgnoreCase("min"           )) { return Math.min        (fncargs.nextAsDouble(),fncargs.nextAsDouble()); }
                } break;
            case 'n': {
               if(fncnam.equalsIgnoreCase("nikita.nikita"        )) { return fncargs.nextAsDouble();                }  
               if(fncnam.equalsIgnoreCase("not"        )) { Object o = fncargs.next(); return  false;                }  
                } break;
            case 'r': {
                if(fncnam.equalsIgnoreCase("random"        )) { return Math.random     ();                              } // impure
                if(fncnam.equalsIgnoreCase("round"         )) { return Math.round      (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("roundHE"       )) { return Math.rint       (fncargs.nextAsDouble());                } // round half-even
                } break;
            case 's': {
                if(fncnam.equalsIgnoreCase("signum"        )) { return Math.signum     (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("sin"           )) { return Math.sin        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("sinh"          )) { return Math.sinh       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("sqrt"          )) { return Math.sqrt       (fncargs.nextAsDouble());                }
                } break;
            case 't': {
                if(fncnam.equalsIgnoreCase("tan"           )) { return Math.tan        (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("tanh"          )) { return Math.tanh       (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("toDegrees"     )) { return Math.toDegrees  (fncargs.nextAsDouble());                }
                if(fncnam.equalsIgnoreCase("toRadians"     )) { return Math.toRadians  (fncargs.nextAsDouble());                }
                } break;
            case 'u': {
                if(fncnam.equalsIgnoreCase("ulp"           )) { return Math.ulp        (fncargs.nextAsDouble());                }
                } break;
                // no default
            }
             throw new UnsupportedOperationException("MathEval internal function setup is incorrect - internal function \""+fncnam+"\" not handled");
        }

    static final DefaultImpl            INSTANCE=new DefaultImpl();

    static private final Operator       OPR_EQU =new Operator('=',99,99,RIGHT_SIDE,true ,DefaultImpl.INSTANCE); // simple assignment, used as the final operation, must be maximum precedence
    static private final Operator       OPR_PWR =new Operator('^',80,81,NO_SIDE   ,false,DefaultImpl.INSTANCE); // power
    static private final Operator       OPR_NEG =new Operator('±',60,60,RIGHT_SIDE,true ,DefaultImpl.INSTANCE); // unary negation
    static private final Operator       OPR_MLT =new Operator('*',40                    ,DefaultImpl.INSTANCE); // multiply (classical)
    static private final Operator       OPR_BKT =new Operator('(',40                    ,DefaultImpl.INSTANCE); // multiply (implicit due to brackets, e.g "(a)(b)")
    static private final Operator       OPR_DIV =new Operator('/',40                    ,DefaultImpl.INSTANCE); // divide (classical computing)
    static private final Operator       OPR_MOD =new Operator('%',40                    ,DefaultImpl.INSTANCE); // remainder
    static private final Operator       OPR_ADD =new Operator('+',20                    ,DefaultImpl.INSTANCE); // add/unary-positive
    static private final Operator       OPR_SUB =new Operator('-',20                    ,DefaultImpl.INSTANCE); // subtract/unary-negative    
    static private final Operator       OPR_NOP =new Operator(';',97,97,LEFT_SIDE,true ,DefaultImpl.INSTANCE);  // simple assignment, used as the final operation, must be maximum precedence
    static private final Operator       OPR_NOT =new Operator('!',98,98,RIGHT_SIDE,true ,DefaultImpl.INSTANCE); // simple assignment, used as the final operation, must be maximum precedence
    
   
    

    // To add/remove operators change evaluateOperator() and registration
    static void registerOperators(MathEval tgt) {
        tgt.setOperator(OPR_EQU );
        tgt.setOperator(OPR_PWR );
        tgt.setOperator(OPR_NEG );
        tgt.setOperator(OPR_MLT);
        tgt.setOperator(OPR_BKT );
        tgt.setOperator(OPR_DIV);
        tgt.setOperator(OPR_MOD );
        tgt.setOperator(OPR_ADD );
        tgt.setOperator(OPR_SUB );
        
        tgt.setOperator( new Operator('&',5                     ,DefaultImpl.INSTANCE) );
        tgt.setOperator( new Operator('|',5                     ,DefaultImpl.INSTANCE) );
        tgt.setOperator( new Operator('þ',5                     ,DefaultImpl.INSTANCE) );
        tgt.setOperator( new Operator('>',10                    ,DefaultImpl.INSTANCE) );
        tgt.setOperator( new Operator('<',10                    ,DefaultImpl.INSTANCE) );
        tgt.setOperator( new Operator('«',10                    ,DefaultImpl.INSTANCE) );//<=
        tgt.setOperator( new Operator('»',10                    ,DefaultImpl.INSTANCE) );//>=
        tgt.setOperator( new Operator(':',10                    ,DefaultImpl.INSTANCE) );//==
        tgt.setOperator( new Operator('~',10                    ,DefaultImpl.INSTANCE) );//!=
        tgt.setOperator( new Operator('‡',20                    ,DefaultImpl.INSTANCE) );//concat
       
        tgt.setOperator( OPR_NOT );
        tgt.setOperator( OPR_NOP );
                
        }

    // To add/remove functions change evaluateOperator() and registration
    static void registerFunctions(MathEval tgt) {
        tgt.setFunctionHandler("abs"           ,INSTANCE);
        tgt.setFunctionHandler("acos"          ,INSTANCE);
        tgt.setFunctionHandler("asin"          ,INSTANCE);
        tgt.setFunctionHandler("atan"          ,INSTANCE);
        tgt.setFunctionHandler("cbrt"          ,INSTANCE);
        tgt.setFunctionHandler("ceil"          ,INSTANCE);
        tgt.setFunctionHandler("cos"           ,INSTANCE);
        tgt.setFunctionHandler("cosh"          ,INSTANCE);
        tgt.setFunctionHandler("exp"           ,INSTANCE);
        tgt.setFunctionHandler("expm1"         ,INSTANCE);
        tgt.setFunctionHandler("floor"         ,INSTANCE);
        tgt.setFunctionHandler("log"           ,INSTANCE);
        tgt.setFunctionHandler("log10"         ,INSTANCE);
        tgt.setFunctionHandler("log1p"         ,INSTANCE);
        tgt.setFunctionHandler("max"           ,INSTANCE);
        tgt.setFunctionHandler("min"           ,INSTANCE);
        tgt.setFunctionHandler("pow"           ,INSTANCE);
        tgt.setFunctionHandler("random"        ,INSTANCE,true); // impure
        tgt.setFunctionHandler("round"         ,INSTANCE);
        tgt.setFunctionHandler("roundHE"       ,INSTANCE); // round half-even
        tgt.setFunctionHandler("signum"        ,INSTANCE);
        tgt.setFunctionHandler("sin"           ,INSTANCE);
        tgt.setFunctionHandler("sinh"          ,INSTANCE);
        tgt.setFunctionHandler("sqrt"          ,INSTANCE);
        tgt.setFunctionHandler("tan"           ,INSTANCE);
        tgt.setFunctionHandler("tanh"          ,INSTANCE);
        tgt.setFunctionHandler("toDegrees"     ,INSTANCE);
        tgt.setFunctionHandler("toRadians"     ,INSTANCE);
        tgt.setFunctionHandler("ulp"           ,INSTANCE);
        
        tgt.setFunctionHandler("not"           ,INSTANCE);
        tgt.setFunctionHandler("nikita.nikita"           ,INSTANCE);
        }
    }

    // *************************************************************************************************
    // STATIC PROPERTIES
    // *************************************************************************************************

    /** Operator/operand on on the left. */
    static public final int                 LEFT_SIDE ='L';
    /** Operator/operand on on the right. */
    static public final int                 RIGHT_SIDE='R';
    /** Operator/operand side is immaterial. */
    static public final int                 NO_SIDE   ='B';
    static public final OperatorHandler     DFT_OPERATOR_HANDLER=DefaultImpl.INSTANCE;
    static public final FunctionHandler     DFT_FUNCTION_HANDLER=DefaultImpl.INSTANCE;
    static private final Operator           OPERAND=new Operator('\0',0,0,NO_SIDE,false,null);          // special "non-operator" representing an operand character
} // END CLASS
