package org.github;

import org.github.converter.TypeConverter;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InternalFireTest {

    private final Map<Class<?>, TypeConverter> converters = new HashMap<>();

    @Test
    void canFireSimpleMethod() throws Exception {
        final ToRun toRun = new ToRun();
        new InternalFire(converters).fire(toRun, "run");
        assertThat(toRun.ran, is(true));
    }

    @Test
    void willFailWhenNEmptyMethodPassed() throws Exception {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            final ToRun toRun = new ToRun();
            new InternalFire(converters).fire(toRun, "");
        });
        assertThat(exception.getMessage(), is("Method \"\" is not found. \nPublic methods are: \"run, anotherRun\""));
    }

    @Test
    void willFailWhenNoMethodPassed() throws Exception {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            final ToRun toRun = new ToRun();
            new InternalFire(converters).fire(toRun);
        });
        assertThat(exception.getMessage(), is("Please pass the public method name that you would like to call"));
    }

    @Test
    void cannotCallPrivateMethod() throws Exception {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            final ToRunPrivate toRun = new ToRunPrivate();
            final InternalFire unit = new InternalFire(converters);
            unit.fire(toRun, "run", "--shouldI=yes");
        });
        assertThat(exception.getMessage(), is("There are no public methods in the class: org.github.InternalFireTest.ToRunPrivate"));
    }

    @Test
    void cannotFindMethod() {
        final ToRun toRun = new ToRun();
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> new InternalFire(converters).fire(toRun, "notFound"));
        assertThat(result.getMessage(), is(
                "Method \"notFound\" is not found. " +
                        "\nPublic methods are: \"run, anotherRun\""));
    }

    @Test
    void canFireStatic() throws Exception {
        final RunMeStatic toRun = new RunMeStatic();
        final InternalFire unit = new InternalFire(converters);
        assertThat(toRun.ran, is(false));
        unit.fire(toRun, "run", "--shouldI=yes");
        assertThat(toRun.ran, is(true));
    }

    @Test
    void canFireMethodWithOneParameter() throws Exception {
        final ToRun toRun = new ToRun();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(toRun, "anotherRun", "--shouldI=yes");
        assertThat(toRun.ran, is(true));
    }

    @Test
    void canFireMethodWithTwoParameters() throws Exception {
        final RunMe runMe = new RunMe();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "two", "--shouldI=yes", "--really=maybe");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void canFireMethodWithTwoParametersReverseOrder() throws Exception {
        final RunMe runMe = new RunMe();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "two", "--really=maybe", "--shouldI=yes");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void canHandleOverloadedMethods() throws Exception {
        final RunMeAgain runMe = new RunMeAgain();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "methodToCall", "--really=maybe", "--shouldI=yes", "--couldI=perhaps");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void whenMethodHasAReturnTypeSysoutWithToString() throws Exception {
        final String[] lastPrintedLine = {""};

        class  CustomPrintStream extends PrintStream {
            public CustomPrintStream() { super(nullOutputStream()); }
            @Override public void println(String line) {
                lastPrintedLine[0] =line;
            }
        }

        System.setOut(new CustomPrintStream());

        final RunMeWithReturn runMe = new RunMeWithReturn();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "methodToCall", "--shouldI=yes");
        assertThat(lastPrintedLine[0], is("I am a sysout-ed value from the returned object"));
    }

    @Test
    void canHandleBooleanTrueConversion() throws Exception {
        final BoolThing runMe = new BoolThing();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "callWithBoolean", "--aValue=true");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void canHandleBooleanTrueConversion2() throws Exception {
        final BoolThing runMe = new BoolThing();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "callWithBoolean", "--aValue=TRUE");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void canHandleBooleanFalseConversion() throws Exception {
        final BoolThing2 runMe = new BoolThing2();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "callWithBoolean", "--aValue=false");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void canHandleBooleanFalseConversion2() throws Exception {
        final BoolThing2 runMe = new BoolThing2();
        final InternalFire unit = new InternalFire(converters);
        unit.fire(runMe, "callWithBoolean", "--aValue=FALSE");
        assertThat(runMe.ran, is(true));
    }

    @Test
    void cannotHandleClassWhichCantBeConverted() throws Exception {
        final CalledWithAnotherClass runMe = new CalledWithAnotherClass();
        final InternalFire unit = new InternalFire(converters);

        Exception reply = assertThrows(IllegalArgumentException.class, () -> {
            unit.fire(runMe, "myMethod", "--otherClass=myClass");
        });
        assertThat(reply.getMessage(), is("Unable to convert org.github.InternalFireTest$ToRun for parameter: \"otherClass\". Please register converters for non basic objects"));
    }


    class CalledWithAnotherClass {
        public void myMethod(ToRun otherClass) {
        }
    }

    class ToRun {
        boolean ran = false;

        public void run() {
            ran = true;
        }

        public void anotherRun(String shouldI) {
            if ("yes".equals(shouldI)) {
                ran = true;
            }
        }
    }

    class ToRunPrivate {
        boolean ran = false;

        private void run(String shouldI) {
            if ("yes".equals(shouldI)) {
                ran = true;
            }
        }
    }
    class RunMe {
        boolean ran = false;

        public void two(String shouldI, String really) {
            if ("yes".equals(shouldI) && "maybe".equals(really)) {
                ran = true;
            }
        }
    }
    static class RunMeStatic {
        static boolean ran = false;

        public static void run(String shouldI) {
            if ("yes".equals(shouldI)) {
                ran = true;
            }
        }
    }
    class RunMeAgain {
        boolean ran = false;

        public void methodToCall(String shouldI, String really) {
        }

        public void methodToCall(String shouldI, String really, String couldI) {
            if ("yes".equals(shouldI) && "maybe".equals(really) && "perhaps".equals(couldI)) {
                ran = true;
            }
        }
    }
    class RunMeWithReturn {
        public ReturnedValue methodToCall(String shouldI) {
            if ("yes".equals(shouldI)) {
                return new ReturnedValue();
            }
            throw new RuntimeException();
        }
    }

    class ReturnedValue {
        @Override
        public String toString() {
            return "I am a sysout-ed value from the returned object";
        }
    }

    class BoolThing {
        boolean ran = false;
        public void callWithBoolean(boolean aValue) {
            if (aValue) {
                ran = true;
            }
        }
    }
    class BoolThing2 {
        boolean ran = false;
        public void callWithBoolean(boolean aValue) {
            if (!aValue) {
                ran = true;
            }
        }
    }
}
