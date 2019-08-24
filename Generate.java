import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Nathaniel Vanderpuye
 *
 **/

public class Generate extends AbstractGenerate {

    /** Throws a compilationerror when called on an instance of generate  */

    @Override
    public void reportError(Token tokenRead, String explanatoryMessage) throws CompilationException {
        System.out.println("The following symbol(s) was expected: " + explanatoryMessage + " on line number " + tokenRead.lineNumber);
        System.out.println("The token symbol received was erroneous: " + tokenRead.text + " on line number " + tokenRead.lineNumber);
        CompilationException cause = new CompilationException(tokenRead.text,tokenRead.lineNumber);
        throw cause;
    }
}