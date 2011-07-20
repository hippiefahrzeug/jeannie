import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.processors.DefaultPreprocessor;


public class Preprocessor extends DefaultPreprocessor implements Preprocessor {
    public String getResulty() {
        return getContext().get("current").name;
    }

    public String getJavaClassName() {
        return getContext().get("current").name + "Entity";
    }
}

