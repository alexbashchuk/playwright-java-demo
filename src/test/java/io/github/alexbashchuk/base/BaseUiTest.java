package io.github.alexbashchuk.base;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.awt.*;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseUiTest {
  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;

  @BeforeAll
  void beforeAll() {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int w = (int) screen.getWidth();
    int h = (int) screen.getHeight();
	playwright = Playwright.create();
	browser = playwright.chromium().launch(
	          new BrowserType.LaunchOptions()
	          .setChannel("chrome")
	          .setHeadless(false).setArgs(List.of("--window-size=" + w + "," + h)));
	context = browser.newContext(
		    new Browser.NewContextOptions()
		        .setViewportSize(null)   // disables Playwright viewport = full window
		);     // fresh profile per test class with maximized browser window
	page = context.newPage();            // fresh page per test class
	page.navigate("https://alexbashchuk.github.io");
	page.waitForTimeout(2000);
  }

  @AfterAll
  void afterAll() {
	context.close();                     // also closes page
	browser.close();
	playwright.close();
  }

 /* @BeforeEach
  void beforeEach() {
    
  }

  @AfterEach
  void afterEach() {
   
  }*/
}