package com.aireux.onsen;

import java.lang.instrument.Instrumentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassRedefinitionAgent {

  static final Logger log = LogManager.getLogger(ClassRedefinitionAgent.class);

  static volatile Instrumentation instrumentation;

  public static void agentmain(String args, Instrumentation instrumentation) {
    if (!instrumentation.isRedefineClassesSupported()) {
      log.fatal("Class redefinition is not enabled.  Please ");
    }
  }
}
