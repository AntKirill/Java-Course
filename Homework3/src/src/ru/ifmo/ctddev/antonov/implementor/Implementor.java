package ru.ifmo.ctddev.antonov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Implementor implements Impler {

    private ArrayList<Method> getAllMethods(Class<?> token) {
        ArrayList<Method> all = new ArrayList<>();
        Collections.addAll(all, token.getMethods());
        for (Class<?> cl = token; cl != null; cl = cl.getSuperclass()) {
            for (Method m: cl.getDeclaredMethods()) {
                if (!Modifier.isPrivate(m.getModifiers()) && Modifier.isAbstract(m.getModifiers())) {
                    all.add(m);
                }
            }
        }
        return all;
    }

    private String className;
    private StringBuilder allcode;

    private boolean appendConstructors(Constructor[] constrs) {
        boolean onlyPrivate = true;
        for (Constructor c: constrs) {
            Implementable k = new ImplConstructor(c, className);
            allcode.append("\n\n").append(k.getToPrint());
            if (!Modifier.isPrivate(c.getModifiers())) {
                onlyPrivate = false;
            }
        }
        return !onlyPrivate;
    }

    private void appendMethods(ArrayList<Method> allMethods) {
        Map<Integer, Method> methodsToImpl = new HashMap<>();
        for (Method method: allMethods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                methodsToImpl.put(getHash(method),method);
            }
        }
        for (Method method: methodsToImpl.values()) {
            Implementable k = new ImplMethod(method);
            allcode.append("\n\n").append(k.getToPrint());
        }
    }

    private Integer getHash(Method method) {
        return method.getName().hashCode() + Arrays.hashCode(method.getParameterTypes());
    }

    private void makeCode(Class<?> token) throws ImplerException {
        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Unable to implement");
        } else if (token.isPrimitive()) {
            throw new ImplerException("Class is primitive");
        } else if (token == Enum.class) {
            throw new ImplerException("Enum can not be extended");
        }
        allcode = new StringBuilder();
        className = token.getSimpleName().concat(Constants.SUFFIX);
        try {
            allcode.append("package ".concat(token.getPackage().getName()).concat(";\n\n"));
        } catch (NullPointerException e) {
            //ignore
        }
        allcode.append("public class ".concat(className).concat(" "));
        if (token.isInterface()) {
            allcode.append("implements ");
        } else {
            allcode.append("extends ");
        }

        allcode.append(token.getCanonicalName()).append(" {\n");

        if (!appendConstructors(token.getDeclaredConstructors()) && !token.isInterface()) {
            throw new ImplerException("class has no constructors");
        }
        appendMethods(getAllMethods(token));

        allcode.append("\n}");
    }

    /**
     * Produces code implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <tt>root</tt> directory and have correct file name. For example, the implementation of the
     * interface {@link List} should go to <tt>$root/java/util/ListImpl.java</tt>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException when implementation cannot be
     *                         generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        makeCode(token);

        Path genFile = Paths.get(root.toString(), token.getCanonicalName().replace(".", File.separator).concat(Constants.SUFFIX).concat(".java"));
        try {
            Files.createDirectories(genFile.getParent());
            Files.deleteIfExists(genFile);
            Path finalPath = Files.createFile(genFile);
            try (PrintWriter pw = new PrintWriter(finalPath.toString(), Constants.CHARSET)) {
                pw.print(allcode);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Specify the class and the root");
            return;
        }
        try {
            Class<?> cl = ClassLoader.getSystemClassLoader().loadClass(args[0]);

            try {
                new Implementor().implement(cl, Paths.get(args[1]));
            } catch (ImplerException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

//    /**
//     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>token</tt>.
//     * <p>
//     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
//     * added.
//     *
//     * @param token   type token to create implementation for.
//     * @param jarFile target <tt>.jar</tt> file.
//     * @throws ImplerException when implementation cannot be generated.
//     */
//    @Override
//    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
//
//    }
}
