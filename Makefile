cleanmvn:
	cd instagram4j && mvn clean

clean:
	rm *.class
	rm instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar

jar:
	cd instagram4j && mvn clean install
	mv instagram4j/target/instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar ./instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar

compile:
	javac -cp '.:instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar' Main.java

all: clean jar compile cleanmvn