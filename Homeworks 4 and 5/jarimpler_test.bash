javac -d production/java-advanced-2017/ src/ru/ifmo/ctddev/antonov/implementor/*.java src/info/kgeorgiy/java/advanced/implementor/*.java

java -cp artifacts/*:lib/*:./production/java-advanced-2017/ info.kgeorgiy.java.advanced.implementor.Tester jar-class ru.ifmo.ctddev.antonov.implementor.Implementor
