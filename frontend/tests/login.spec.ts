import { test, expect } from '@playwright/test';

test('login form renders', async ({ page }) => {
  await page.goto('/login');

  await expect(page.getByRole('textbox', { name: /email/i })).toBeVisible();
  await expect(page.getByLabel(/password/i)).toBeVisible();
  await expect(page.getByRole('button', { name: /login/i })).toBeVisible();
});

test('login fails appropriately', async ({ page }) => {
  await page.goto('/login');

  await page.getByRole('textbox', { name: /email/i }).fill('test@not-real.com');
  await page.getByLabel(/password/i).fill('invalid-password');
  await page.getByRole('button', { name: /login/i }).click();

  await expect(
    page.getByText(/login failed/i)
  ).toBeVisible();
});