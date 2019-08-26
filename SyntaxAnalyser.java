/**
 * @Author: Nathaniel Vanderpuye
 *
 **/

import java.io.* ;
import java.util.*;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser{

    String fileName;
    CompilationException CompExcep;
    List <Integer> errors = new ArrayList<Integer>();


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

    /** Checks the symbol to accept for its validity and throws an error if it's incorrect */

    public void acceptTerminal(int symbol)throws IOException, CompilationException{

        if (nextToken.symbol == symbol){
            nextToken = lex.getNextToken();
        }
        else{
            System.out.println("\nThe Accept check recieved the following erroneous symbol: " + Token.getName(nextToken.symbol));
            myGenerate.flag = true;
            Error();
        }
    }

    /** Throws an error if a symbol that wasn't expected is encountered */

    public void Error() throws IOException, CompilationException{
        if(myGenerate.flag == true) {
            String errorMessage = "";

            for (int x = 0; x < errors.size(); x++) {
                errorMessage = errorMessage + "[" + Token.getName(errors.get(x)) + "]";
            }
            myGenerate.reportError(nextToken, errorMessage);
        }
    }

    /** Starts processing for the first top level token */

    public void _statementPart_()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("StatementPart");

            switch (nextToken.symbol) {
                case Token.beginSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.beginSymbol);
                    StatementList();
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.endSymbol);
                    myGenerate.finishNonterminal("StatementPart");
                    myGenerate.insertTerminal(nextToken);
                    break;
                default:
                    errors.add(Token.beginSymbol);
                    errors.add(Token.endSymbol);
                    myGenerate.flag = true;
            }
            Error();
            myGenerate.finishNonterminal("StatementPart");
        }
        catch(CompilationException e){
            throw e;
        }
    }

    /** Processes a production rule request for a statementlist in the given grammar */

    public void StatementList()throws IOException, CompilationException{
        try{
            myGenerate.commenceNonterminal("StatementList");
            Statement();

            switch (nextToken.symbol){
                case Token.semicolonSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.semicolonSymbol);
                    StatementList();
                    break;
                case Token.endSymbol:
                    break;
                default:
                    errors.add(Token.semicolonSymbol);
                    errors.add(Token.endSymbol);
                    myGenerate.flag = true;
            }
            Error();
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

            switch (nextToken.symbol){
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
                case Token.doSymbol:
                    UntilStatement();
                    break;
                case Token.forSymbol:
                    ForStatement();
                    break;
                case Token.semicolonSymbol:
                    break;
                default:
                    errors.add(Token.identifier);
                    errors.add(Token.ifSymbol);
                    errors.add(Token.whileSymbol);
                    errors.add(Token.callSymbol);
                    errors.add(Token.doSymbol);
                    errors.add(Token.forSymbol);
                    errors.add(Token.semicolonSymbol);
                    myGenerate.flag = true;
            }
            Error();
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
                case Token.identifier:
                    Expression();
                    break;
                case Token.numberConstant:
                    Expression();
                    type = Variable.Type.NUMBER;
                    var = new Variable(identifier.text, type);
                    myGenerate.addVariable(var);
                    break;
                case Token.leftParenthesis:
                    Expression();
                    break;
                case Token.stringConstant:
                    myGenerate.insertTerminal(nextToken);
                    type = Variable.Type.STRING;
                    var = new Variable(identifier.text, type);
                    myGenerate.addVariable(var);
                    acceptTerminal(Token.stringConstant);
                    break;
                case Token.endSymbol:
                    break;
                default:
                    errors.add(Token.identifier);
                    errors.add(Token.numberConstant);
                    errors.add(Token.leftParenthesis);
                    errors.add(Token.stringConstant);
                    errors.add(Token.endSymbol);
                    myGenerate.flag = true;
            }
            Error();
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
                default:
                    errors.add(Token.endSymbol);
                    errors.add(Token.elseSymbol);
                    errors.add(Token.ifSymbol);
                    myGenerate.flag = true;
            }
            Error();
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

            Error();
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

            Error();
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

            Error();
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

            Error();
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

            switch (nextToken.symbol){
                case Token.identifier:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.identifier);
                    break;
                case Token.commaSymbol:
                    break;
                case Token.rightParenthesis:
                    break;
                default:
                    errors.add(Token.identifier);
                    errors.add(Token.commaSymbol);
                    errors.add(Token.rightParenthesis);
                    myGenerate.flag = true;
            }
            switch (nextToken.symbol){
                case Token.commaSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.commaSymbol);
                    ArgumentList();
                    break;
                case Token.identifier:
                    break;
                case Token.rightParenthesis:
                    break;
                default:
                    errors.add(Token.commaSymbol);
                    errors.add(Token.identifier);
                    errors.add(Token.rightParenthesis);
                    myGenerate.flag = true;
            }
            Error();
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
                default:
                    errors.add(Token.identifier);
                    errors.add(Token.numberConstant);
                    errors.add(Token.stringConstant);
                    myGenerate.flag = true;
            }
            Error();
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
                default:
                    errors.add(Token.greaterThanSymbol);
                    errors.add(Token.greaterEqualSymbol);
                    errors.add(Token.equalSymbol);
                    errors.add(Token.notEqualSymbol);
                    errors.add(Token.lessThanSymbol);
                    errors.add(Token.lessEqualSymbol);
                    myGenerate.flag = true;
            }
            Error();
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

