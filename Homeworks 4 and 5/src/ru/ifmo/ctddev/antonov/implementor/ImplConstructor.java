package ru.ifmo.ctddev.antonov.implementor;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

/**
 * Class that contains methods for performing operations that {@link Implementor} invokes.
 */
public class ImplConstructor extends Implementable {


    /**
     * Perform generation of constructors realisation
     *
     * @param retType constructor that we want to implement
     */
    @Override
    protected void setRealisation(Class<?> retType) {
        s.append(" {\nsuper(");
        for (Integer i = 0; i < amountOfVariables; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(" ").append(Constants.NAME_OF_ARG).append(i.toString());
        }
        s.append(");\n}");

    }

    /**
     * Perform generation of every constructor.
     * </p>
     *
     * This method invokes by {@link ru.ifmo.ctddev.antonov.implementor.Implementor#implement(Class, Path)}
     *
     * @param c constructor that we want to implement
     * @param className name of class for implementing
     */
    public ImplConstructor(Constructor c, String className) {
        super.setAnnotation(c.getAnnotations());
        super.setModifiers(c.getModifiers());
        super.setName(className);
        super.setParams(c.getParameterTypes());
        super.setExceptions(c.getExceptionTypes());
        setRealisation(null);
    }
}
