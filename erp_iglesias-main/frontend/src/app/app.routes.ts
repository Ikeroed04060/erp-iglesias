import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { LoginComponent } from './core/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ChurchComponent } from './features/church/church.component';
import { UsersComponent } from './features/users/users.component';
import { PeopleComponent } from './features/people/people.component';
import { CoursesComponent } from './features/courses/courses.component';
import { EnrollmentsComponent } from './features/erollments/enrollments.component';
import { OfferingsComponent } from './features/offerings/offerings.component';
import { PaymentsComponent } from './features/payments/payments.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'church', component: ChurchComponent, canActivate: [authGuard] },
  { path: 'users', component: UsersComponent, canActivate: [authGuard] },
  { path: 'people', component: PeopleComponent, canActivate: [authGuard] },
  { path: 'courses', component: CoursesComponent, canActivate: [authGuard] },
  { path: 'enrollments', component: EnrollmentsComponent, canActivate: [authGuard] },
  { path: 'offerings', component: OfferingsComponent, canActivate: [authGuard] },
  { path: 'payments', component: PaymentsComponent, canActivate: [authGuard] }
];
