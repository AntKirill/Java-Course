javac -d production/Java-Course/ -cp artifacts/HelloUDPTest.jar:lib/*:./ src/ru/ifmo/ctddev/antonov/hello/*.java java/info/kgeorgiy/java/advanced/hello/*.java;

java -cp artifacts/HelloUDPTest.jar:lib/*:./production/Java-Course/ info.kgeorgiy.java.advanced.hello.Tester server ru.ifmo.ctddev.antonov.hello.HelloUDPServer
