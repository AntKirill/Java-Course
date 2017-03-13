package ru.ifmo.ctddev.antonov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;

import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * Class that contains methods for performing operations that {@link Implementor} invokes.
 */

public class ImplMethod extends Implementable {

    /**
     * Perform generation of constructors realisation
     *
     * @param retType constructor that we want to implement
     */
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

    /**
     * Perform generation of every method.
     * </p>
     * This method invokes by {@link ru.ifmo.ctddev.antonov.implementor.Implementor#implement(Class, Path )}
     *
     * @param m method that we want to implement
     */
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
