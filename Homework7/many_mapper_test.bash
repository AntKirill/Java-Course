javac -d production/java-advanced-2017/ src/ru/ifmo/ctddev/antonov/concurrent/*.java src/info/kgeorgiy/java/advanced/mapper/*.java src/info/kgeorgiy/java/advanced/concurrent/*.java;

for i in 0 1 2 3 4 5
do
	java -cp artifacts/*:lib/*:./production/java-advanced-2017/ info.kgeorgiy.java.advanced.mapper.Tester list ru.ifmo.ctddev.antonov.concurrent.ParallelMapperImpl,ru.ifmo.ctddev.antonov.concurrent.IterativeParallelism
	if [ $? -eq 1 ] 
	then
		echo ""
		echo "You have passed $i times"
		break
	fi
done

if [ $? -eq 1 ]
then
	echo "You have passed all"
fi 
