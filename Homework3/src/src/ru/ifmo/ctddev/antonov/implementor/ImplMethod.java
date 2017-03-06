package ru.ifmo.ctddev.antonov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;

import java.lang.reflect.Method;

/**
 * Created by kirill on 2/26/17.
 */

public class ImplMethod extends Implementable {

    @Override
    protected void setRealisation(Class<?> retType) {
        s.append(" {\nreturn");
        if (retType.equals(void.class)) {
            s.append(";");
        } else if (retType.isPrimitive()) {
            s.append(retType.equals(boolean.class) ? " false;" : " 0;");
        } else {
            s.append(" null;");
        }
        s.append("\n}");
    }

    public ImplMethod(Method m) {
        super.setAnnotation(m.getAnnotations());
        super.setModifiers(m.getModifiers());
        super.setReturnType(m.getReturnType());
        super.setName(m.getName());
        super.setParams(m.getParameterTypes());
        super.setExceptions(m.getExceptionTypes());
        setRealisation(m.getReturnType());
    }
}
