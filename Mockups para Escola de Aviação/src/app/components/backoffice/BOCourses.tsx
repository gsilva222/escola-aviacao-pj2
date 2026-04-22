import { useState } from "react";
import { BookOpen, ChevronDown, ChevronRight, Clock, Users, Plus, Layers } from "lucide-react";
import { mockCourses } from "../../data/mockData";

const TYPE_MAP: Record<string, { label: string; bg: string; color: string }> = {
  theoretical: { label: "Teórico", bg: "#DBEAFE", color: "#1D4ED8" },
  practical: { label: "Prático", bg: "#DCFCE7", color: "#16A34A" },
  simulator: { label: "Simulador", bg: "#F3E8FF", color: "#7C3AED" },
};

const COURSE_COLORS = ["#1565C0", "#7C3AED", "#059669", "#D97706"];

export function BOCourses() {
  const [expanded, setExpanded] = useState<string | null>("ppl");

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-6">
        <p className="text-sm" style={{ color: "#64748B" }}>{mockCourses.length} cursos disponíveis · EASA Part-FCL Approved</p>
        <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
          <Plus className="w-4 h-4" />
          Novo Curso
        </button>
      </div>

      {/* Course cards */}
      <div className="space-y-4">
        {mockCourses.map((course, ci) => {
          const isOpen = expanded === course.id;
          const color = COURSE_COLORS[ci % COURSE_COLORS.length];
          return (
            <div key={course.id} className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
              {/* Course Header */}
              <button
                onClick={() => setExpanded(isOpen ? null : course.id)}
                className="w-full flex items-center gap-5 p-6 text-left hover:bg-gray-50/50 transition-colors"
              >
                <div className="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: `${color}18` }}>
                  <BookOpen className="w-6 h-6" style={{ color }} />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex flex-wrap items-center gap-3 mb-1">
                    <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>{course.name}</h3>
                    <span className="px-2.5 py-0.5 rounded-full text-xs" style={{ background: `${color}18`, color, fontWeight: 600 }}>EASA Part-FCL</span>
                  </div>
                  <div className="flex flex-wrap gap-5 text-sm" style={{ color: "#64748B" }}>
                    <span className="flex items-center gap-1.5"><Clock className="w-3.5 h-3.5" />{course.duration}</span>
                    <span className="flex items-center gap-1.5"><Layers className="w-3.5 h-3.5" />{course.flightHours}h voo + {course.theoreticalHours}h teórico</span>
                    <span className="flex items-center gap-1.5"><Users className="w-3.5 h-3.5" />{course.enrolled} ativos · {course.completed} concluídos</span>
                    <span style={{ color, fontWeight: 700 }}>€{course.price.toLocaleString("pt-PT")}</span>
                  </div>
                </div>
                <div className="flex items-center gap-3 flex-shrink-0">
                  <div className="text-right hidden md:block">
                    <div className="text-sm" style={{ color: "#0F2344", fontWeight: 700 }}>{course.modules.length} módulos</div>
                    <div className="text-xs" style={{ color: "#94A3B8" }}>{course.modules.filter(m => m.type === "practical").length} práticos</div>
                  </div>
                  {isOpen ? <ChevronDown className="w-5 h-5" style={{ color: "#94A3B8" }} /> : <ChevronRight className="w-5 h-5" style={{ color: "#94A3B8" }} />}
                </div>
              </button>

              {/* Modules */}
              {isOpen && (
                <div style={{ borderTop: "1px solid #E2E8F0" }}>
                  <div className="p-4 grid gap-2" style={{ background: "#F8FAFC" }}>
                    <div className="flex items-center justify-between px-2 mb-1">
                      <span className="text-xs uppercase tracking-wider" style={{ color: "#94A3B8", fontWeight: 600 }}>Módulos do Curso</span>
                      <button className="flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-lg" style={{ background: "white", border: "1.5px solid #E2E8F0", color: "#374151" }}>
                        <Plus className="w-3 h-3" />
                        Adicionar Módulo
                      </button>
                    </div>
                    {course.modules.map((mod, mi) => {
                      const tp = TYPE_MAP[mod.type];
                      return (
                        <div key={mod.id} className="flex items-center gap-4 p-4 bg-white rounded-xl" style={{ border: "1px solid #E2E8F0" }}>
                          <div className="w-8 h-8 rounded-lg flex items-center justify-center text-sm flex-shrink-0" style={{ background: "#F1F5F9", color: "#64748B", fontWeight: 700 }}>
                            {mi + 1}
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{mod.name}</div>
                          </div>
                          <div className="flex items-center gap-3 flex-shrink-0">
                            <span className="flex items-center gap-1 text-xs" style={{ color: "#64748B" }}>
                              <Clock className="w-3.5 h-3.5" />
                              {mod.hours}h
                            </span>
                            <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: tp.bg, color: tp.color, fontWeight: 600 }}>{tp.label}</span>
                          </div>
                        </div>
                      );
                    })}
                  </div>

                  {/* Stats Bar */}
                  <div className="flex gap-6 px-6 py-4" style={{ borderTop: "1px solid #E2E8F0" }}>
                    {[
                      { label: "Teórico", hours: course.modules.filter(m => m.type === "theoretical").reduce((a, m) => a + m.hours, 0), color: "#1D4ED8" },
                      { label: "Prático", hours: course.modules.filter(m => m.type === "practical").reduce((a, m) => a + m.hours, 0), color: "#16A34A" },
                      { label: "Simulador", hours: course.modules.filter(m => m.type === "simulator").reduce((a, m) => a + m.hours, 0), color: "#7C3AED" },
                    ].map(s => (
                      <div key={s.label} className="text-sm">
                        <span style={{ color: "#94A3B8" }}>{s.label}: </span>
                        <span style={{ color: s.color, fontWeight: 700 }}>{s.hours}h</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
