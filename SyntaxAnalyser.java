/**
 * @Author: Nathaniel Vanderpuye
 *
 **/

import java.io.* ;
import java.util.*;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser{

    String fileName;
    CompilationException CompExcep;

    /**
     * Takes in a file name and creates a lexical analyser for it
     *
     * @param file the filename of the file to be analysed
     *
     */
    public SyntaxAnalyser(String file){

        fileName = file;

        try{
            lex = new LexicalAnalyser(fileName);
        }
        catch(IOException a){
            System.out.println(a);
        }
    }

    /** Starts processing for the first top level token */

    public void _statementPart_()throws IOException, CompilationException{

        try{
            myGenerate.commenceNonterminal("StatementPart");

            if(nextToken.symbol == Token.beginSymbol){
                myGenerate.insertTerminal(nextToken);
                acceptTerminal(Token.beginSymbol);
                StatementList();
            }

            else{
                Error(Token.beginSymbol);
            }
            if (nextToken.symbol == Token.endSymbol){
                myGenerate.insertTerminal(nextToken);
                acceptTerminal(Token.endSymbol);
                myGenerate.finishNonterminal("StatementPart");
                myGenerate.insertTerminal(nextToken);
            }
            else{

                Error(Token.endSymbol);
            }
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Checks the input symbol for its validity and throws an error if it's incorrect */

    public void acceptTerminal(int symbol)throws IOException, CompilationException{

        if (nextToken.symbol == symbol){
            nextToken = lex.getNextToken();
        }
        else{
            System.out.println("\nThe given symbol was not be matched with the expected symbol.");
            Error(symbol);
        }
    }

    /** Processes a production rule request for a statementlist in the given grammar */

    public void StatementList()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("StatementList");
            Statement();

           while (nextToken.symbol == Token.semicolonSymbol ){
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.semicolonSymbol);
                    StatementList();
                    break;
            }

            myGenerate.finishNonterminal("StatementList");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a statement in the given grammar */

    public void Statement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("Statement");

            switch (nextToken.symbol) {

                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));

                case Token.identifier:
                    AssignmentStatement();
                    break;
                case Token.ifSymbol:
                    IfStatement();
                    break;
                case Token.whileSymbol:
                    WhileStatement();
                    break;
                case Token.callSymbol:
                    ProcedureStatement();
                    break;
                case Token.untilSymbol:
                    UntilStatement();
                    break;
                case Token.forSymbol:
                    ForStatement();
                    break;
            }

            myGenerate.finishNonterminal("Statement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for an assignmentstatement in the given grammar */

    public void AssignmentStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("AssignmentStatement");

            Variable var;
            Token identifier;
            Variable.Type type;

            myGenerate.insertTerminal(nextToken);
            identifier = nextToken;
            acceptTerminal(Token.identifier);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.becomesSymbol);

            switch (nextToken.symbol){
                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));
                case Token.identifier:
                    Expression();
                    break;
                case Token.leftParenthesis:
                    Expression();
                    break;
                case Token.numberConstant:
                    Expression();
                    type = Variable.Type.NUMBER;
                    var = new Variable(identifier.text, type);
                    myGenerate.addVariable(var);
                    break;
                case Token.stringConstant:
                    myGenerate.insertTerminal(nextToken);
                    type = Variable.Type.STRING;
                    var = new Variable(identifier.text, type);
                    myGenerate.addVariable(var);
                    acceptTerminal(Token.stringConstant);
                    break;
            }



//            while (nextToken.symbol == Token.stringConstant){
//                myGenerate.insertTerminal(nextToken);
//                type = Variable.Type.STRING;
//                var = new Variable(identifier.text, type);
//                myGenerate.addVariable(var);
//                acceptTerminal(Token.stringConstant);
//                break;
//            }
//            while (nextToken.symbol == Token.numberConstant){
//
//            }

