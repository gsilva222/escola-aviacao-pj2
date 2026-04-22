import { createBrowserRouter, redirect } from "react-router";

import { LandingPage } from "./components/LandingPage";

// BackOffice
import { BOLogin } from "./components/backoffice/BOLogin";
import { BOLayout } from "./components/backoffice/BOLayout";
import { BODashboard } from "./components/backoffice/BODashboard";
import { BOStudents } from "./components/backoffice/BOStudents";
import { BOStudentFile } from "./components/backoffice/BOStudentFile";
import { BOCourses } from "./components/backoffice/BOCourses";
import { BOFlights } from "./components/backoffice/BOFlights";
import { BOAircraft } from "./components/backoffice/BOAircraft";
import { BOMaintenance } from "./components/backoffice/BOMaintenance";
import { BOEvaluations } from "./components/backoffice/BOEvaluations";
import { BOPayments } from "./components/backoffice/BOPayments";
import { BOReports } from "./components/backoffice/BOReports";

// FrontOffice
import { FOLogin } from "./components/frontoffice/FOLogin";
import { FOLayout } from "./components/frontoffice/FOLayout";
import { FODashboard } from "./components/frontoffice/FODashboard";
import { FOSchedule } from "./components/frontoffice/FOSchedule";
import { FOFlights } from "./components/frontoffice/FOFlights";
import { FOHours } from "./components/frontoffice/FOHours";
import { FOEvaluations } from "./components/frontoffice/FOEvaluations";
import { FODocuments } from "./components/frontoffice/FODocuments";
import { FOPayments } from "./components/frontoffice/FOPayments";
import { FOProfile } from "./components/frontoffice/FOProfile";

export const router = createBrowserRouter([
  { path: "/", Component: LandingPage },

  // ── BackOffice ──────────────────────────────────────────────────────────────
  { path: "/bo/login", Component: BOLogin },
  {
    path: "/bo",
    Component: BOLayout,
    children: [
      { index: true, loader: () => redirect("/bo/dashboard") },
      { path: "dashboard", Component: BODashboard },
      { path: "students", Component: BOStudents },
      { path: "students/:id", Component: BOStudentFile },
      { path: "courses", Component: BOCourses },
      { path: "flights", Component: BOFlights },
      { path: "aircraft", Component: BOAircraft },
      { path: "maintenance", Component: BOMaintenance },
      { path: "evaluations", Component: BOEvaluations },
      { path: "payments", Component: BOPayments },
      { path: "reports", Component: BOReports },
    ],
  },

  // ── FrontOffice ─────────────────────────────────────────────────────────────
  { path: "/fo/login", Component: FOLogin },
  {
    path: "/fo",
    Component: FOLayout,
    children: [
      { index: true, loader: () => redirect("/fo/dashboard") },
      { path: "dashboard", Component: FODashboard },
      { path: "schedule", Component: FOSchedule },
      { path: "flights", Component: FOFlights },
      { path: "hours", Component: FOHours },
      { path: "evaluations", Component: FOEvaluations },
      { path: "documents", Component: FODocuments },
      { path: "payments", Component: FOPayments },
      { path: "profile", Component: FOProfile },
    ],
  },
]);
