package io.github.alexbashchuk.tests;
//import com.microsoft.playwright.*;

import io.github.alexbashchuk.base.BaseUiTest;
import io.github.alexbashchuk.base.ImageToTemplateMatcher;
import io.github.alexbashchuk.base.AllurePlaywrightFailureExtension;
import io.github.alexbashchuk.pages.LandingPage;
//import io.github.alexbashchuk.base.AllureConsoleLogsExtension;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(AllureConsoleLogsExtension.class) // Add logging redirected to Allure report
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Remove test instance after this test class execution finish
@ExtendWith(AllurePlaywrightFailureExtension.class) // Add screenshots for failures
public class ButtonsFunctionalityCheckTests extends BaseUiTest{
	
	
	// First test - this is a simple API test to check site response - it is here just for example (not suitable in real situation in JUnit test sets)
	@Test
	@Order(1)
	void API_CheckSiteResponse() {
		APIRequestContext api = playwright.request().newContext();
	    APIResponse response = api.get("https://alexbashchuk.github.io");
	    int status = response.status();

	    assertTrue(status >= 200 && status < 400,
	        "Site is not accessible. HTTP status: " + status);
	}
	
	@Test
	@Order(2)
	void OpenPortfolio() {
		assertTrue(page.title().contains("myportfolio"));
	}
	
	// The parameters below define work experience Companies for that test function
	@ParameterizedTest
	@ValueSource(strings = {"Experience at Southern Company", "Experience at Lockheed Martin", "Experience at Fiserv", "Experience at Ford Motor Company", 
			"Experience at Royal Caribbean Ltd.", "Experience at Canfield Scientific", "Experience at Zodiac Interactive"})
	void CheckWorkDescriptionBlock (String experience) {
		
		LandingPage landingPage = new LandingPage(page);
				
		// Define Company experience for validation and click on "Choose work description" button
		landingPage.DefineDescriptionCompanyExperience (experience);
		landingPage.ClickChooseWorkDescription ();
		
		// Validate functionality of "Short Details" button
		landingPage.DefineShortDetailsButton ();
		landingPage.ClickShortDetailsButton ();
		String HeaderShort = landingPage.GetWorkStoryHeaderText();
		assertTrue("Work description".equals(HeaderShort)); // - This is 1-st example how 2 Strings can be compared
		assertTrue (landingPage.DefineHideDescriptionButton ().isVisible()); // Assert button "Hide description" appeared
		
		// Validate functionality of "Long Story" button
		landingPage.DefineLongStoryButton ();
		landingPage.ClickLongStoryButton ();
		String HeaderLong = landingPage.GetWorkStoryHeaderText();
		assertEquals ("Work story", HeaderLong); // - This is 2-nd example how 2 Strings can be compared
		assertTrue (landingPage.DefineHideDescriptionButton ().isVisible()); // Assert button "Hide description" is visible
		
		// Validate functionality of "Hide description" button
		landingPage.ClickHideDescriptionButton ();
		assertFalse (landingPage.DefineWorkStoryHeader ().isVisible()); // Assert work story header is hidden
		assertEquals(0, landingPage.DefineShortDetailsButton ().count(), "Button \"Short Details\" should not exist in DOM"); /* Validate button "Short Details" 
		not exists in the re-rendered through React DOM */
		assertFalse(landingPage.DefineLongStoryButton ().isVisible(), "Button \"Long Story\"should not be visible"); /* Validate button "Long Story" invisible OR
		not exists in the re-rendered through React DOM - THAT VALIDATION GOOD IF JUST VISIBILITY MATTERS*/
		assertEquals(0, landingPage.DefineHideDescriptionButton ().count(), "Button \"Hide Description\" should not exist in DOM"); /* Validate button
		"Hide Description" not exists in the re-rendered through React DOM */
	    	    
	    //page.pause();
	}
	// The parameters below define certificates for that test function
	@ParameterizedTest
	@ValueSource(strings = {"AS1", "AS2", "TDS1", "TDS2", "qTest", "CTFL"})
	void ValidateCertificatePictures (String CertName) {
		LandingPage landingPage = new LandingPage(page);
		landingPage.DefineCertificateLocation(CertName);
		landingPage.ClickOnCertificateLink ();
		String CapturePath = "D:/PROGRAMMING/Java/Java_Programs/Portfolio_Java_Playwright/Screenshots/Captures/" + CertName + ".jpg";
		String TemplatePath = "D:/PROGRAMMING/Java/Java_Programs/Portfolio_Java_Playwright/Screenshots/Templates/" + CertName + ".jpg";		
		landingPage.CaptureCertificate (CapturePath);
		ImageToTemplateMatcher imageMatcher = new ImageToTemplateMatcher(TemplatePath, CapturePath, 80); // Create images matcher
		assertTrue (imageMatcher.matches()); // Getting and validating boolean result of images matching function
	}
}
