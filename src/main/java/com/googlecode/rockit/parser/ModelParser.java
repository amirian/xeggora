// $ANTLR 3.4 com/googlecode/rockit/parser/Model.g 2015-11-10 15:30:56

package com.googlecode.rockit.parser;
import java.util.TreeSet;
import java.util.HashSet;
import com.googlecode.rockit.javaAPI.*;
import com.googlecode.rockit.javaAPI.predicates.*;
import com.googlecode.rockit.javaAPI.types.*;
import com.googlecode.rockit.javaAPI.formulas.*;
import com.googlecode.rockit.javaAPI.formulas.expressions.*;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.*;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.*;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.Messages;
import com.googlecode.rockit.app.Parameters;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class ModelParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMA", "DOUBLEVAR", "FLOAT", "HASH", "ID", "ML_COMMENT", "NEWLINE", "NOT", "SL_COMMENT", "STAR", "STRING", "WS", "'('", "')'", "'.'", "':'", "'<='", "'>='", "'v'", "'|'"
    };

    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int COMMA=4;
    public static final int DOUBLEVAR=5;
    public static final int FLOAT=6;
    public static final int HASH=7;
    public static final int ID=8;
    public static final int ML_COMMENT=9;
    public static final int NEWLINE=10;
    public static final int NOT=11;
    public static final int SL_COMMENT=12;
    public static final int STAR=13;
    public static final int STRING=14;
    public static final int WS=15;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public ModelParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public ModelParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return ModelParser.tokenNames; }
    public String getGrammarFileName() { return "com/googlecode/rockit/parser/Model.g"; }


    /** Map variable name to Integer object holding value */
    TreeSet<Type> types = new TreeSet<Type>();
    TreeSet<PredicateAbstract> predicates = new TreeSet<PredicateAbstract>();
    int formulaId = 1;
    HerbrandUniverse u = HerbrandUniverse.getInstance();



    // $ANTLR start "model"
    // com/googlecode/rockit/parser/Model.g:37:1: model returns [Model model = new Model()] : ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+ ;
    public final Model model() throws RecognitionException, ParseException {
        Model model =  new Model();


        PredicateAbstract pre =null;

        FormulaSoft sf =null;

        FormulaHard hf =null;

        FormulaCardinality cf =null;


        try {
            // com/googlecode/rockit/parser/Model.g:37:64: ( ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+ )
            // com/googlecode/rockit/parser/Model.g:38:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+
            {
            // com/googlecode/rockit/parser/Model.g:38:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+
            int cnt1=0;
            loop1:
            do {
                int alt1=8;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:39:4: ( ML_COMMENT )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:39:4: ( ML_COMMENT )
            	    // com/googlecode/rockit/parser/Model.g:39:5: ML_COMMENT
            	    {
            	    match(input,ML_COMMENT,FOLLOW_ML_COMMENT_in_model53); 

            	    }


            	    }
            	    break;
            	case 2 :
            	    // com/googlecode/rockit/parser/Model.g:39:19: ( SL_COMMENT )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:39:19: ( SL_COMMENT )
            	    // com/googlecode/rockit/parser/Model.g:39:20: SL_COMMENT
            	    {
            	    match(input,SL_COMMENT,FOLLOW_SL_COMMENT_in_model59); 

            	    }


            	    }
            	    break;
            	case 3 :
            	    // com/googlecode/rockit/parser/Model.g:39:34: ( NEWLINE )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:39:34: ( NEWLINE )
            	    // com/googlecode/rockit/parser/Model.g:39:35: NEWLINE
            	    {
            	    match(input,NEWLINE,FOLLOW_NEWLINE_in_model65); 

            	    }


            	    }
            	    break;
            	case 4 :
            	    // com/googlecode/rockit/parser/Model.g:40:4: (pre= predicateDefinition )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:40:4: (pre= predicateDefinition )
            	    // com/googlecode/rockit/parser/Model.g:40:5: pre= predicateDefinition
            	    {
            	    pushFollow(FOLLOW_predicateDefinition_in_model76);
            	    pre=predicateDefinition();

            	    state._fsp--;


            	    model.addPredicate(pre);

            	    }


            	    }
            	    break;
            	case 5 :
            	    // com/googlecode/rockit/parser/Model.g:41:4: (sf= softFormulaDefinition )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:41:4: (sf= softFormulaDefinition )
            	    // com/googlecode/rockit/parser/Model.g:41:5: sf= softFormulaDefinition
            	    {
            	    pushFollow(FOLLOW_softFormulaDefinition_in_model88);
            	    sf=softFormulaDefinition();

            	    state._fsp--;


            	    FormulaSoft formula = sf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;
            	case 6 :
            	    // com/googlecode/rockit/parser/Model.g:48:4: (hf= hardFormulaDefinition )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:48:4: (hf= hardFormulaDefinition )
            	    // com/googlecode/rockit/parser/Model.g:48:5: hf= hardFormulaDefinition
            	    {
            	    pushFollow(FOLLOW_hardFormulaDefinition_in_model100);
            	    hf=hardFormulaDefinition();

            	    state._fsp--;


            	    FormulaHard formula = hf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;
            	case 7 :
            	    // com/googlecode/rockit/parser/Model.g:55:4: (cf= cardinalityFormulaDefiniton )
            	    {
            	    // com/googlecode/rockit/parser/Model.g:55:4: (cf= cardinalityFormulaDefiniton )
            	    // com/googlecode/rockit/parser/Model.g:55:5: cf= cardinalityFormulaDefiniton
            	    {
            	    pushFollow(FOLLOW_cardinalityFormulaDefiniton_in_model112);
            	    cf=cardinalityFormulaDefiniton();

            	    state._fsp--;


            	    FormulaCardinality formula = cf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return model;
    }
    // $ANTLR end "model"



    // $ANTLR start "softFormulaDefinition"
    // com/googlecode/rockit/parser/Model.g:70:1: softFormulaDefinition returns [FormulaSoft formula] : ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition ;
    public final FormulaSoft softFormulaDefinition() throws RecognitionException, ParseException {
        FormulaSoft formula = null;


        Token f=null;
        Token dv=null;
        FormulaHard formulaHard =null;


        try {
            // com/googlecode/rockit/parser/Model.g:70:74: ( ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition )
            // com/googlecode/rockit/parser/Model.g:71:5: ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition
            {

                    Double w = null;
                    VariableDouble doubleVariable = null;
                

            // com/googlecode/rockit/parser/Model.g:75:5: ( (f= FLOAT ) | (dv= ID ':' ) )
            int alt2=2;
            switch ( input.LA(1) ) {
            case FLOAT:
                {
                alt2=1;
                }
                break;
            case ID:
                {
                alt2=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }

            switch (alt2) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:75:6: (f= FLOAT )
                    {
                    // com/googlecode/rockit/parser/Model.g:75:6: (f= FLOAT )
                    // com/googlecode/rockit/parser/Model.g:75:7: f= FLOAT
                    {
                    f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_softFormulaDefinition164); 

                    }



                            if(f != null){
                              w = Double.parseDouble(f.getText());
                            }
                         

                    }
                    break;
                case 2 :
                    // com/googlecode/rockit/parser/Model.g:80:7: (dv= ID ':' )
                    {
                    // com/googlecode/rockit/parser/Model.g:80:7: (dv= ID ':' )
                    // com/googlecode/rockit/parser/Model.g:80:8: dv= ID ':'
                    {
                    dv=(Token)match(input,ID,FOLLOW_ID_in_softFormulaDefinition177); 

                    match(input,19,FOLLOW_19_in_softFormulaDefinition179); 

                    }



                            if(dv !=null){
                              doubleVariable = new VariableDouble(dv.getText());
                            }
                        

                    }
                    break;

            }



                    String name = "f"+formulaId;
                    formula = new FormulaSoft();
                    formula.setName(name);
                    formulaId++;
                    if(w != null) formula.setWeight(w);
                    if(doubleVariable != null) formula.setDoubleVariable(doubleVariable);
                

            pushFollow(FOLLOW_formulaBodyDefinition_in_softFormulaDefinition191);
            formulaHard=formulaBodyDefinition();

            state._fsp--;



                  formula.setForVariables(formulaHard.getForVariables());
                  formula.setIfExpressions(formulaHard.getIfExpressions());
                  formula.setRestrictions(formulaHard.getRestrictions());
                  if(formulaHard.isConjunction()) formula.setConjunction();
                  if(formulaHard.isDisjunction()) formula.setDisjunction();
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "softFormulaDefinition"



    // $ANTLR start "hardFormulaDefinition"
    // com/googlecode/rockit/parser/Model.g:105:1: hardFormulaDefinition returns [FormulaHard formula] : formulaHard= formulaBodyDefinition '.' ;
    public final FormulaHard hardFormulaDefinition() throws RecognitionException, ParseException {
        FormulaHard formula = null;


        FormulaHard formulaHard =null;


        try {
            // com/googlecode/rockit/parser/Model.g:105:74: (formulaHard= formulaBodyDefinition '.' )
            // com/googlecode/rockit/parser/Model.g:106:5: formulaHard= formulaBodyDefinition '.'
            {
            pushFollow(FOLLOW_formulaBodyDefinition_in_hardFormulaDefinition230);
            formulaHard=formulaBodyDefinition();

            state._fsp--;



                formula = formulaHard;
                

            match(input,18,FOLLOW_18_in_hardFormulaDefinition242); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "hardFormulaDefinition"



    // $ANTLR start "cardinalityFormulaDefiniton"
    // com/googlecode/rockit/parser/Model.g:119:1: cardinalityFormulaDefiniton returns [FormulaCardinality formula] : ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT ) ;
    public final FormulaCardinality cardinalityFormulaDefiniton() throws RecognitionException, ParseException {
        FormulaCardinality formula = null;


        Token var1=null;
        Token var2=null;
        Token operator=null;
        Token cardinality=null;
        FormulaHard hardFormula =null;


        try {
            // com/googlecode/rockit/parser/Model.g:119:87: ( ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT ) )
            // com/googlecode/rockit/parser/Model.g:121:5: ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT )
            {

                  HashSet<VariableType> preliminaryOverVariables = new HashSet<VariableType>();
                

            // com/googlecode/rockit/parser/Model.g:124:5: ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' )
            // com/googlecode/rockit/parser/Model.g:124:6: '|' (var1= ID ) ( ',' (var2= ID ) )* '|'
            {
            match(input,23,FOLLOW_23_in_cardinalityFormulaDefiniton284); 

            // com/googlecode/rockit/parser/Model.g:124:10: (var1= ID )
            // com/googlecode/rockit/parser/Model.g:124:11: var1= ID
            {
            var1=(Token)match(input,ID,FOLLOW_ID_in_cardinalityFormulaDefiniton289); 

            }



                     if(var1 != null){
                       preliminaryOverVariables.add(new VariableType(var1.getText()));
                     }
                    

            // com/googlecode/rockit/parser/Model.g:129:5: ( ',' (var2= ID ) )*
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:129:6: ',' (var2= ID )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_cardinalityFormulaDefiniton298); 

            	    // com/googlecode/rockit/parser/Model.g:129:10: (var2= ID )
            	    // com/googlecode/rockit/parser/Model.g:129:11: var2= ID
            	    {
            	    var2=(Token)match(input,ID,FOLLOW_ID_in_cardinalityFormulaDefiniton303); 

            	    }



            	             if(var2 != null){
            	               preliminaryOverVariables.add(new VariableType(var2.getText()));
            	             }
            	            

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input,23,FOLLOW_23_in_cardinalityFormulaDefiniton314); 

            }


            pushFollow(FOLLOW_formulaBodyDefinition_in_cardinalityFormulaDefiniton323);
            hardFormula=formulaBodyDefinition();

            state._fsp--;



                  // if we have a cardinality formula, we need to figure out if the preliminary over variables
                  // exist in the for variables of the formula.
                  HashSet<VariableType> forVariables = hardFormula.getForVariables();
                  HashSet<VariableType> overVariables = new HashSet<VariableType>();
                  for(VariableType search :preliminaryOverVariables){
                     boolean found = false;
                     for(VariableType var : forVariables){
                       if(search.getName().equals(var.getName())){
                         overVariables.add(var);
                         found=true;
                       }
                     }
                     if(!found){
                       throw new ParseException("Over-variable " + search + " could not be found in the formula body of the following formula: " + formula.toString() + " Every over variable has to occur in the formula body.");
                     }
                  }
                

            // com/googlecode/rockit/parser/Model.g:155:5: (operator= ( '<=' | '>=' ) )
            // com/googlecode/rockit/parser/Model.g:155:6: operator= ( '<=' | '>=' )
            {
            operator=(Token)input.LT(1);

            if ( (input.LA(1) >= 20 && input.LA(1) <= 21) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }


            // com/googlecode/rockit/parser/Model.g:155:28: (cardinality= FLOAT )
            // com/googlecode/rockit/parser/Model.g:155:29: cardinality= FLOAT
            {
            cardinality=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_cardinalityFormulaDefiniton353); 

            }



                  int card = Integer.parseInt(cardinality.getText());
                  boolean lessEqual=true;
                  if(operator.getText().equals(">=")){
                  	lessEqual=false;
                  }
                  // now we have all information to create the cardinalityformula
                  formula = new FormulaCardinality(hardFormula.getName(), hardFormula.getForVariables(), hardFormula.getIfExpressions(),
                  overVariables, hardFormula.getRestrictions(), card, lessEqual);
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "cardinalityFormulaDefiniton"



    // $ANTLR start "formulaBodyDefinition"
    // com/googlecode/rockit/parser/Model.g:171:1: formulaBodyDefinition returns [FormulaHard formula] : e1= expressionDefinition ( 'v' e2= expressionDefinition )* ;
    public final FormulaHard formulaBodyDefinition() throws RecognitionException, ParseException {
        FormulaHard formula = null;


        ArrayList<IfExpression> e1 =null;

        ArrayList<IfExpression> e2 =null;


        try {
            // com/googlecode/rockit/parser/Model.g:171:74: (e1= expressionDefinition ( 'v' e2= expressionDefinition )* )
            // com/googlecode/rockit/parser/Model.g:172:5: e1= expressionDefinition ( 'v' e2= expressionDefinition )*
            {
            pushFollow(FOLLOW_expressionDefinition_in_formulaBodyDefinition388);
            e1=expressionDefinition();

            state._fsp--;



                              String name = "f"+formulaId;
                              formula = new FormulaHard();
                              formula.setName(name);
                              formulaId++;
                
                              for(IfExpression ifExpr : e1){
                               if(ifExpr instanceof PredicateExpression){ 
                                PredicateExpression expr = (PredicateExpression) ifExpr;  
                                if(expr.getPredicate().isObserved()){
                                 expr.setPositive(!expr.isPositive());
                                 formula.addIfExpression(expr);
                                }else{
                                 formula.addRestriction(expr);
                                }
                               }else{
                                 formula.addIfExpression(ifExpr);
                               }
                              }
                             

            // com/googlecode/rockit/parser/Model.g:192:5: ( 'v' e2= expressionDefinition )*
            loop4:
            do {
                int alt4=2;
                switch ( input.LA(1) ) {
                case 22:
                    {
                    alt4=1;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:192:6: 'v' e2= expressionDefinition
            	    {
            	    match(input,22,FOLLOW_22_in_formulaBodyDefinition397); 

            	    pushFollow(FOLLOW_expressionDefinition_in_formulaBodyDefinition401);
            	    e2=expressionDefinition();

            	    state._fsp--;


//Me IMPORTANT
            	                      for(IfExpression ifExpr : e2){
            	                       if(ifExpr instanceof PredicateExpression){ 
            	                        PredicateExpression expr = (PredicateExpression) ifExpr;  
            	                        if(expr.getPredicate().isObserved()){
            	                         expr.setPositive(!expr.isPositive());
            	                         formula.addIfExpression(expr);
            	                        }else{
            	                         formula.addRestriction(expr);
            	                        }
            	                       }else{
            	                         formula.addIfExpression(ifExpr);
            	                       }
            	                      }
            	                     

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);



                  formula.setAllAsForVariables();
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "formulaBodyDefinition"



    // $ANTLR start "expressionDefinition"
    // com/googlecode/rockit/parser/Model.g:219:1: expressionDefinition returns [ArrayList<IfExpression> expressions = new ArrayList<IfExpression>();] : (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) ) )* ')' ;
    public final ArrayList<IfExpression> expressionDefinition() throws RecognitionException, ParseException {
        ArrayList<IfExpression> expressions =  new ArrayList<IfExpression>();;


        Token n0=null;
        Token predId=null;
        Token var1=null;
        Token n1=null;
        Token string1=null;
        Token var2=null;
        Token n2=null;
        Token string2=null;

        try {
            // com/googlecode/rockit/parser/Model.g:219:122: ( (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) ) )* ')' )
            // com/googlecode/rockit/parser/Model.g:220:6: (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) ) )* ')'
            {
            // com/googlecode/rockit/parser/Model.g:220:6: (n0= NOT )?
            int alt5=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt5=1;
                    }
                    break;
            }

            switch (alt5) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:220:7: n0= NOT
                    {
                    n0=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition449); 

                    }
                    break;

            }


            predId=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition455); 


                               // get Predicate from id
                               PredicateAbstract search = new Predicate(predId.getText(), false);
                               PredicateAbstract predicate = null;
                               if(!predicates.contains(search)){
                                  throw new ParseException("Predicate " + Messages.printTokenDetails(predId) + " has not been defined. Check for typos (case sensitive)."); 
                               }else{
                                  predicate = predicates.ceiling(search);
                               }
                               // negated or not?
                               boolean isPositive = false;
                               if(n0==null){
                                isPositive=true;
                               }
                               // create new expression.
                               expressions.add(new PredicateExpression(isPositive, predicate));
                              

            match(input,16,FOLLOW_16_in_expressionDefinition464); 

            // com/googlecode/rockit/parser/Model.g:237:10: ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) )
            int alt7=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt7=1;
                }
                break;
            case NOT:
            case STRING:
                {
                alt7=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:237:11: (var1= ID )
                    {
                    // com/googlecode/rockit/parser/Model.g:237:11: (var1= ID )
                    // com/googlecode/rockit/parser/Model.g:237:12: var1= ID
                    {
                    var1=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition470); 

                    }


                    }
                    break;
                case 2 :
                    // com/googlecode/rockit/parser/Model.g:237:21: ( (n1= NOT )? (string1= STRING ) )
                    {
                    // com/googlecode/rockit/parser/Model.g:237:21: ( (n1= NOT )? (string1= STRING ) )
                    // com/googlecode/rockit/parser/Model.g:237:22: (n1= NOT )? (string1= STRING )
                    {
                    // com/googlecode/rockit/parser/Model.g:237:22: (n1= NOT )?
                    int alt6=2;
                    switch ( input.LA(1) ) {
                        case NOT:
                            {
                            alt6=1;
                            }
                            break;
                    }

                    switch (alt6) {
                        case 1 :
                            // com/googlecode/rockit/parser/Model.g:237:23: n1= NOT
                            {
                            n1=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition477); 

                            }
                            break;

                    }


                    // com/googlecode/rockit/parser/Model.g:237:31: (string1= STRING )
                    // com/googlecode/rockit/parser/Model.g:237:32: string1= STRING
                    {
                    string1=(Token)match(input,STRING,FOLLOW_STRING_in_expressionDefinition483); 

                    }


                    }


                    }
                    break;

            }


             
                              int zahl = 0;
                              PredicateExpression predExpr = (PredicateExpression) expressions.get(0);
                              VariableAbstract v = null;
                              if(var1!=null){
                               v =new VariableType();
                               v.setName(var1.getText());
                              } else {
                                v = new VariableString(u.getKey(string1.getText().replace("\"", "")));
                              }
                              predExpr.addVariable(v);
                             

            // com/googlecode/rockit/parser/Model.g:249:6: ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) ) )*
            loop10:
            do {
                int alt10=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt10=1;
                    }
                    break;

                }

                switch (alt10) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:249:7: ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionDefinition496); 

            	    // com/googlecode/rockit/parser/Model.g:249:11: ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) )
            	    int alt9=2;
            	    switch ( input.LA(1) ) {
            	    case ID:
            	        {
            	        alt9=1;
            	        }
            	        break;
            	    case NOT:
            	    case STRING:
            	        {
            	        alt9=2;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt9) {
            	        case 1 :
            	            // com/googlecode/rockit/parser/Model.g:249:12: (var2= ID )
            	            {
            	            // com/googlecode/rockit/parser/Model.g:249:12: (var2= ID )
            	            // com/googlecode/rockit/parser/Model.g:249:13: var2= ID
            	            {
            	            var2=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition502); 

            	            }


            	            }
            	            break;
            	        case 2 :
            	            // com/googlecode/rockit/parser/Model.g:249:22: ( (n2= NOT )? (string2= STRING ) )
            	            {
            	            // com/googlecode/rockit/parser/Model.g:249:22: ( (n2= NOT )? (string2= STRING ) )
            	            // com/googlecode/rockit/parser/Model.g:249:23: (n2= NOT )? (string2= STRING )
            	            {
            	            // com/googlecode/rockit/parser/Model.g:249:23: (n2= NOT )?
            	            int alt8=2;
            	            switch ( input.LA(1) ) {
            	                case NOT:
            	                    {
            	                    alt8=1;
            	                    }
            	                    break;
            	            }

            	            switch (alt8) {
            	                case 1 :
            	                    // com/googlecode/rockit/parser/Model.g:249:24: n2= NOT
            	                    {
            	                    n2=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition509); 

            	                    }
            	                    break;

            	            }


            	            // com/googlecode/rockit/parser/Model.g:249:32: (string2= STRING )
            	            // com/googlecode/rockit/parser/Model.g:249:33: string2= STRING
            	            {
            	            string2=(Token)match(input,STRING,FOLLOW_STRING_in_expressionDefinition515); 

            	            }


            	            }


            	            }
            	            break;

            	    }


            	     
            	                      PredicateExpression predExpr2 = (PredicateExpression) expressions.get(0);
            	                      VariableAbstract v2 = null;
            	                      if(var2!=null){
            	                       v2 =new VariableType();
            	                       v2.setName(var2.getText());
            	                      } else {
            	                        v2 = new VariableString(u.getKey(string2.getText().replace("\"", "")));
            	                      }
            	                      predExpr.addVariable(v2);
            	                     

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);



                  for(IfExpression ifExpression : expressions){ 
                   if(ifExpression instanceof PredicateExpression){
                     PredicateExpression expression = (PredicateExpression) ifExpression;
                     // check if types and variable length are ok.
                     PredicateAbstract p = expression.getPredicate();
                     ArrayList<Type> types = p.getTypes();
                     ArrayList<VariableAbstract> variables = expression.getVariables();
                     if(p instanceof Predicate && !(types.size()==variables.size())){
                        throw new ParseException("Predicate " + p + " has not the same size in types and variables in the current equation. Types are: "+types.toString()+". Variables are: "+variables.toString()); 
                     }else if(p instanceof PredicateDouble && !(types.size()+1==variables.size())){
                        throw new ParseException("Double Predicate " + p + " has not the same size in types and variables in the current equation. Types are: "+types.toString()+", Double]. Variables are: "+variables.toString());          
                     }
                     // set last variable of PredicateDouble to DoubleVariable. Before we have no chance to detect if this is the last variable.
                     if(p instanceof PredicateDouble){
                        VariableAbstract transformToDoubleVar = variables.get(variables.size()-1);
                        VariableDouble transformed = new VariableDouble(transformToDoubleVar.getName());
                        variables.set(variables.size()-1, transformed);
                        expression.setVariables(variables);
                     }
                     // set variable types (in case of the double variable, the last variable is not tuched.
                     for(int i = 0; i<types.size(); i++){
                      Type type = types.get(i);
                      VariableAbstract varAbstract = variables.get(i);
                      if(varAbstract instanceof VariableType){
                          VariableType var = (VariableType) varAbstract;
                          var.setType(type); 
                      }else if(varAbstract instanceof VariableDouble){
                         throw new ParseException("No double Type allowed "+ type+". Variable must not be a double var. "+ varAbstract);          
                      }
                     }
                    }
                   }
                  

            match(input,17,FOLLOW_17_in_expressionDefinition536); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expressions;
    }
    // $ANTLR end "expressionDefinition"



    // $ANTLR start "predicateDefinition"
    // com/googlecode/rockit/parser/Model.g:301:1: predicateDefinition returns [PredicateAbstract predicate] : ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')' ;
    public final PredicateAbstract predicateDefinition() throws RecognitionException, ParseException {
        PredicateAbstract predicate = null;


        Token id1=null;
        Token d=null;
        Token id2=null;
        Token STAR1=null;
        Type t1 =null;

        Type t2 =null;


        try {
            // com/googlecode/rockit/parser/Model.g:301:80: ( ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')' )
            // com/googlecode/rockit/parser/Model.g:302:9: ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')'
            {
            // com/googlecode/rockit/parser/Model.g:302:9: ( STAR )?
            int alt11=2;
            switch ( input.LA(1) ) {
                case STAR:
                    {
                    alt11=1;
                    }
                    break;
            }

            switch (alt11) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:302:10: STAR
                    {
                    STAR1=(Token)match(input,STAR,FOLLOW_STAR_in_predicateDefinition566); 

                    }
                    break;

            }


            id1=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition572); 


                                boolean isHidden = false;
                                if(STAR1==null){
                                  isHidden=true;
                                }
                                ArrayList<Type> types = new ArrayList<Type>();
                               

            match(input,16,FOLLOW_16_in_predicateDefinition584); 

            pushFollow(FOLLOW_typeDefinition_in_predicateDefinition588);
            t1=typeDefinition();

            state._fsp--;


            types.add(t1);

            // com/googlecode/rockit/parser/Model.g:310:9: ( ',' t2= typeDefinition )*
            loop12:
            do {
                int alt12=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    switch ( input.LA(2) ) {
                    case ID:
                        {
                        alt12=1;
                        }
                        break;

                    }

                    }
                    break;

                }

                switch (alt12) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:310:10: ',' t2= typeDefinition
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_predicateDefinition601); 

            	    pushFollow(FOLLOW_typeDefinition_in_predicateDefinition605);
            	    t2=typeDefinition();

            	    state._fsp--;


            	    types.add(t2);

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            // com/googlecode/rockit/parser/Model.g:311:9: ( ',' d= DOUBLEVAR (id2= ID )? )?
            int alt14=2;
            switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt14=1;
                    }
                    break;
            }

            switch (alt14) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:311:10: ',' d= DOUBLEVAR (id2= ID )?
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_predicateDefinition622); 

                    d=(Token)match(input,DOUBLEVAR,FOLLOW_DOUBLEVAR_in_predicateDefinition626); 

                    // com/googlecode/rockit/parser/Model.g:311:26: (id2= ID )?
                    int alt13=2;
                    switch ( input.LA(1) ) {
                        case ID:
                            {
                            alt13=1;
                            }
                            break;
                    }

                    switch (alt13) {
                        case 1 :
                            // com/googlecode/rockit/parser/Model.g:311:27: id2= ID
                            {
                            id2=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition631); 

                            }
                            break;

                    }


                    }
                    break;

            }



            			             if(d == null){
            			                    predicate = new Predicate(id1.getText(),isHidden);
            			             }else{
            			                    if(isHidden){
            			                       throw new ParseException("Double predicates are not allowed to be hidden. Predicate " + Messages.printTokenDetails(id1) + " violates that rule."); 
                                      }
            			                    predicate = new PredicateDouble(id1.getText(),isHidden);
            			                    
            			             }
            			             predicate.setTypes(types);
            			             if(predicates.contains(predicate)){
                                  throw new ParseException("Predicate " + Messages.printTokenDetails(id1) + " had already been created. The new predicate is ignored"); 
                               }else{
                                  predicates.add(predicate);
                               }
                    

            match(input,17,FOLLOW_17_in_predicateDefinition655); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return predicate;
    }
    // $ANTLR end "predicateDefinition"



    // $ANTLR start "typeDefinition"
    // com/googlecode/rockit/parser/Model.g:335:1: typeDefinition returns [Type type] : ID ;
    public final Type typeDefinition() throws RecognitionException {
        Type type = null;


        Token ID2=null;

        try {
            // com/googlecode/rockit/parser/Model.g:335:35: ( ID )
            // com/googlecode/rockit/parser/Model.g:336:5: ID
            {
            ID2=(Token)match(input,ID,FOLLOW_ID_in_typeDefinition679); 

            Type t=new Type(ID2.getText());
                    types.add(t);
                    type = types.ceiling(t);
                    
                   

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return type;
    }
    // $ANTLR end "typeDefinition"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\17\uffff";
    static final String DFA1_eofS =
        "\1\1\14\uffff\1\5\1\uffff";
    static final String DFA1_minS =
        "\1\6\5\uffff\1\20\3\uffff\1\10\1\4\1\5\1\6\1\4";
    static final String DFA1_maxS =
        "\1\27\5\uffff\1\23\3\uffff\1\16\1\21\1\16\1\27\1\21";
    static final String DFA1_acceptS =
        "\1\uffff\1\10\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\1\7\5\uffff";
    static final String DFA1_specialS =
        "\17\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\7\1\uffff\1\6\1\2\1\4\1\10\1\3\1\5\11\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            "\1\12\2\uffff\1\7",
            "",
            "",
            "",
            "\1\13\2\uffff\1\10\2\uffff\1\10",
            "\1\14\14\uffff\1\15",
            "\1\5\2\uffff\1\16\2\uffff\1\10\2\uffff\1\10",
            "\1\5\1\uffff\6\5\4\uffff\1\10\3\uffff\1\10\1\5",
            "\1\14\14\uffff\1\15"
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()+ loopback of 38:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+";
        }
    }
 

    public static final BitSet FOLLOW_ML_COMMENT_in_model53 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_SL_COMMENT_in_model59 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_NEWLINE_in_model65 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_predicateDefinition_in_model76 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_softFormulaDefinition_in_model88 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_hardFormulaDefinition_in_model100 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_cardinalityFormulaDefiniton_in_model112 = new BitSet(new long[]{0x0000000000803F42L});
    public static final BitSet FOLLOW_FLOAT_in_softFormulaDefinition164 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_ID_in_softFormulaDefinition177 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_softFormulaDefinition179 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_softFormulaDefinition191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_hardFormulaDefinition230 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_hardFormulaDefinition242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_cardinalityFormulaDefiniton284 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_cardinalityFormulaDefiniton289 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_COMMA_in_cardinalityFormulaDefiniton298 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_cardinalityFormulaDefiniton303 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_23_in_cardinalityFormulaDefiniton314 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_cardinalityFormulaDefiniton323 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_set_in_cardinalityFormulaDefiniton343 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FLOAT_in_cardinalityFormulaDefiniton353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionDefinition_in_formulaBodyDefinition388 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_formulaBodyDefinition397 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_expressionDefinition_in_formulaBodyDefinition401 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition449 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition455 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_expressionDefinition464 = new BitSet(new long[]{0x0000000000004900L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition470 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition477 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_expressionDefinition483 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_COMMA_in_expressionDefinition496 = new BitSet(new long[]{0x0000000000004900L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition502 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition509 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_expressionDefinition515 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_17_in_expressionDefinition536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_predicateDefinition566 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition572 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_predicateDefinition584 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_typeDefinition_in_predicateDefinition588 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_COMMA_in_predicateDefinition601 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_typeDefinition_in_predicateDefinition605 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_COMMA_in_predicateDefinition622 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DOUBLEVAR_in_predicateDefinition626 = new BitSet(new long[]{0x0000000000020100L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition631 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_predicateDefinition655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_typeDefinition679 = new BitSet(new long[]{0x0000000000000002L});

}