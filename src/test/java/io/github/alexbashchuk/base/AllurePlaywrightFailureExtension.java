package io.github.alexbashchuk.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AllurePlaywrightFailureExtension
    implements TestWatcher, TestInstancePostProcessor {

  private static final ExtensionContext.Namespace NameSp =
      ExtensionContext.Namespace.create(AllurePlaywrightFailureExtension.class);

  // Store the test instance to access "page" on failure
  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    context.getStore(NameSp).put("testInstance", testInstance);
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
	  
    Object testInstance = context.getStore(NameSp).get("testInstance");
    if (testInstance == null) return;

    Page page = extractPageField(testInstance);
    if (page == null) return;
    
	try {
		Files.createDirectories(Paths.get("target/screenshots"));
		// Save a screenshot to the hard drive as a file
		page.screenshot(new Page.ScreenshotOptions()
	      .setFullPage(true)
	      .setPath(Paths.get("target/screenshots/" + context.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_") + ".png")));
	} 
	catch (IOException e) {
		e.printStackTrace();
	}
	
    try {
      // Add a screenshot to the Allure report as a byte stream
      byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
      Allure.addAttachment("Playwright screenshot", "image/png", new ByteArrayInputStream(png), "png");
    } catch (Exception ignored) {
      // Don't mask original test failure if screenshot fails
    }

    try {
      String url = page.url();
      Allure.addAttachment("Page URL", "text/plain",
          new ByteArrayInputStream(url.getBytes(StandardCharsets.UTF_8)), "txt");
    } catch (Exception ignored) { }

    try {
      String html = page.content();
      Allure.addAttachment("Page HTML", "text/html",
          new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), "html");
    } catch (Exception ignored) { }
  }

  private Page extractPageField(Object testInstance) {
    // Looks for a field named "page" in the class hierarchy (BaseUiTest -> your test class)
    Class<?> c = testInstance.getClass();
    while (c != null) {
      try {
        Field f = c.getDeclaredField("page");
        f.setAccessible(true);
        Object v = f.get(testInstance);
        return (v instanceof Page) ? (Page) v : null;
      } catch (NoSuchFieldException e) {
        c = c.getSuperclass();
      } catch (IllegalAccessException e) {
        return null;
      }
    }
    return null;
  }
}