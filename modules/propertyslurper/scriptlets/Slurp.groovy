import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.processors.DefaultPreprocessor;

class Slurp extends DefaultPreprocessor {

    def upper = { it.toUpperCase() }

    def getModel() {
       def smap = []
       getContext().getCurrent().collect() { 
         smap.add([it.get(0).toUpperCase().trim(), toCamelCase(it.get(0).toLowerCase().trim()), it.get(2).trim()])
       }
       return smap
    }

    static String toCamelCase( String text, boolean capitalized = false ) {
        text = text.replaceAll( "(_)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() } )
        return capitalized ? capitalize(text) : text
    }
 
    static String toSnakeCase( String text ) {
        text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
    }

    def getDefaults() {
       def smap = []
       getContext().getCurrent().collect() { it ->
           if (it.get(1).trim() == '""') {
             smap.add([it.get(0).toUpperCase().trim(), '\\"'])
           }
           else {
             smap.add([it.get(0).toUpperCase().trim(), it.get(1).trim()])
           }
       }
       return smap
    }

    def getClassname() {
        return getContext().get("currentfile").getName().replace(".csv", "");
    }
}
