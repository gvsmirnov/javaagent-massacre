This repo contains a set of examples of java agents and how they blow up in your face.

Below are some explanations of them, but they are half-intentionally kept concise and
mysterious. Try your best to figure them out on your own, but feel free to ping me if
you get stuck or need some extra info on any of them.


# Example 1: simplest possible agent

  1.  Example agent: retransform + return originalBytes
  2.  Show how RSS is much bigger
  3.  Show GC logs: not much of a difference
  4.  Use NativeMemoryTracking
  5.  Disable retransformation
  6.  RTFM (or RTFS)?
  7.  return null
  8.  Success!

# Example 2: do some simple instrumentation

  1.  Hello, world!
  2.  WTF, why are we not transforming?
  3.  Print all class names that come to transform()
  4.  WTF? Trace class loading
  5.  Ah shit! Don't load the transformed class in agent.
  6.  Ah shit! Different name formats.
  7.  OK, doing the transformation now.
  8.  WTF? Nothing is happening.
  9.  Silent exception swallowing? Nice. Get rid of this {{}} stuff.
  10. Ugh, VerifyError. Compute frames.
  11. Success!

# Example 3: agent has dependencies conflicting with those of the app

  1.  Run simple boot app, http get localhost:8080/hello
  2.  Run with agent. Why does it no longer find the class?
  3.  Fucking class loaders! Exclude.
  4.  NoSuchMethodError? Why?
  5.  Avoid instrumenting servlet. WTF?
  6.  Remove from classpath?
  7.  Can't really get all implementors of a class without loading it? Ugh.
  8.  Use isAnnotatedWith(Controller) and annotatedWith(RequestMapping)
  9.  OK, but we really want to get all the servlets. Let's get back to it.

# Example 4: use JVMTI to track class loading events

  1.  Disclaimer: doing native shit is dangerous!
  2.  Do something very very simple. Track class loading and pass that to Java
  3.  NPE? Where does that come from? Add checks
  4.  Let's log the class name from native code using Class.getName()!
  5.  Right. Check exception after getting method ID.
  6.  What the hell is this? Explore some hotspot sources.
  7.  Use (*jni)->ExceptionClear(jni);
  8.  NoSuchMethodError: getName?
  9.  OK, let's just use toString()
  10. Yay! OK, now also do java.
  11. Nothing happening if we do it before. How about we do it after?
  12. check_exception(jni, "JvmtiAgent#onClassLoaded");
  13. Log depth when leaving and exiting

  => (see also: https://bugs.openjdk.java.net/browse/JDK-8153202)
  => We can also screw up using some other methods as well. Annotations.
  => CircularClass...