
interface SwitchProps {
  checked: boolean;
  disabled?: boolean;
  onChange: () => void;
  activeColor?: string;
}

export function Switch({ checked, disabled = false, onChange, activeColor = "#34c759" }: SwitchProps) {
  return (
    <label style={{
      position: "relative",
      display: "inline-block",
      width: 44,
      height: 24,
      cursor: disabled ? "not-allowed" : "pointer"
    }}>
      <input
        type="checkbox"
        checked={checked}
        disabled={disabled}
        onChange={onChange}
        style={{ opacity: 0, width: 0, height: 0 }}
      />
      <span style={{
        position: "absolute",
        top: 0, left: 0, right: 0, bottom: 0,
        backgroundColor: checked && !disabled ? activeColor : "#e5e5ea",
        transition: "0.2s",
        borderRadius: 24
      }}>
        <span style={{
          position: "absolute",
          height: 20,
          width: 20,
          left: checked && !disabled ? 22 : 2,
          bottom: 2,
          backgroundColor: "white",
          transition: "0.2s",
          borderRadius: "50%",
          boxShadow: "0 1px 3px rgba(0,0,0,0.2)"
        }} />
      </span>
    </label>
  );
}