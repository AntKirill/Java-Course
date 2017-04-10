javac -d production/java-advanced-2017/ src/ru/ifmo/ctddev/antonov/concurrent/*.java src/info/kgeorgiy/java/advanced/mapper/*.java src/info/kgeorgiy/java/advanced/concurrent/*.java;

java -cp artifacts/*:lib/*:./production/java-advanced-2017/ info.kgeorgiy.java.advanced.mapper.Tester list ru.ifmo.ctddev.antonov.concurrent.ParallelMapperImpl,ru.ifmo.ctddev.antonov.concurrent.IterativeParallelism
