package jug.gvsmirnov.javaagent.retransform;

import jug.gvsmirnov.javaagent.ExperimentBuilder;

public class Setup {

    private static final String applicationJarPath = System.getProperty("app.jar.path");
    private static final String agentJarPath       = System.getProperty("agent.jar.path");

    public static void main(String[] args) {
        makeExperimentalSetup(args[0]).build().perform();
    }

    private static ExperimentBuilder makeExperimentalSetup(String setupName) {
        final ExperimentBuilder builder = new ExperimentBuilder(setupName).applicationJar(applicationJarPath);

        switch (setupName) {
            case "baseline":     return builder;
            case "agent":        return agent(builder);

            case "baseline-rss": return baselineRss(builder);
            case "agent-rss":    return agentRss(builder);

            case "baseline-nmt": return baselineNmt(builder);
            case "agent-nmt":    return agentNmt(builder);

            default: throw new IllegalArgumentException("Unknown setup: " + setupName);
        }
    }

    private static ExperimentBuilder agent(ExperimentBuilder builder) {
        return builder.withAgent(agentJarPath);
    }

    private static ExperimentBuilder baselineRss(ExperimentBuilder builder) {
        return builder.trackResidentSetSize(true);
    }

    private static ExperimentBuilder agentRss(ExperimentBuilder builder) {
        return builder.withAgent(agentJarPath).trackResidentSetSize(true);
    }

    private static ExperimentBuilder baselineNmt(ExperimentBuilder builder) {
        return builder.trackResidentSetSize(true).trackNativeMemory(true);
    }

    private static ExperimentBuilder agentNmt(ExperimentBuilder builder) {
        return builder.withAgent(agentJarPath).trackResidentSetSize(true).trackNativeMemory(true);
    }

}
