import { test, expect } from '@playwright/test';

test('navigate to login', async ({ page }) => {
    await page.goto('/');

    await page.getByRole('link', { name: /login/i }).first().click();
    await expect(page).toHaveURL(/login/);
});


test('navigate to signup', async ({ page }) => {
    await page.goto('/');

    await page.getByRole('link', { name: /sign up/i }).first().click();
    await expect(page).toHaveURL(/signup/);
});

test('navigate home from other page', async ({ page }) => {
    await page.goto('/login');

    await page.getByRole('link', { name: /home/i }).click();
    await expect(page).toHaveURL("/");
});

test('navigate to protected route', async ({ page }) => {
    await page.goto('/');

    await page.getByRole('link', { name: /movies/i }).click();
    await expect(page.getByText(/error 403/i)).toBeVisible();
});