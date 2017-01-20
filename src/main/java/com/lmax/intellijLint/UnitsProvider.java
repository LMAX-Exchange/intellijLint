package com.lmax.intellijLint;

import com.intellij.codeInspection.InspectionToolProvider;

public class UnitsProvider implements InspectionToolProvider {
    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{
                UnitsInspection.class
        };
    }
}
