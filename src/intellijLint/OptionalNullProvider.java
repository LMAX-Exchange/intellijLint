package intellijLint;

import com.intellij.codeInspection.InspectionToolProvider;

public class OptionalNullProvider implements InspectionToolProvider {
    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{
                OptionalNullInspection.class
        };
    }
}