//            switch (nextToken.symbol) {
//                case Token.semicolonSymbol:
//                    break;
//                case Token.rightParenthesis:
//                    break;
//                case Token.endSymbol:
//                    break;
//                default:
//                    errors.add(Token.plusSymbol);
//                    errors.add(Token.minusSymbol);
//                    errors.add(Token.semicolonSymbol);
//                    errors.add(Token.rightParenthesis);
//                    errors.add(Token.endSymbol);
//                    myGenerate.flag = true;
//
//            }
            while (nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
                switch (nextToken.symbol) {
                    case Token.plusSymbol:
                        myGenerate.insertTerminal(nextToken);
                        acceptTerminal(Token.plusSymbol);
                        Expression();
                        break;
                    case Token.minusSymbol:
                        myGenerate.insertTerminal(nextToken);
                        acceptTerminal(Token.minusSymbol);
                        Expression();
                        break;
                    default:
                        errors.add(Token.plusSymbol);
                        errors.add(Token.minusSymbol);
                        errors.add(Token.semicolonSymbol);
                        errors.add(Token.rightParenthesis);
                        errors.add(Token.endSymbol);
                        myGenerate.flag = true;
                }
            }
            Error();
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
                case Token.timesSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.timesSymbol);
                    Term();
                    break;
                case Token.divideSymbol:
                    myGenerate.insertTerminal(nextToken);
                    acceptTerminal(Token.divideSymbol);
                    Term();
                    break;
                case Token.semicolonSymbol:
                    break;
                case Token.rightParenthesis:
                    break;
                case Token.minusSymbol:
                    break;
                case Token.plusSymbol:
                    break;
                case Token.endSymbol:
                    break;
                default:
                    errors.add(Token.timesSymbol);
                    errors.add(Token.divideSymbol);
                    errors.add(Token.semicolonSymbol);
                    errors.add(Token.rightParenthesis);
                    errors.add(Token.minusSymbol);
                    errors.add(Token.plusSymbol);
                    errors.add(Token.endSymbol);
                    myGenerate.flag = true;
            }
            Error();
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
                case Token.endSymbol:
                    break;
                default:
                    errors.add(Token.identifier);
                    errors.add(Token.numberConstant);
                    errors.add(Token.leftParenthesis);
                    errors.add(Token.endSymbol);
                    myGenerate.flag = true;
            }
            Error();
            myGenerate.finishNonterminal("Factor");
        }
        catch(CompilationException e){
            throw e;
        }
    }
}