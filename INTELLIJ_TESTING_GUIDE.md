# How to Run Tests in IntelliJ IDEA

## Prerequisites

1. **IntelliJ IDEA** (Community or Ultimate Edition)
2. **JDK 17** installed
3. **Project imported** as Maven project

## ✅ Step 1: Open Project in IntelliJ

1. Launch **IntelliJ IDEA**
2. Click **File → Open** (or Open Project)
3. Navigate to: `d:\Development\2026\ExpensesTracker`
4. Click **Open**
5. Select **"This Window"** to open in current window
6. Wait for IntelliJ to index the project (progress bar at bottom)

## ✅ Step 2: Configure JDK 17

1. Go to **File → Project Structure** (or press `Ctrl+Alt+Shift+S`)
2. Select **Project** on the left
3. Set **SDK** to: **Java 17** or **Temurin 17**
   - If not listed, click **"Add SDK"** → **"Download JDK"**
   - Select **Eclipse Temurin 17**
   - Click **Download and Select**
4. Click **Apply** → **OK**

## ✅ Step 3: Configure Maven (if needed)

1. Go to **File → Settings** (Windows) or **IntelliJ IDEA → Preferences** (Mac)
2. Search for **"Maven"**
3. Verify Maven is set to:
   - **Runner**: Bundled (recommended)
   - **Imported directories**: Your project path
4. Click **Apply** → **OK**

## ✅ Step 4: Build the Project

### Option A: Build via Menu
1. Click **Build → Build Project** (or press `Ctrl+F9`)
2. Wait for "Build complete" message

### Option B: Build via Maven
1. Open **Maven Tool Window**: View → Tool Windows → Maven
2. Expand `loantracker` → `Lifecycle`
3. Double-click **clean**
4. Then double-click **compile**

## ✅ Step 5: Run Tests

### Option A: Run All Tests

**Method 1: Via Test Class**
1. Open `src/test/java/com/loantracker/LoanTrackerAppTest.java`
2. Right-click on class name `LoanTrackerAppTest`
3. Select **Run 'LoanTrackerAppTest'** (or press `Ctrl+Shift+F10`)

**Method 2: Via Maven**
1. Open Maven Tool Window
2. Expand `loantracker` → `Lifecycle`
3. Double-click **test**

**Method 3: Via Terminal**
1. Open Terminal in IntelliJ (View → Tool Windows → Terminal)
2. Run: `mvn test`

### Option B: Run Specific Test Method

1. Open `LoanTrackerAppTest.java`
2. Find the test method (e.g., `testCreateLoanSuccess`)
3. Click on the green play icon ▶ next to the method name
4. Select **Run 'testCreateLoanSuccess'**

### Option C: Run Tests with Debugging

1. Right-click on test class or method
2. Select **Debug 'LoanTrackerAppTest'** (or press `Ctrl+Shift+D`)
3. Execution pauses at breakpoints

## 📊 View Test Results

After tests run, you'll see:

### Test Results Panel
- **✅ Passed Tests** - Green checkmark
- **❌ Failed Tests** - Red X mark
- **⊘ Skipped Tests** - Yellow dash
- **⚠ Error Tests** - Blue exclamation

### Test Output
- Click on test result to see details
- Stack trace shows error location
- Double-click to jump to test code

## 🔍 Troubleshooting Test Failures

### If tests fail with database errors:

#### 1. Delete Test Database
```bash
# In IntelliJ Terminal:
rm -r d:\Development\2026\ExpensesTracker\data\

# Or Windows Command Prompt:
rmdir /s data
```

#### 2. Rebuild Project
- **Build → Rebuild Project** (or `Ctrl+F9`)

#### 3. Clear Maven Cache
- **File → Invalidate Caches** (or `Ctrl+Shift+Alt+I`)
- Select **"Invalidate and Restart"**

