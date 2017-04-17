package jug.gvsmirnov.javaagent.jvmti;

import jug.gvsmirnov.javaagent.ExperimentBuilder;

// TODO: deduplicate
public class Setup {

    private static final String applicationJarPath = System.getProperty("app.jar.path");
    private static final String agentJarPath       = System.getProperty("agent.jar.path");

    public static void main(String[] args) {
        makeExperimentalSetup(args[0]).build().perform();
    }

    private static ExperimentBuilder makeExperimentalSetup(String setupName) {
        final ExperimentBuilder builder = new ExperimentBuilder(setupName)
                .applicationJar(applicationJarPath)
                .logStdOut().logStdErr();

        switch (setupName) {
            case "baseline":     return builder;
            case "agent":        return agent(builder);

            default: throw new IllegalArgumentException("Unknown setup: " + setupName);
        }
    }

    private static ExperimentBuilder agent(ExperimentBuilder builder) {
        return builder.withAgent(agentJarPath);
    }

    private static ExperimentBuilder agentTcl(ExperimentBuilder builder) {
        return builder.withAgent(agentJarPath).traceClassLoading(true);
    }

}
