package jug.gvsmirnov.javaagent.deps;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ServletAgent {

    public static void premain(String arguments, Instrumentation instrumentation) throws ClassNotFoundException { // (1)
        new AgentBuilder.Default()
                .type(isSubTypeOf(Class.forName("javax.servlet.Servlet"))) // (2)
                .transform((builder, typeDescription, classLoader, module) ->
                    builder.method(named("service")) // (3)
                            .intercept(
                                    MethodDelegation.to(Interceptor.class) // (4)
                            )
                ).installOn(instrumentation);
    }

    public static class Interceptor {
        public static void intercept(ServletRequest req, ServletResponse res) {
            try {
                final ServletOutputStream outputStream = res.getOutputStream();

                outputStream.println("Hello, transformed world!");
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
