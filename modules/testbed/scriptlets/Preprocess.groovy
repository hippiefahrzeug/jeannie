import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.processors.DefaultPreprocessor;

public class Preprocessor extends DefaultPreprocessor implements Preprocessor {
    public String getJavaClassName() {
        return context.current.name + "Entity";
    }
}
