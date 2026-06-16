import { ReactNode, useEffect } from 'react';

export function Modal({
  open,
  title,
  onClose,
  children,
  wide,
}: {
  open: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
  wide?: boolean;
}) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [open, onClose]);

  if (!open) return null;
  return (
    <div className="modal-backdrop" onClick={onClose} role="presentation">
      <div
        className={`modal ${wide ? 'modal-wide' : ''}`}
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
      >
        <header className="modal-header">
          <h2>{title}</h2>
          <button
            type="button"
            className="btn btn-ghost btn-sm"
            onClick={onClose}
            title="Fechar"
            aria-label="Fechar"
          >
            ×
          </button>
        </header>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
}
