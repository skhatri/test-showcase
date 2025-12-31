import { test, expect } from '@playwright/test';

test('google.com.au loads (fast smoke) @fast', async ({ page }) => {
  await page.goto('https://www.google.com.au/', { waitUntil: 'domcontentloaded' });
  await expect(page).toHaveTitle(/Google/i);
});

test('google.com.au search (network dependent)', async ({ page }) => {
  await page.goto('https://www.google.com.au/', { waitUntil: 'domcontentloaded' });

  const consentButtons = [
    page.getByRole('button', { name: /I agree/i }),
    page.getByRole('button', { name: /Accept all/i }),
    page.getByRole('button', { name: /Accept all cookies/i }),
  ];
  for (const b of consentButtons) {
    if (await b.isVisible().catch(() => false)) {
      await b.click().catch(() => {});
      break;
    }
  }

  const q = page.getByRole('combobox', { name: /Search/i }).or(page.locator('textarea[name="q"], input[name="q"]')).first();
  await q.waitFor({ state: 'visible' });
  await q.fill('Playwright');
  await q.press('Enter');

  await page.waitForLoadState('domcontentloaded');
});

