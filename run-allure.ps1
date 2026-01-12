param(
  [string]$ProjectDir = (Get-Location).Path
)

$resultsDir = Join-Path $ProjectDir "target\allure-results"
$reportDir  = Join-Path $ProjectDir "target\site\allure-maven-plugin"
$historySrc = Join-Path $reportDir  "history"
$historyDst = Join-Path $resultsDir "history"

Write-Host "ProjectDir: $ProjectDir"
Write-Host "ResultsDir: $resultsDir"
Write-Host "ReportDir : $reportDir"

# 1) If previous report history exists, copy it into allure-results before running tests
if (Test-Path $historySrc) {
  Write-Host "Copying history from previous report..."
  New-Item -ItemType Directory -Force -Path $historyDst | Out-Null
  Copy-Item -Path (Join-Path $historySrc "*") -Destination $historyDst -Recurse -Force
} else {
  Write-Host "No previous history found (first run is normal)."
}

# 2) Run tests + generate report
Write-Host "Running Maven: test allure:report"
mvn test allure:report
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# 3) Serve the generated report
Write-Host "Serving Allure report..."
mvn allure:serve