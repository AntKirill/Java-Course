package ru.ifmo.ctddev.antonov.implementor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * Abstract class that contains methods and fields that {@link ImplMethod} and {@link ImplConstructor} classes use.
 * </p>
 * See {@link ru.ifmo.ctddev.antonov.implementor.ImplMethod#setRealisation(Class)} and {@link ru.ifmo.ctddev.antonov.implementor.ImplConstructor#setRealisation(Class)}
 */
abstract public class Implementable {
    /**
     * {@link java.lang.StringBuilder} that contains method or constructor to implement.
     */
    protected StringBuilder s = new StringBuilder();
    /**
     * Amount of arguments in constructor or method
     */
    protected Integer amountOfVariables = 0;

    /**
     * For taking out implemented method or constructor
     * @return generated method or constructor
     */
    public String getToPrint() {
        return s.toString();
    }

    /**
     * Add modifiers to {@link Implementable#s}.
     * </p>
     *
     * @param m modifiers of method or constructor
     */

    protected void setModifiers(int m) {
        if (Modifier.isPrivate(m)) {
            s.append("private ");
        } else if (Modifier.isProtected(m)) {
            s.append("protected ");
        } else if (Modifier.isPublic(m)) {
            s.append("public ");
        }
    }

    /**
     * Add Annotation to {@link Implementable#s}.
     * </p>
     * @param annotations - all annotations for method or constructor
     */
    protected void setAnnotation(Annotation[] annotations) {
        for (Annotation a: annotations) {
            s.append("\n");
            s.append(a.toString());
        }
        s.append("\n");
    }

    /**
     * Add type that method returns to to {@link Implementable#s}.
     * </p>
     *
     * @param returnType type that method returns.
     */
    protected void setReturnType(Class<?> returnType) {
        s.append(returnType.getCanonicalName()).append(" ");
    }

    /**
     * Add name.
     * @param name
     */
    protected void setName(String name) {
        s.append(name).append(" (");
    }

    /**
     * Add parameters of method or constructor to {@link Implementable#s}.
     * </p>
     *
     * @param params
     */
    protected void setParams(Class<?>[] params) {
        boolean first = true;
        for (Class<?> p : params) {
            if (amountOfVariables != 0) {
                s = s.append(", ");
            }
            s.append(p.getTypeName()).append(" ").append(Constants.NAME_OF_ARG).append(amountOfVariables.toString());
            ++amountOfVariables;
        }
        s.append(")");
    }

    /**
     * Add exceptions to constructor or method to {@link Implementable#s}.
     * </p>
     * @param exceptions thar constructor or method witch we are generating may throw.
     */
    protected void setExceptions(Class<?>[] exceptions) {
        if (exceptions.length > 0) {
            s.append(" throws ");
            boolean first = true;
            for (Class<?> e : exceptions) {
                if (!first) {
                    s.append(", ");
                }
                first = false;
                s.append(e.getCanonicalName());
            }
        }
    }

    /**
     * Realistion of constructors and methods is defferent so method is implemented for constructor and method separately.
     * See {@link ru.ifmo.ctddev.antonov.implementor.ImplMethod#setRealisation(Class)} and {@link ru.ifmo.ctddev.antonov.implementor.ImplConstructor#setRealisation(Class)}
     * </p>
     * @param retType type that method return, null for constructors.
     */
    abstract protected void setRealisation(Class<?> retType);

}
