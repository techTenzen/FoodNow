import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthRequest, RegisterRequest, ResetPasswordRequest, AuthResponse, User } from '../models/auth.model';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
    this.initializeUser();
  }

  private initializeUser(): void {
    if (this.tokenService.isAuthenticated()) {
      const token = this.tokenService.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const user: User = {
            email: payload.sub,
            firstName: payload.firstName || '',
            lastName: payload.lastName || '',
            roles: payload.roles || []
          };
          this.currentUserSubject.next(user);
        } catch (error) {
          this.logout();
        }
      }
    }
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          this.tokenService.setToken(response.token);
          const user: User = {
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            roles: response.roles
          };
          this.currentUserSubject.next(user);
        })
      );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData)
      .pipe(
        tap(response => {
          this.tokenService.setToken(response.token);
          const user: User = {
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            roles: response.roles
          };
          this.currentUserSubject.next(user);
        })
      );
  }

  resetPassword(resetData: ResetPasswordRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, resetData);
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe({
      next: () => {
        this.tokenService.removeToken();
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      },
      error: () => {
        // Even if logout fails on server, clear local storage
        this.tokenService.removeToken();
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      }
    });
  }

  isAuthenticated(): boolean {
    return this.tokenService.isAuthenticated();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}