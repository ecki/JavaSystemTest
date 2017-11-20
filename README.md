# JavaSystemTest

Test various OS/JVM combinations (System Properties, Environment and JMX)

See the [doc/samples/](/ecki/JavaSystemTest/tree/master/doc/sample) directory for a number of generated output files on various operating systems
with different JVM providers and versions.

The code is released under the GNU General Public License 2.0 (GPL).

## Usage

    > cd JavaSystemTest/src/main/java
    > javac net/eckenfels/test/javasystemtest/Main.java
    > java -cp . net.eckenfels.test.javasystemtest.Main > output.txt

# Other Tests

* net.eckenfels.test.javasystemtest.StackDepth - tests maximum stack depth with and without doPriviledged