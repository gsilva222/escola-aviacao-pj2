import { loggedStudent } from "../../data/mockData";
import { CheckCircle2, Clock, AlertCircle, CreditCard, Download } from "lucide-react";

const STATUS_MAP: Record<string, { label: string; bg: string; color: string; icon: any }> = {
  paid: { label: "Pago", bg: "#DCFCE7", color: "#16A34A", icon: CheckCircle2 },
  pending: { label: "Pendente", bg: "#FEF3C7", color: "#D97706", icon: Clock },
  overdue: { label: "Em Atraso", bg: "#FEE2E2", color: "#DC2626", icon: AlertCircle },
};

const PAYMENT_METHODS = [
  { label: "Transferência Bancária", value: "transfer", icon: "🏦" },
  { label: "MB Way", value: "mbway", icon: "📱" },
  { label: "Cartão de Débito/Crédito", value: "card", icon: "💳" },
  { label: "Multibanco (Referência)", value: "mb", icon: "🏧" },
];

export function FOPayments() {
  const payments = loggedStudent.payments;
  const totalPaid = payments.filter(p => p.status === "paid").reduce((a, p) => a + p.amount, 0);
  const totalPending = payments.filter(p => p.status === "pending").reduce((a, p) => a + p.amount, 0);
  const totalCourse = payments.reduce((a, p) => a + p.amount, 0);
  const pctPaid = Math.round((totalPaid / totalCourse) * 100);

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Summary Banner */}
      <div
        className="rounded-2xl p-6 mb-6"
        style={{ background: "linear-gradient(135deg, #0F2344, #1565C0)" }}
      >
        <div className="flex items-start justify-between flex-wrap gap-4">
          <div>
            <p className="text-blue-300 text-sm mb-1">Resumo Financeiro</p>
            <h2 className="text-white text-2xl mb-1" style={{ fontWeight: 800 }}>
              €{totalPaid.toLocaleString("pt-PT")}
              <span className="text-blue-300 text-base font-normal ml-2">pagos</span>
            </h2>
            <p className="text-blue-300 text-sm">de €{totalCourse.toLocaleString("pt-PT")} total do curso</p>
          </div>
          <div className="flex gap-3">
            {totalPending > 0 && (
              <div className="px-5 py-3 rounded-xl" style={{ background: "rgba(245,158,11,0.2)", border: "1px solid rgba(245,158,11,0.3)" }}>
                <div className="text-yellow-300 text-xl" style={{ fontWeight: 800 }}>€{totalPending.toLocaleString("pt-PT")}</div>
                <div className="text-yellow-400 text-xs">Pendente</div>
              </div>
            )}
          </div>
        </div>
        <div className="mt-4">
          <div className="flex justify-between text-xs mb-1.5" style={{ color: "#93C5FD" }}>
            <span>Progresso de pagamento</span>
            <span style={{ fontWeight: 700 }}>{pctPaid}%</span>
          </div>
          <div className="h-2.5 rounded-full" style={{ background: "rgba(255,255,255,0.15)" }}>
            <div
              className="h-2.5 rounded-full"
              style={{ width: `${pctPaid}%`, background: "linear-gradient(90deg, #60A5FA, #34D399)" }}
            />
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        {[
          { label: "Total Pago", value: `€${totalPaid.toLocaleString("pt-PT")}`, ...STATUS_MAP.paid },
          { label: "Pendente", value: `€${totalPending.toLocaleString("pt-PT")}`, ...STATUS_MAP.pending },
          { label: "Recibos Emitidos", value: payments.filter(p => p.status === "paid").length, bg: "#DBEAFE", color: "#1565C0", icon: CheckCircle2 },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="w-9 h-9 rounded-xl flex items-center justify-center mb-3" style={{ background: s.bg }}>
              <s.icon className="w-4 h-4" style={{ color: s.color }} />
            </div>
            <div className="text-xl mb-0.5" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Payments List */}
      <div className="bg-white rounded-2xl overflow-hidden mb-5" style={{ border: "1px solid #E2E8F0" }}>
        <div className="px-6 py-4 flex items-center justify-between" style={{ borderBottom: "1px solid #E2E8F0" }}>
          <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Histórico de Pagamentos</h3>
          <button className="flex items-center gap-2 px-3 py-2 rounded-xl text-xs hover:bg-gray-50 transition-colors" style={{ border: "1.5px solid #E2E8F0", color: "#374151" }}>
            <Download className="w-3.5 h-3.5" />
            Exportar extrato
          </button>
        </div>
        <div className="divide-y" style={{ divideColor: "#F1F5F9" }}>
          {payments.map((p, i) => {
            const st = STATUS_MAP[p.status];
            const StatusIcon = st.icon;
            return (
              <div
                key={p.id}
                className="flex items-center gap-4 px-6 py-4"
                style={{ borderBottom: i < payments.length - 1 ? "1px solid #F1F5F9" : undefined }}
              >
                <div className="w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: st.bg }}>
                  <StatusIcon className="w-5 h-5" style={{ color: st.color }} />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-sm mb-0.5" style={{ color: "#0F2344", fontWeight: 600 }}>{p.description}</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>
                    Vencimento: {new Date(p.dueDate).toLocaleDateString("pt-PT")}
                    {p.paidDate && ` · Pago em: ${new Date(p.paidDate).toLocaleDateString("pt-PT")}`}
                  </div>
                </div>
                <div className="flex items-center gap-4 flex-shrink-0">
                  <span className="text-lg" style={{ color: "#0F2344", fontWeight: 800 }}>€{p.amount.toLocaleString("pt-PT")}</span>
                  <span className="flex items-center gap-1 px-2.5 py-1 rounded-full text-xs" style={{ background: st.bg, color: st.color, fontWeight: 600 }}>
                    <StatusIcon className="w-3 h-3" />
                    {st.label}
                  </span>
                  {p.status === "paid" && (
                    <button className="p-1.5 rounded-lg hover:bg-gray-50 transition-colors">
                      <Download className="w-4 h-4" style={{ color: "#94A3B8" }} />
                    </button>
                  )}
                  {p.status === "pending" && (
                    <button
                      className="px-4 py-2 rounded-xl text-xs text-white"
                      style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 600 }}
                    >
                      Pagar agora
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Payment Methods */}
      <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
        <h3 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>
          <CreditCard className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
          Métodos de Pagamento Disponíveis
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {PAYMENT_METHODS.map(m => (
            <div key={m.value} className="p-4 rounded-xl text-center" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0" }}>
              <div className="text-2xl mb-2">{m.icon}</div>
              <div className="text-xs" style={{ color: "#374151", fontWeight: 600 }}>{m.label}</div>
            </div>
          ))}
        </div>
        <p className="text-xs mt-4" style={{ color: "#94A3B8", lineHeight: 1.6 }}>
          Para pagamentos por transferência bancária, utilize o IBAN PT50 0035 0001 0000 0000 00000 e indique o seu número de aluno na referência.
          Para mais informações contacte a secretaria: <strong style={{ color: "#1565C0" }}>secretaria@aeroschool.pt</strong>
        </p>
      </div>
    </div>
  );
}
