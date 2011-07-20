import com.sb.utils.GenieInterface;

public class Scriptlet implements GenieInterface {
	
      public void runit() {
      	  println "running :-)"
      }

      public int getResult() {
      	  return 42
      }
}
