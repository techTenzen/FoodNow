import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./auth/login/login').then(c => c.LoginComponent) },
  { path: 'register', loadComponent: () => import('./auth/register/register').then(c => c.RegisterComponent) },
  { path: 'reset-password', loadComponent: () => import('./auth/reset-password/reset-password').then(c => c.ResetPasswordComponent) },
  { path: 'home', loadComponent: () => import('./home/home').then(c => c.HomeComponent), canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/home' }
];