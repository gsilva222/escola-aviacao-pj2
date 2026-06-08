const ALL = [
  'dashboard', 'students', 'courses', 'flights', 'aircraft',
  'instructors', 'maintenance', 'evaluations', 'payments', 'reports', 'profiles', 'users',
] as const;

export type BoPageKey = (typeof ALL)[number];

export function allowedBoPages(staffProfile: string | null | undefined): Set<BoPageKey> {
  const pages = new Set<BoPageKey>(['dashboard']);
  const role = staffProfile?.trim() || 'Administrador';

  switch (role) {
    case 'Administrador':
      ALL.forEach((p) => pages.add(p));
      break;
    case 'Secretaria':
      ['students', 'courses', 'payments', 'evaluations', 'users'].forEach((p) => pages.add(p as BoPageKey));
      break;
    case 'Gestor Operacional':
      ['flights', 'aircraft', 'instructors', 'maintenance', 'evaluations', 'reports'].forEach((p) => pages.add(p as BoPageKey));
      break;
    case 'Instrutor':
      ['students', 'flights', 'evaluations'].forEach((p) => pages.add(p as BoPageKey));
      break;
    case 'Técnico de Manutenção':
      ['aircraft', 'maintenance'].forEach((p) => pages.add(p as BoPageKey));
      break;
    default:
      ALL.forEach((p) => pages.add(p));
  }
  return pages;
}

export function canAccessBoPage(staffProfile: string | null | undefined, menuKey: BoPageKey): boolean {
  return allowedBoPages(staffProfile).has(menuKey);
}
