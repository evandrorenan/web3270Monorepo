from playwright.sync_api import sync_playwright

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context(viewport={'width': 1280, 'height': 800})
    page = context.new_page()

    print("Navigating to Home...")
    page.goto("http://localhost:5173/")

    # Check Header
    print("Checking Header...")
    page.wait_for_selector("text=Web3270")

    # Check Terminal Page
    print("Checking Terminal Page...")
    page.wait_for_selector("text=3270 Terminal Session")

    # Take screenshot of Terminal
    page.screenshot(path="/home/jules/verification/terminal_page.png")
    print("Screenshot saved: terminal_page.png")

    # Navigate to Reports
    print("Navigating to Reports...")
    page.click("text=Reports")
    page.wait_for_selector("text=System Reports")

    # Navigate to Upload
    print("Navigating to Upload...")
    page.click("text=Upload")
    page.wait_for_selector("text=Upload & Download")

    # Take screenshot of Upload
    page.screenshot(path="/home/jules/verification/upload_page.png")
    print("Screenshot saved: upload_page.png")

    browser.close()

with sync_playwright() as playwright:
    run(playwright)