#### 4. Run Tests Again
- Right-click on `LoanTrackerAppTest.java`
- Select **Run 'LoanTrackerAppTest'**

### If "JDK not found" error:

1. Go to **File → Project Structure**
2. Under **Project**, click **SDK dropdown**
3. If empty, click **"Add SDK"** → **"Download JDK"**
4. Select **Eclipse Temurin 17**
5. Complete the download

## 🎯 Expected Test Results

```
Running LoanTrackerAppTest

Test Results:
✅ testValidateBorrowerNameSuccess
✅ testValidateBorrowerNameEmpty
✅ testValidateBorrowerNameNull
✅ testValidatePrincipalAmountSuccess
✅ testValidatePrincipalAmountZero
✅ testValidatePrincipalAmountNegative
✅ testValidateInterestRateSuccess
✅ testValidateInterestRateOutOfRange
✅ testValidateLoanTermSuccess
✅ testValidateLoanTermTooShort
✅ testValidateLoanTermTooLong
✅ testValidateStartDateInFuture
✅ testCreateLoanSuccess
✅ testCreateLoanWithInvalidData
✅ testGetAllLoans
✅ testGetSummary
✅ testErrorCodeMapping
✅ testLoanTrackerExceptionToString

Results: 18 passed, 0 failed

BUILD SUCCESS
```

## 📈 View Code Coverage

### Generate Coverage Report

1. Right-click on test class
2. Select **Run 'LoanTrackerAppTest' with Coverage** (or **Debug with Coverage**)

### View Coverage Results

1. A coverage panel appears at bottom
2. Shows % coverage per file
3. Highlights covered/uncovered lines in code:
   - 🟢 Green = Covered
   - 🔴 Red = Not covered

### Coverage Report File

After coverage run:
- Coverage data saved to: `target/coverage/`
- Open in browser for detailed report

## 🔧 Configure Test Settings

### Change Test Runner

1. **File → Settings** → **Build, Execution, Deployment → Testing**
2. Select **Test runner**: JUnit 4 (already selected)
3. Click **Apply** → **OK**

### Configure VM Options

1. **File → Settings** → **Build, Execution, Deployment → Compiler → Kotlin**
2. Add any VM options needed
3. Click **Apply** → **OK**

## 💡 Tips & Tricks

### Quick Test Run
- Press `Ctrl+Shift+F10` (Run last test)
- Press `Ctrl+Shift+D` (Debug last test)

### Run All Tests in Package
- Right-click on `com.loantracker` package
- Select **Run Tests in 'loantracker'**

### Watch Mode (Continuous Testing)
1. Go to **Run → Edit Configurations**
2. Add new configuration for Maven
3. Set **Command line**: `test`
4. Enable **"Recompile on changes"**

### Create Custom Run Configuration

1. **Run → Edit Configurations** (or `Ctrl+Alt+Shift+F10`)
2. Click **"+" → JUnit**
3. Set:
   - **Name**: "All Tests"
   - **Test class**: `com.loantracker.LoanTrackerAppTest`
   - **JDK**: 17
4. Click **Apply** → **OK**
5. Run via **Run → Run 'All Tests'**

## 📝 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "JDK not found" | File → Project Structure → Add JDK 17 |
| "Maven not configured" | File → Settings → Maven → Use bundled |
| "Tests won't run" | Build → Rebuild Project |
| "Database errors" | Delete `data/` folder, Rebuild |
| "Can't find test class" | Build → Rebuild Project |
| "Import error" | File → Invalidate Caches → Restart |

---

## ✅ Verification Checklist

After running tests:

- [ ] All 18 tests passed
- [ ] No red X marks in test results
- [ ] Build shows "SUCCESS"
- [ ] Test output is visible
- [ ] No database errors
- [ ] Code coverage shows 90%+

---

**Last Updated:** June 14, 2026  
**Java Version:** 17 (LTS)  
**Build Tool:** Maven 3.6+  
**Test Framework:** JUnit 4
