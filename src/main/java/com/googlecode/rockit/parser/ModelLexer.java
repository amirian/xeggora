// $ANTLR 3.4 com/googlecode/rockit/parser/Model.g 2015-11-10 15:30:56
 
package com.googlecode.rockit.parser; 


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class ModelLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public ModelLexer() {} 
    public ModelLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ModelLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "com/googlecode/rockit/parser/Model.g"; }

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:6:7: ( '(' )
            // com/googlecode/rockit/parser/Model.g:6:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:7:7: ( ')' )
            // com/googlecode/rockit/parser/Model.g:7:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:8:7: ( '.' )
            // com/googlecode/rockit/parser/Model.g:8:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:9:7: ( ':' )
            // com/googlecode/rockit/parser/Model.g:9:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:10:7: ( '<=' )
            // com/googlecode/rockit/parser/Model.g:10:9: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:11:7: ( '>=' )
            // com/googlecode/rockit/parser/Model.g:11:9: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:12:7: ( 'v' )
            // com/googlecode/rockit/parser/Model.g:12:9: 'v'
            {
            match('v'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:13:7: ( '|' )
            // com/googlecode/rockit/parser/Model.g:13:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "DOUBLEVAR"
    public final void mDOUBLEVAR() throws RecognitionException {
        try {
            int _type = DOUBLEVAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:344:10: ( 'float_' )
            // com/googlecode/rockit/parser/Model.g:344:12: 'float_'
            {
            match("float_"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOUBLEVAR"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:345:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // com/googlecode/rockit/parser/Model.g:345:9: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // com/googlecode/rockit/parser/Model.g:345:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt1=1;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:347:7: ( '\"' ( options {greedy=false; } : . )* ( '\"' | ')' | ',' ) )
            // com/googlecode/rockit/parser/Model.g:347:10: '\"' ( options {greedy=false; } : . )* ( '\"' | ')' | ',' )
            {
            match('\"'); 

            // com/googlecode/rockit/parser/Model.g:347:14: ( options {greedy=false; } : . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='\"'||LA2_0==')'||LA2_0==',') ) {
                    alt2=2;
                }
                else if ( ((LA2_0 >= '\u0000' && LA2_0 <= '!')||(LA2_0 >= '#' && LA2_0 <= '(')||(LA2_0 >= '*' && LA2_0 <= '+')||(LA2_0 >= '-' && LA2_0 <= '\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:347:41: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            if ( input.LA(1)=='\"'||input.LA(1)==')'||input.LA(1)==',' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:348:8: ( '\\n' )
            // com/googlecode/rockit/parser/Model.g:348:10: '\\n'
            {
            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:349:5: ( ( ' ' | '\\t' | '\\r' )+ )
            // com/googlecode/rockit/parser/Model.g:349:9: ( ' ' | '\\t' | '\\r' )+
            {
            // com/googlecode/rockit/parser/Model.g:349:9: ( ' ' | '\\t' | '\\r' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case '\t':
                case '\r':
                case ' ':
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:350:5: ( '*' )
            // com/googlecode/rockit/parser/Model.g:350:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "HASH"
    public final void mHASH() throws RecognitionException {
        try {
            int _type = HASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:351:5: ( '#' )
            // com/googlecode/rockit/parser/Model.g:351:9: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HASH"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:352:6: ( ',' )
            // com/googlecode/rockit/parser/Model.g:352:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:353:4: ( '!' )
            // com/googlecode/rockit/parser/Model.g:353:7: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:355:3: ( ( '+' | '-' )? ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
            // com/googlecode/rockit/parser/Model.g:355:5: ( '+' | '-' )? ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )?
            {
            // com/googlecode/rockit/parser/Model.g:355:5: ( '+' | '-' )?
            int alt4=2;
            switch ( input.LA(1) ) {
                case '+':
                case '-':
                    {
                    alt4=1;
                    }
                    break;
            }

            switch (alt4) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // com/googlecode/rockit/parser/Model.g:355:15: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt5=1;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            // com/googlecode/rockit/parser/Model.g:355:29: ( '.' ( '0' .. '9' )* )?
            int alt7=2;
            switch ( input.LA(1) ) {
                case '.':
                    {
                    alt7=1;
                    }
                    break;
            }

            switch (alt7) {
                case 1 :
                    // com/googlecode/rockit/parser/Model.g:355:31: '.' ( '0' .. '9' )*
                    {
                    match('.'); 

                    // com/googlecode/rockit/parser/Model.g:355:35: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt6=1;
                            }
                            break;

                        }

                        switch (alt6) {
                    	case 1 :
                    	    // com/googlecode/rockit/parser/Model.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:358:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // com/googlecode/rockit/parser/Model.g:358:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // com/googlecode/rockit/parser/Model.g:358:14: ( options {greedy=false; } : . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='*') ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1=='/') ) {
                        alt8=2;
                    }
                    else if ( ((LA8_1 >= '\u0000' && LA8_1 <= '.')||(LA8_1 >= '0' && LA8_1 <= '\uFFFF')) ) {
                        alt8=1;
                    }


                }
                else if ( ((LA8_0 >= '\u0000' && LA8_0 <= ')')||(LA8_0 >= '+' && LA8_0 <= '\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:358:41: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/googlecode/rockit/parser/Model.g:360:11: ( '//' (~ ( NEWLINE ) )* )
            // com/googlecode/rockit/parser/Model.g:360:13: '//' (~ ( NEWLINE ) )*
            {
            match("//"); 



            // com/googlecode/rockit/parser/Model.g:360:18: (~ ( NEWLINE ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // com/googlecode/rockit/parser/Model.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SL_COMMENT"

    public void mTokens() throws RecognitionException {
        // com/googlecode/rockit/parser/Model.g:1:8: ( T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | DOUBLEVAR | ID | STRING | NEWLINE | WS | STAR | HASH | COMMA | NOT | FLOAT | ML_COMMENT | SL_COMMENT )
        int alt10=20;
        switch ( input.LA(1) ) {
        case '(':
            {
            alt10=1;
            }
            break;
        case ')':
            {
            alt10=2;
            }
            break;
        case '.':
            {
            alt10=3;
            }
            break;
        case ':':
            {
            alt10=4;
            }
            break;
        case '<':
            {
            alt10=5;
            }
            break;
        case '>':
            {
            alt10=6;
            }
            break;
        case 'v':
            {
            switch ( input.LA(2) ) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt10=10;
                }
                break;
            default:
                alt10=7;
            }

            }
            break;
        case '|':
            {
            alt10=8;
            }
            break;
        case 'f':
            {
            switch ( input.LA(2) ) {
            case 'l':
                {
                switch ( input.LA(3) ) {
                case 'o':
                    {
                    switch ( input.LA(4) ) {
                    case 'a':
                        {
                        switch ( input.LA(5) ) {
                        case 't':
                            {
                            switch ( input.LA(6) ) {
                            case '_':
                                {
                                switch ( input.LA(7) ) {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                case 'G':
                                case 'H':
                                case 'I':
                                case 'J':
                                case 'K':
                                case 'L':
                                case 'M':
                                case 'N':
                                case 'O':
                                case 'P':
                                case 'Q':
                                case 'R':
                                case 'S':
                                case 'T':
                                case 'U':
                                case 'V':
                                case 'W':
                                case 'X':
                                case 'Y':
                                case 'Z':
                                case '_':
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                case 'g':
                                case 'h':
                                case 'i':
                                case 'j':
                                case 'k':
                                case 'l':
                                case 'm':
                                case 'n':
                                case 'o':
                                case 'p':
                                case 'q':
                                case 'r':
                                case 's':
                                case 't':
                                case 'u':
                                case 'v':
                                case 'w':
                                case 'x':
                                case 'y':
                                case 'z':
                                    {
                                    alt10=10;
                                    }
                                    break;
                                default:
                                    alt10=9;
                                }

                                }
                                break;
                            default:
                                alt10=10;
                            }

                            }
                            break;
                        default:
                            alt10=10;
                        }

                        }
                        break;
                    default:
                        alt10=10;
                    }

                    }
                    break;
                default:
                    alt10=10;
                }

                }
                break;
            default:
                alt10=10;
            }

            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt10=10;
            }
            break;
        case '\"':
            {
            alt10=11;
            }
            break;
        case '\n':
            {
            alt10=12;
            }
            break;
        case '\t':
        case '\r':
        case ' ':
            {
            alt10=13;
            }
            break;
        case '*':
            {
            alt10=14;
            }
            break;
        case '#':
            {
            alt10=15;
            }
            break;
        case ',':
            {
            alt10=16;
            }
            break;
        case '!':
            {
            alt10=17;
            }
            break;
        case '+':
        case '-':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt10=18;
            }
            break;
        case '/':
            {
            switch ( input.LA(2) ) {
            case '*':
                {
                alt10=19;
                }
                break;
            case '/':
                {
                alt10=20;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 19, input);

                throw nvae;

            }

            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("", 10, 0, input);

            throw nvae;

        }

        switch (alt10) {
            case 1 :
                // com/googlecode/rockit/parser/Model.g:1:10: T__16
                {
                mT__16(); 


                }
                break;
            case 2 :
                // com/googlecode/rockit/parser/Model.g:1:16: T__17
                {
                mT__17(); 


                }
                break;
            case 3 :
                // com/googlecode/rockit/parser/Model.g:1:22: T__18
                {
                mT__18(); 


                }
                break;
            case 4 :
                // com/googlecode/rockit/parser/Model.g:1:28: T__19
                {
                mT__19(); 


                }
                break;
            case 5 :
                // com/googlecode/rockit/parser/Model.g:1:34: T__20
                {
                mT__20(); 


                }
                break;
            case 6 :
                // com/googlecode/rockit/parser/Model.g:1:40: T__21
                {
                mT__21(); 


                }
                break;
            case 7 :
                // com/googlecode/rockit/parser/Model.g:1:46: T__22
                {
                mT__22(); 


                }
                break;
            case 8 :
                // com/googlecode/rockit/parser/Model.g:1:52: T__23
                {
                mT__23(); 


                }
                break;
            case 9 :
                // com/googlecode/rockit/parser/Model.g:1:58: DOUBLEVAR
                {
                mDOUBLEVAR(); 


                }
                break;
            case 10 :
                // com/googlecode/rockit/parser/Model.g:1:68: ID
                {
                mID(); 


                }
                break;
            case 11 :
                // com/googlecode/rockit/parser/Model.g:1:71: STRING
                {
                mSTRING(); 


                }
                break;
            case 12 :
                // com/googlecode/rockit/parser/Model.g:1:78: NEWLINE
                {
                mNEWLINE(); 


                }
                break;
            case 13 :
                // com/googlecode/rockit/parser/Model.g:1:86: WS
                {
                mWS(); 


                }
                break;
            case 14 :
                // com/googlecode/rockit/parser/Model.g:1:89: STAR
                {
                mSTAR(); 


                }
                break;
            case 15 :
                // com/googlecode/rockit/parser/Model.g:1:94: HASH
                {
                mHASH(); 


                }
                break;
            case 16 :
                // com/googlecode/rockit/parser/Model.g:1:99: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 17 :
                // com/googlecode/rockit/parser/Model.g:1:105: NOT
                {
                mNOT(); 


                }
                break;
            case 18 :
                // com/googlecode/rockit/parser/Model.g:1:109: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 19 :
                // com/googlecode/rockit/parser/Model.g:1:115: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 20 :
                // com/googlecode/rockit/parser/Model.g:1:126: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;

        }

    }


 

}