import com.sb.jeannie.interfaces.Postprocessor
import com.sb.jeannie.processors.DefaultPostprocessor;

public class Postprocessor extends DefaultPostprocessor implements Postprocessor {
    String getOutputdir() {
        return "src/outputdir"
    }

    String getOutputname() {
        return getContext().currentfile.name + "_generated"
    }

    boolean getDontgenerate() {
        return false;
    }
}
