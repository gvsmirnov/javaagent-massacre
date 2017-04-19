package jug.gvsmirnov.javaagent.deps;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import javax.servlet.Servlet;
import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ByteBuddyAgent {

    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(isSubTypeOf(Servlet.class))
                .transform((builder, typeDescription, classLoader, module) ->
                    builder.method(named("service"))
                            .intercept(
                                    MethodDelegation.to(Interceptor.class)
                            )
                ).installOn(instrumentation);
    }

    public static class Interceptor {
        @RuntimeType
        public static Object intercept() {
            return "Hello, transformed world!";
        }
    }
}
