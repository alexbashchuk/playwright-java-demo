package io.github.alexbashchuk.base;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class AllureConsoleLogsExtension implements BeforeEachCallback, AfterEachCallback {

  private PrintStream originalOut;
  private PrintStream originalErr;

  private ByteArrayOutputStream outBuffer;
  private ByteArrayOutputStream errBuffer;

  @Override
  public void beforeEach(ExtensionContext context) {
    originalOut = System.out;
    originalErr = System.err;

    outBuffer = new ByteArrayOutputStream();
    errBuffer = new ByteArrayOutputStream();

    System.setOut(new PrintStream(outBuffer, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(errBuffer, true, StandardCharsets.UTF_8));
  }

  @Override
  public void afterEach(ExtensionContext context) {
    // Restore console
    System.setOut(originalOut);
    System.setErr(originalErr);

    String stdout = outBuffer.toString(StandardCharsets.UTF_8);
    String stderr = errBuffer.toString(StandardCharsets.UTF_8);

    if (!stdout.isBlank()) {
      Allure.addAttachment("STDOUT", "text/plain", stdout, ".log");
    }
    if (!stderr.isBlank()) {
      Allure.addAttachment("STDERR (SLF4J-simple logs)", "text/plain", stderr, ".log");
    }
  }
}