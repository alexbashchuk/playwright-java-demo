package io.github.alexbashchuk.pages;
import java.nio.file.Paths;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

public class LandingPage {
	
	 private Page page;
	 
	 public LandingPage (Page page) {
		    this.page = page;
	 }

	 private Locator CompanyBlock;    
	 private Locator ButtonChooseWorkDescription;
	 private Locator ButtonShortDetails;
	 private Locator ButtonLongStory;
	 private Locator ButtonHideDescription;
	 private Locator WorkStoryHeading;
	 private Locator CertIdentifier;
	 
	// Screen scrolling function for DEMO
	 public static void scrollToLacator(Locator locator) {
		    locator.evaluate ("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
	}
	 
	 // Define Company experience information block on the page
	 public void DefineDescriptionCompanyExperience (String BlockName) {
		 CompanyBlock = page.getByLabel(BlockName);
		 ButtonChooseWorkDescription = CompanyBlock.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions()
		    		.setName("Choose work description"));	 
	 }
	// Click on Choose work description button inside currently defined Company experience information block
	 public void ClickChooseWorkDescription () {
		scrollToLacator (ButtonChooseWorkDescription);
		page.waitForTimeout(1000);
	    ButtonChooseWorkDescription.click();
		page.waitForTimeout(2000);
	 }
	// Define Short Details button inside currently defined Company experience information block
	 public Locator DefineShortDetailsButton () {
		 ButtonShortDetails = CompanyBlock.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Short Details"));
		 return ButtonShortDetails;
	 }
	// Click on Short Details button inside currently defined Company experience information block
	 public void ClickShortDetailsButton () {
		 ButtonShortDetails.click();
		 page.waitForTimeout(2000);
	 }
	// Define Long Story button inside currently defined Company experience information block
	 public Locator DefineLongStoryButton () {
		 ButtonLongStory = CompanyBlock.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Long Story"));
		 return ButtonLongStory;
	 }
	// Click on Long Story button inside currently defined Company experience information block
	 public void ClickLongStoryButton () {
		 ButtonLongStory.click();
		 page.waitForTimeout(2000);
	 }
	// Define Hide description button inside currently defined Company experience information block
	 public Locator DefineHideDescriptionButton () {
		 ButtonHideDescription = CompanyBlock.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Hide description"));
		 return ButtonHideDescription;
	 }
	// Click on Hide description button inside currently defined Company experience information block
	 public void ClickHideDescriptionButton () {
		 ButtonHideDescription.click();
		 page.waitForTimeout(2000);
	 }
	// Define work story header inside currently defined Company experience information block
	 public Locator DefineWorkStoryHeader () {
		 WorkStoryHeading = CompanyBlock.locator(".expBodyTitle");
		 return WorkStoryHeading;
	 }	 
	 // Get a Header text in the opened work description of currently defined Company experience information block
	 public String GetWorkStoryHeaderText () {
		 DefineWorkStoryHeader ();
		 String WorkDescriptionHeaderName = WorkStoryHeading.innerText();
		 return WorkDescriptionHeaderName;
	 }
	 
	 /* Below are the functions that opens certificates pictures and save the screenshot for matching */
	 
	 // Define certificate
	 public void DefineCertificateLocation (String CertificateName) {
		 CertIdentifier = page.locator(".certLink").getByText(CertificateName);
	 }
	 // Click on defined certificate link
	 public void ClickOnCertificateLink () {
		 scrollToLacator (CertIdentifier);
		 page.waitForTimeout(1000);
		 CertIdentifier.click();
		 page.waitForTimeout(2000);
	 }
	 // Capture and save screenshot of certificate
	 public void CaptureCertificate(String PathToSaveCert) {
			Locator modal = page.locator(".certModal");
			modal.waitFor(new Locator.WaitForOptions()
			        .setState(WaitForSelectorState.VISIBLE));
			modal.screenshot(new Locator.ScreenshotOptions()
			        .setPath(Paths.get(PathToSaveCert)));
			modal.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("CLOSE")).click(); // Close cert image
			page.waitForTimeout(1000);
			// Temporary deprecated function part
			/* Locator certImage = page.locator("img.certImage");
		    page.waitForFunction(
		        "img => img.complete && img.naturalWidth > 0",
		        certImage
		    );	
		    certImage.screenshot(new Locator.ScreenshotOptions()
		        .setPath(Paths.get(PathToSaveCert)));
			 */
	 }
}