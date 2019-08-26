import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Nathaniel Vanderpuye
 *
 **/

public class Generate extends AbstractGenerate {

    boolean flag = false;

    /** Throws a compilationerror when called on an instance of generate  */



    public void reportError(Token tokenRead, String explanatoryMessage) throws CompilationException {
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("The token symbol received was erroneous: " + tokenRead.text + " on line number " + tokenRead.lineNumber);
            System.out.println("One of the following symbol(s) was expected by the compiler: " + explanatoryMessage);
            System.out.println("-------------------------------------------------------------------------------------------");
            CompilationException cause = new CompilationException(tokenRead.text, tokenRead.lineNumber);
            throw cause;
    }
}