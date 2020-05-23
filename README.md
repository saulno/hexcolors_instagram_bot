# hex_colors_instagram_bot

Generates solid hex color images and uploads it to @hexcode.colors on Instagram

## Usage
A Makefile is included to create the jar file. Run ```make build``` and procede to run the programm. Maven is required. 
- ```make rebuild``` -> cleans existing files and build project
- ```make jar``` -> builds jar
- ```make compile``` -> compiles Main.java

To run use ```java -cp '.:instagram4j-1.15-SNAPSHOT-jar-with-dependencies.jar' Main.java user psswd start numberOfPosts```.
- Start and end are integers [0 - 16,777,216) representing the total ammount of RGB colors

## Instagram4j

I used https://github.com/instagram4j/instagram4j instagram bot to upload images to account.

It was neccesary to modify maven behaviour, so that it includes dependencies on jar. By adding:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>2.4</version>
    <configuration>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <archive>
            <manifest>
                <mainClass>com.domain.Program</mainClass>
            </manifest>
        </archive>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
