JFire
=====

This is a port of the great [python fire](https://github.com/google/python-fire) for Java.
It's nowhere near as complete as the python version, but I really liked the idea of it, so this small port is for my Java projects where I need something similar.


## Usage

So, we have a Java class we want to call from the command line:
```java
package org.github.examples;

public class CommandLineApp {

    public void hello(boolean print, String who) {
        if(print) {
            System.out.println("Hello " + who);
        }
    }

}
```

In order to call it, we need to make a main method, passing the object to be called to JFire
```java
package org.github.examples;

import org.github.JFire;

public class Main {
    public static void main(String... args) {
        JFire.fire(new CommandLineApp(), args);
    }
}
```
then, after compilation, on the command line:
```shell
java -classpath <jar_files> org.github.examples.Main hello --print=true "--who=there gorgeous!"
```

Then the output will be:
```shell
Hello there gorgeous!
```


## Boring, Essential Stuff

###Things That Are in Python Fire, but Not Yet Supported in JFire
#### Non Flag Based Arguments
If you're used to using Fire in python, JFire only supports ```--parameter=value``` approach at this point in time.
If I can be bothered, I will add ordered parameter values at some point. Feel free to send a pull request

#### Python Fire flags
```-- --help``` and ```-- --interactive``` are also not yet supported



### Compiler Requirements 
This lib only works if you compile with extra parameter info in the class you're calling (so you can get the name of the parameter).
This is disabled by default in javac.
You can turn it on in gradle by:
```groovy
tasks.withType(JavaCompile) {
    options.compilerArgs << '-parameters'
}
```

You can also pass -parameters to javac when compiling to get the same effect.

_Word of warning though: this may introduce security concerns into your code (i.e. being able to see that the parameter name is "password")_

###Type Conversion
JFire will automatically convert parameters from ```String``` to ```boolean, Boolean, int, Integer, long, Long, double, Double, String```. 
If you want to convert any other objects you need to pass addtional converters in, like so:

```java
package org.github.examples;

import org.github.JFire;

import java.util.Date;
import java.util.HashMap;

public class Main {
    public static void main(String... args) {
        JFire.fire(new CommandLineApp(), additionalConverters(), args);
    }

    private static Map<Class<?>, TypeConverter> additionalConverters() {
        Map<Class<?>, TypeConverter> reply = new HashMap<>();
        reply.put(Date.class, dateConverter());
        return reply;
    }
}
```

Again, if you feel strongly that there should be more base converters in there, send me a pull request.


###License
Since Python fire is Licensed under the Apache 2.0 License, this library is as well
