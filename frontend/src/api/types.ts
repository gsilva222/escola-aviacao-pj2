export interface Student {
  id: number;
  name: string;
  email: string;
  phone?: string;
  courseId: number;
  courseName?: string;
  instructorId?: number;
  instructorName?: string;
  status: string;
  progress?: number;
  flightHours?: number;
  theoreticalHours?: number;
  paymentStatus?: string;
  address?: string;
  nationality?: string;
}

export interface Course {
  id: number;
  name: string;
  duration?: string;
  flightHours?: number;
  theoreticalHours?: number;
  price?: number;
  description?: string;
}

export interface Instructor {
  id: number;
  name: string;
  license: string;
  specialization?: string;
  status?: string;
  email?: string;
  phone?: string;
}

export interface Aircraft {
  id: number;
  registration: string;
  model: string;
  type: string;
  status?: string;
  flightHours?: number;
  location?: string;
  fuelLevel?: number;
}

export interface Flight {
  id: number;
  flightDate: string;
  flightTime?: string;
  duration?: number;
  studentId: number;
  studentName?: string;
  instructorId: number;
  instructorName?: string;
  aircraftId: number;
  aircraftRegistration?: string;
  origin?: string;
  destination?: string;
  flightType?: string;
  status: string;
}

export interface Evaluation {
  id: number;
  studentId: number;
  studentName?: string;
  courseId: number;
  examName: string;
  evaluationDate?: string;
  score?: number;
  maxScore?: number;
  status: string;
  evaluationType?: string;
}

export interface Payment {
  id: number;
  studentId: number;
  studentName?: string;
  description: string;
  amount: number;
  dueDate?: string;
  paidDate?: string;
  status: string;
  paymentMethod?: string;
}

export interface Maintenance {
  id: number;
  aircraftId: number;
  aircraftRegistration?: string;
  maintenanceType: string;
  description: string;
  status: string;
  priority?: string;
  startDate?: string;
  estimatedEndDate?: string;
}

export interface Perfil {
  id: number;
  nome: string;
  descricao?: string;
}

export interface ReportsSummary {
  totalStudents: number;
  activeStudents: number;
  suspendedStudents: number;
  completedStudents: number;
  totalCourses: number;
  totalFlights: number;
  scheduledFlights: number;
  completedFlights: number;
  totalAircraft: number;
  operationalAircraft: number;
  maintenanceAircraft: number;
  groundedAircraft: number;
  pendingPayments: number;
  overduePayments: number;
  totalPendingAmount: number;
  activeMaintenances: number;
  studentsByCourse: Record<string, number>;
}

export interface BoDashboard {
  reports: ReportsSummary;
  flightsToday: number;
  completedFlightsToday: number;
  scheduledFlightsToday: number;
  flightHoursThisMonth: number;
  revenueThisMonth: number;
  recentActivity: { icon: string; title: string; subtitle: string; type: string }[];
}

export interface FoDashboard {
  studentId: number;
  studentName: string;
  courseName?: string;
  progress?: number;
  completedFlightHours: number;
  requiredFlightHours: number;
  upcomingFlights: number;
  pendingPayments: number;
  pendingAmount: number;
  recentFlights: Flight[];
  recentEvaluations: Evaluation[];
}

export interface FoHoursSummary {
  completedHours: number;
  localHours: number;
  navigationHours: number;
  totalFlights: number;
  requiredHours: number;
  remainingHours: number;
  progressPercent: number;
}

export interface StudentDocument {
  id: number;
  fileName: string;
  category?: string;
  uploadedAt: string;
}

export interface PaymentSummary {
  totalPending: number;
  totalOverdue: number;
  totalPaid: number;
  pendingCount: number;
  overdueCount: number;
  paidCount: number;
}
