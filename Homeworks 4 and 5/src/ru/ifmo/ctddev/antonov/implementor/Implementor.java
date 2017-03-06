package ru.ifmo.ctddev.antonov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;


public class Implementor implements JarImpler {

    /**
     * Findes all methods that needed to be implemented.
     * <p>
     *
     * @param token is a type token of class that we want to implement
     * @return ArrayList of Method witch contains all methods to implement
     */
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

    /**
     * name of class that we want to implement
     */
    private String className;

    /**
     * all methods, consturctors
     */
    private StringBuilder allcode;

    /**
     * Appends implemented constructors to <tt>allcode</tt>
     * <p>
     *
     * @param constrs all constructors to implement
     * @return false if there are only private constructors, true otherwise
     */
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

    /**
     * Appedns implemented methods to <tt>allcode</tt>.
     *
     * <p>
     *
     * @param allMethods all methods to implement
     *
     */
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

    /**
     * Add all code from class specified by provided <tt>token</tt> to StringBuilder <tt>allcode</tt>
     * <p>
     *
     * @param method method to hash
     *
     */
    private Integer getHash(Method method) {
        return method.getName().hashCode() + Arrays.hashCode(method.getParameterTypes());
    }

    /**
     * Add all code from class specified by provided <tt>token</tt> to StringBuilder <tt>allcode</tt>.
     *
     * It is checking weather class is primitive, or enum {@link java.lang.Enum}.
     * <p>
     *
     * @param token type token to create implementation for.
     * @throws ImplerException when implementation cannot be
     *                         generated.
     */
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
     * Path defines an Absolute Path for generated file
     */
    Path finalPath;
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
            finalPath = Files.createFile(genFile);
            try (PrintWriter pw = new PrintWriter(finalPath.toString(), Constants.CHARSET)) {
                pw.print(allcode);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    /**
     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>args</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param args three strings first might be -jar, second type topen of class, third is destination of jar file
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Run with -jar option, specify the class and the root");
            return;
        }
        if (!args[0].equals("-jar")) {
            System.out.println("Run with -jar option");
        }
        try {
            Class<?> cl = ClassLoader.getSystemClassLoader().loadClass(args[1]);

            try {
                new Implementor().implementJar(cl, Paths.get(args[2]));
            } catch (ImplerException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param token   type token to create implementation for.
     * @param jarFile target <tt>.jar</tt> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        implement(token, jarFile);
        if (finalPath == null) {
            throw new ImplerException("can not genrate code");
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if ((compiler.run(null, null, null, "-encoding", "Cp866", finalPath.toString()))!=0) {
            throw new ImplerException("Can not compile generated file");
        }
        String nameOfJava = finalPath.toString();
        String nameOfClass = nameOfJava.substring(0, nameOfJava.length() - 4).concat("class");
        String nameOfJar = jarFile.toString().concat(File.separator).concat(token.getSimpleName()).concat(Constants.SUFFIX).concat(".jar");
        File fileWithClass = new File (nameOfClass);

        try (InputStream genClass = new FileInputStream(fileWithClass);
             JarOutputStream genJar = new JarOutputStream(new FileOutputStream(nameOfJar))) {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            JarEntry jarEntry = new JarEntry(nameOfClass);
            jarEntry.setTime(System.currentTimeMillis());
            genJar.putNextEntry(jarEntry);
            byte [] buf = new byte[1024];
            int cnt = 0;
            while ((cnt = genClass.read(buf)) >= 0) {
                genJar.write(buf, 0, cnt);
            }
            genJar.closeEntry();
        } catch (IOException e) {
            throw new ImplerException("Can not create jar\n".concat(e.getMessage()));
        }
    }
}
