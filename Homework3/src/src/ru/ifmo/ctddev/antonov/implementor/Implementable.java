package ru.ifmo.ctddev.antonov.implementor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * Created by kirill on 2/26/17.
 */
abstract public class Implementable {
    protected StringBuilder s = new StringBuilder();
    protected Integer amountOfVariables = 0;
    //protected final String nameOfArg = "arg";

    public String getToPrint() {
        return s.toString();
    }

    protected void setModifiers(int m) {
        if (Modifier.isPrivate(m)) {
            s.append("private ");
        } else if (Modifier.isProtected(m)) {
            s.append("protected ");
        } else if (Modifier.isPublic(m)) {
            s.append("public ");
        }
    }

    protected void setAnnotation(Annotation[] annotations) {
        for (Annotation a: annotations) {
            s.append("\n");
            s.append(a.toString());
        }
        s.append("\n");
    }

    protected void setReturnType(Class<?> returnType) {
        s.append(returnType.getCanonicalName()).append(" ");
    }

    protected void setName(String name) {
        s.append(name).append(" (");
    }

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

    abstract protected void setRealisation(Class<?> retType);

}
