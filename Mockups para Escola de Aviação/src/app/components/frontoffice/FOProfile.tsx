import { useState } from "react";
import { loggedStudent } from "../../data/mockData";
import {
  User, Mail, Phone, MapPin, Calendar, Edit2, Camera,
  Shield, Bell, Lock, ChevronRight, CheckCircle2, Plane
} from "lucide-react";

const TABS = ["Dados Pessoais", "Segurança", "Notificações"];

export function FOProfile() {
  const [tab, setTab] = useState(0);
  const [editing, setEditing] = useState(false);
  const student = loggedStudent;

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Profile Header */}
      <div className="bg-white rounded-2xl p-6 mb-5 relative overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <div
          className="absolute top-0 left-0 right-0 h-24"
          style={{ background: "linear-gradient(135deg, #0F2344, #1565C0)" }}
        />
        <div className="relative pt-12">
          <div className="flex items-end gap-5 flex-wrap">
            {/* Avatar */}
            <div className="relative">
              <div
                className="w-24 h-24 rounded-2xl flex items-center justify-center text-3xl text-white shadow-lg"
                style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 800, border: "4px solid white" }}
              >
                {student.avatar}
              </div>
              <button
                className="absolute -bottom-1 -right-1 w-8 h-8 rounded-full flex items-center justify-center shadow-md"
                style={{ background: "#1565C0" }}
              >
                <Camera className="w-4 h-4 text-white" />
              </button>
            </div>
            <div className="flex-1 pb-1">
              <div className="flex items-center gap-3 flex-wrap mb-1">
                <h1 className="text-2xl" style={{ color: "#0F2344", fontWeight: 800 }}>{student.name}</h1>
                <span className="px-3 py-1 rounded-full text-xs" style={{ background: "#DCFCE7", color: "#16A34A", fontWeight: 600 }}>
                  <CheckCircle2 className="inline w-3 h-3 mr-1" />
                  Ativo
                </span>
              </div>
              <p className="text-sm mb-2" style={{ color: "#64748B" }}>
                {student.course} · Nº Aluno 100{student.id}
              </p>
              <div className="flex flex-wrap gap-4 text-xs" style={{ color: "#94A3B8" }}>
                <span className="flex items-center gap-1"><Mail className="w-3.5 h-3.5" />{student.email}</span>
                <span className="flex items-center gap-1"><Phone className="w-3.5 h-3.5" />{student.phone}</span>
              </div>
            </div>
            <button
              onClick={() => setEditing(!editing)}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm transition-all hover:opacity-90"
              style={{
                background: editing ? "#DCFCE7" : "linear-gradient(135deg, #1565C0, #0D47A1)",
                color: editing ? "#16A34A" : "white",
                fontWeight: 600,
                border: editing ? "1.5px solid #BBF7D0" : "none",
              }}
            >
              <Edit2 className="w-4 h-4" />
              {editing ? "Guardar" : "Editar Perfil"}
            </button>
          </div>

          {/* Quick Stats */}
          <div className="grid grid-cols-3 md:grid-cols-6 gap-3 mt-6">
            {[
              { label: "Horas de Voo", value: `${student.flightHours}h`, color: "#1565C0" },
              { label: "Progresso", value: `${student.progress}%`, color: "#7C3AED" },
              { label: "Avaliações", value: student.evaluations.length, color: "#059669" },
              { label: "Aprovadas", value: student.evaluations.filter(e => e.status === "passed").length, color: "#16A34A" },
              { label: "Documentos", value: student.documents.length, color: "#D97706" },
              { label: "Voos Agendados", value: student.upcomingFlights.length, color: "#DC2626" },
            ].map(s => (
              <div key={s.label} className="text-center p-3 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                <div className="text-lg mb-0.5" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
                <div className="text-xs" style={{ color: "#94A3B8" }}>{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-5 bg-white rounded-xl p-1.5" style={{ border: "1px solid #E2E8F0", width: "fit-content" }}>
        {TABS.map((t, i) => (
          <button
            key={t}
            onClick={() => setTab(i)}
            className="px-5 py-2 rounded-lg text-sm transition-all"
            style={{
              background: tab === i ? "#1565C0" : "transparent",
              color: tab === i ? "white" : "#64748B",
              fontWeight: tab === i ? 700 : 400,
            }}
          >
            {t}
          </button>
        ))}
      </div>

      {/* Tab 0: Personal Data */}
      {tab === 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {/* Personal */}
          <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
            <h3 className="text-sm mb-5 uppercase tracking-wider flex items-center gap-2" style={{ color: "#94A3B8", fontWeight: 600 }}>
              <User className="w-4 h-4" />
              Dados Pessoais
            </h3>
            <div className="space-y-5">
              {[
                { label: "Nome Completo", value: student.name, icon: User },
                { label: "Email", value: student.email, icon: Mail },
                { label: "Telefone", value: student.phone, icon: Phone },
                { label: "Data de Nascimento", value: new Date(student.birthdate).toLocaleDateString("pt-PT", { day: "numeric", month: "long", year: "numeric" }), icon: Calendar },
                { label: "NIF", value: student.nif, icon: Shield },
                { label: "Morada", value: student.address, icon: MapPin },
                { label: "Nacionalidade", value: student.nationality, icon: null },
              ].map(f => (
                <div key={f.label}>
                  <label className="block text-xs mb-1.5" style={{ color: "#94A3B8", fontWeight: 600 }}>{f.label}</label>
                  {editing ? (
                    <input
                      defaultValue={f.value}
                      className="w-full px-3 py-2.5 rounded-xl text-sm outline-none"
                      style={{ background: "#F8FAFC", border: "1.5px solid #BFDBFE", color: "#0F2344" }}
                    />
                  ) : (
                    <div className="flex items-center gap-2 text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>
                      {f.icon && <f.icon className="w-4 h-4 flex-shrink-0" style={{ color: "#94A3B8" }} />}
                      {f.value}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Academic */}
          <div>
            <div className="bg-white rounded-2xl p-6 mb-5" style={{ border: "1px solid #E2E8F0" }}>
              <h3 className="text-sm mb-5 uppercase tracking-wider flex items-center gap-2" style={{ color: "#94A3B8", fontWeight: 600 }}>
                <Plane className="w-4 h-4" />
                Informação Académica
              </h3>
              <div className="space-y-4">
                {[
                  { label: "Curso", value: student.course },
                  { label: "Instrutor Responsável", value: student.instructor },
                  { label: "Data de Matrícula", value: new Date(student.enrollmentDate).toLocaleDateString("pt-PT", { day: "numeric", month: "long", year: "numeric" }) },
                  { label: "Número de Aluno", value: `100${student.id}` },
                ].map(f => (
                  <div key={f.label}>
                    <div className="text-xs mb-1" style={{ color: "#94A3B8", fontWeight: 600 }}>{f.label}</div>
                    <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{f.value}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Progress Summary */}
            <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
              <h3 className="text-sm mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Progresso do Curso</h3>
              <div className="space-y-4">
                {[
                  { label: "Progresso Geral", current: student.progress, total: 100, unit: "%", color: "#1565C0" },
                  { label: "Horas de Voo", current: student.flightHours, total: 45, unit: "h", color: "#7C3AED" },
                  { label: "Horas Teóricas", current: student.theoreticalHours, total: 100, unit: "h", color: "#059669" },
                ].map(item => (
                  <div key={item.label}>
                    <div className="flex justify-between mb-1.5">
                      <span className="text-xs" style={{ color: "#64748B" }}>{item.label}</span>
                      <span className="text-xs" style={{ color: item.color, fontWeight: 700 }}>
                        {item.current}{item.unit} / {item.total}{item.unit}
                      </span>
                    </div>
                    <div className="h-2 rounded-full" style={{ background: "#E2E8F0" }}>
                      <div
                        className="h-2 rounded-full"
                        style={{
                          width: `${Math.min(100, (item.current / item.total) * 100)}%`,
                          background: item.color,
                        }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 1: Security */}
      {tab === 1 && (
        <div className="bg-white rounded-2xl p-6 max-w-lg" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-6" style={{ color: "#0F2344", fontWeight: 700 }}>
            <Lock className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
            Segurança da Conta
          </h3>
          <div className="space-y-4">
            {[
              { label: "Alterar Password", icon: Lock, desc: "Última alteração: há 3 meses" },
              { label: "Autenticação em Dois Fatores", icon: Shield, desc: "Não configurada" },
              { label: "Sessões Ativas", icon: User, desc: "1 sessão ativa (este dispositivo)" },
            ].map(item => (
              <button
                key={item.label}
                className="w-full flex items-center justify-between p-4 rounded-xl hover:bg-gray-50 transition-colors"
                style={{ border: "1.5px solid #E2E8F0" }}
              >
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 rounded-xl flex items-center justify-center" style={{ background: "#DBEAFE" }}>
                    <item.icon className="w-4 h-4" style={{ color: "#1D4ED8" }} />
                  </div>
                  <div className="text-left">
                    <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{item.label}</div>
                    <div className="text-xs" style={{ color: "#94A3B8" }}>{item.desc}</div>
                  </div>
                </div>
                <ChevronRight className="w-4 h-4" style={{ color: "#CBD5E1" }} />
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Tab 2: Notifications */}
      {tab === 2 && (
        <div className="bg-white rounded-2xl p-6 max-w-lg" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-6" style={{ color: "#0F2344", fontWeight: 700 }}>
            <Bell className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
            Preferências de Notificações
          </h3>
          <div className="space-y-4">
            {[
              { label: "Voos Agendados", desc: "Receber lembretes 24h antes", active: true },
              { label: "Avaliações", desc: "Resultados e datas de exames", active: true },
              { label: "Pagamentos", desc: "Vencimentos e confirmações", active: true },
              { label: "Novidades do Curso", desc: "Atualizações de conteúdo", active: false },
              { label: "Newsletter AeroSchool", desc: "Notícias e eventos", active: false },
            ].map((n, i) => (
              <div key={i} className="flex items-center justify-between p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0" }}>
                <div>
                  <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{n.label}</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>{n.desc}</div>
                </div>
                <div
                  className="w-11 h-6 rounded-full flex items-center cursor-pointer transition-all"
                  style={{ background: n.active ? "#1565C0" : "#E2E8F0", padding: "2px" }}
                >
                  <div
                    className="w-5 h-5 rounded-full bg-white shadow-sm transition-all"
                    style={{ transform: n.active ? "translateX(20px)" : "translateX(0)" }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
