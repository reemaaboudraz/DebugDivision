import { test, expect } from '@playwright/test';

test('check login routing', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByText(/login/i).first()).toBeVisible();
});

test('check signup routing', async ({ page }) => {
    await page.goto('/signup');
    await expect(page.getByText(/register/i)).toBeVisible();
});

test('check not found page routing', async ({ page }) => {
    await page.goto('/404');
    await expect(page.getByText(/error 404/i)).toBeVisible();
});

test('check unauthenticated dashboard routing', async ({ page }) => {
    await page.goto('/dashboard');
    await expect(page.getByText(/error 404/i)).toBeVisible();
});

test('check protected route routing', async ({ page }) => {
    await page.goto('/events');
    await expect(page.getByText(/error 403/i)).toBeVisible();
});
