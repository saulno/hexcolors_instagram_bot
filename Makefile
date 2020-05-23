clean_4j:
	cd instagram4j && mvn clean

clean_images:
	rm ./images/*.png

clean_class:
	rm *.class

clean_jar:
	rm instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar

clean_all: clean_jar clean_class clean_images clean_4j

jar:
	cd instagram4j && mvn clean install
	mv instagram4j/target/instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar ./instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar

compile:
	javac -cp '.:instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar' Main.java

rebuild: clean_4j clean_class clean_jar jar compile

build: jar compile clean_4j