//            while (nextToken.symbol == Token.identifier || nextToken.symbol == Token.leftParenthesis){
//                Expression();
//                break;
//            }

            myGenerate.finishNonterminal("AssignmentStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }


    /** Processes a production rule request for an ifstatement in the given grammar */

    public void IfStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("IfStatement");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.ifSymbol);
            Condition();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.thenSymbol);
            StatementList();

            switch (nextToken.symbol){
                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));
                case Token.endSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.endSymbol);
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.ifSymbol);
                    break;
                case Token.elseSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.elseSymbol);
                    StatementList();
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.endSymbol);
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.ifSymbol);
                    break;
            }

            myGenerate.finishNonterminal("IfStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a whilestatement in the given grammar */

    public void WhileStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("WhileStatement");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.whileSymbol);
            Condition();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.loopSymbol);
            StatementList();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.endSymbol);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.loopSymbol);
            myGenerate.finishNonterminal("WhileStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a procedurestatement in the given grammar */

    public void ProcedureStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("ProcedureStatement");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.callSymbol);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.identifier);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.leftParenthesis);
            ArgumentList();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.rightParenthesis);

            myGenerate.finishNonterminal("ProcedureStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for an untilstatement in the given grammar */

    public void UntilStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("UntilStatement");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.doSymbol);
            StatementList();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.untilSymbol);
            Condition();
            myGenerate.finishNonterminal("UntilStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a forstatement in the given grammar */

    public void ForStatement()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("ForStatement");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.forSymbol);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.leftParenthesis);
            AssignmentStatement();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.semicolonSymbol);
            Condition();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.semicolonSymbol);
            AssignmentStatement();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.rightParenthesis);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.doSymbol);
            StatementList();
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.endSymbol);
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.loopSymbol);
            myGenerate.finishNonterminal("ForStatement");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for an argumentlist in the given grammar */

    public void ArgumentList()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("ArgumentList");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.identifier);

            while (nextToken.symbol == Token.commaSymbol){
                myGenerate.insertTerminal(nextToken);
                acceptTerminal(Token.commaSymbol);
                ArgumentList();
                break;
            }

            myGenerate.finishNonterminal("ArgumentList");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a condition in the given grammar */

    public void Condition()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("Condition");
            myGenerate.insertTerminal(nextToken);
            acceptTerminal(Token.identifier);
            ConditionalOperator();

            switch (nextToken.symbol){
                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));
                case Token.identifier:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.stringConstant:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.stringConstant);
                    break;
            }

            myGenerate.finishNonterminal("Condition");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a conditionaloperator in the given grammar */

    public void ConditionalOperator()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("ConditionalOperator");

            switch (nextToken.symbol){
                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));
                case Token.greaterThanSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.greaterThanSymbol);
                    break;
                case Token.greaterEqualSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.greaterEqualSymbol);
                    break;
                case Token.equalSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.equalSymbol);
                    break;
                case Token.notEqualSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.notEqualSymbol);
                    break;
                case Token.lessThanSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.lessThanSymbol);
                    break;
                case Token.lessEqualSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.lessEqualSymbol);
                    break;
            }

            myGenerate.finishNonterminal("ConditionalOperator");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for an expression in the given grammar */

    public void Expression()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("Expression");
            Term();

            while (nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
                myGenerate.insertTerminal(nextToken);
                acceptTerminal(nextToken.symbol);
                Expression();
                break;
            }

            myGenerate.finishNonterminal("Expression");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a term in the given grammar */

    public void Term()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("Term");
            Factor();

            switch (nextToken.symbol){
                default:
                    break;
                case Token.timesSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(nextToken.symbol);
                    Term();
                    break;

                case Token.divideSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(nextToken.symbol);
                    Term();
                    break;
            }

            myGenerate.finishNonterminal("Term");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a factor in the given grammar */

    public void Factor()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("Factor");

            switch (nextToken.symbol){
                default:
                    System.out.println("Received the following erroneous symbol: " + Token.getName(nextToken.symbol));
                case Token.identifier:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.leftParenthesis:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.leftParenthesis);
                    Expression();
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.rightParenthesis);
                    break;
            }

            myGenerate.finishNonterminal("Factor");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    public void Error(int expectedSymbol)throws IOException, CompilationException{
        myGenerate.reportError(nextToken,Token.getName(expectedSymbol));
    }
}