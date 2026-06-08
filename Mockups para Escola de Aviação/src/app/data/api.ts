const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

export type StudentDTO = {
  id: number;
  name: string;
  email: string;
  phone?: string | null;
  nif?: string | null;
  birthdate?: string | null;
  address?: string | null;
  nationality?: string | null;
  courseId?: number | null;
  courseName?: string | null;
  status?: string | null;
  enrollmentDate?: string | null;
  progress?: number | null;
  flightHours?: number | null;
  theoreticalHours?: number | null;
  paymentStatus?: string | null;
  avatar?: string | null;
  instructorName?: string | null;
};

export type CourseDTO = {
  id: number;
  name: string;
  duration?: string | null;
  flightHours?: number | null;
  theoreticalHours?: number | null;
  price?: number | null;
  enrolled?: number | null;
  completed?: number | null;
  description?: string | null;
};

function buildQuery(params?: Record<string, string | number | null | undefined>) {
  if (!params) return "";
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && `${value}`.trim() !== "") {
      search.set(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `?${query}` : "";
}

async function requestJson<T>(url: string): Promise<T> {
  const response = await fetch(url, { headers: { "Content-Type": "application/json" } });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `HTTP ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export function getStudents(params?: { courseId?: number; status?: string }) {
  const query = buildQuery(params);
  return requestJson<StudentDTO[]>(`${API_BASE}/bo/students${query}`);
}

export function getStudent(id: string | number) {
  return requestJson<StudentDTO>(`${API_BASE}/bo/students/${id}`);
}

export function getCourses() {
  return requestJson<CourseDTO[]>(`${API_BASE}/bo/courses`);
}
