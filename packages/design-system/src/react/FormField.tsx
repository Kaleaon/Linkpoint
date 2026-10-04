import React, { createContext, forwardRef, useContext, useId, useState } from "react";
import { useTheme } from "./LayoutContext.js";

export interface FormFieldContextValue {
  id: string;
  labelId: string;
  descId: string | undefined;
  errorId: string | undefined;
  ariaDescribedBy: string | undefined;
  ariaInvalid: boolean;
  ariaErrorMessage: string | undefined;
}

export const FormFieldContext = createContext<FormFieldContextValue | null>(null);

let fallbackIdCounter = 0;

export interface FormFieldProps extends React.HTMLAttributes<HTMLDivElement> {
  id?: string;
  label?: React.ReactNode;
  description?: React.ReactNode;
  helperText?: React.ReactNode;
  error?: React.ReactNode;
  required?: boolean;
  children?: React.ReactNode;
  style?: React.CSSProperties;
  className?: string;
  labelStyle?: React.CSSProperties;
}

export const FormField: React.FC<FormFieldProps> = ({
  id: explicitId,
  label,
  description,
  helperText,
  error,
  required = false,
  children,
  style,
  className = "",
  labelStyle,
  ...props
}) => {
  let V: Record<string, string> = {};
  let font = "sans-serif";
  try {
    const theme = useTheme();
    if (theme) {
      V = (theme.v as unknown as Record<string, string>) || {};
      font = theme.font || "sans-serif";
    }
  } catch (e) {
    // Fallback if rendered outside ThemeProvider
  }

  const reactId = typeof useId === "function" ? useId() : null;
  const [fallbackId] = useState(() => {
    fallbackIdCounter += 1;
    return `form-field-${fallbackIdCounter}`;
  });

  const inputId = explicitId || (reactId ? `field-${reactId.replace(/:/g, "")}` : fallbackId);
  const descText = description || helperText;

  const labelId = `${inputId}-label`;
  const descId = descText ? `${inputId}-desc` : undefined;
  const errorId = error ? `${inputId}-error` : undefined;

  const describedByParts: string[] = [];
  if (descId) describedByParts.push(descId);
  if (errorId) describedByParts.push(errorId);
  const ariaDescribedBy = describedByParts.length > 0 ? describedByParts.join(" ") : undefined;

  const contextValue: FormFieldContextValue = {
    id: inputId,
    labelId,
    descId,
    errorId,
    ariaDescribedBy,
    ariaInvalid: Boolean(error),
    ariaErrorMessage: errorId || undefined,
  };

  const renderedChildren = React.Children.map(children, (child) => {
    if (!React.isValidElement<Record<string, any>>(child)) return child;
    const childProps = child.props as Record<string, any>;
    return React.cloneElement(child, {
      id: childProps.id || inputId,
      "aria-describedby": childProps["aria-describedby"] || ariaDescribedBy,
      "aria-invalid": childProps["aria-invalid"] !== undefined ? childProps["aria-invalid"] : (error ? true : undefined),
      "aria-errormessage": childProps["aria-errormessage"] || (error ? errorId : undefined),
    } as any);
  });

  return (
    <FormFieldContext.Provider value={contextValue}>
      <div
        className={`form-field ${className}`.trim()}
        style={{ display: "flex", flexDirection: "column", gap: "2px", ...style }}
        {...props}
      >
        {label && (
          <label
            id={labelId}
            htmlFor={inputId}
            style={{
              font: `400 10px/1 ${font}`,
              letterSpacing: ".2em",
              color: V.pri || "#818cf8",
              margin: "4px 0 2px",
              cursor: "pointer",
              ...labelStyle,
            }}
          >
            {label}
            {required && <span aria-hidden="true" style={{ color: V.err || "#ef4444", marginLeft: "2px" }}> *</span>}
          </label>
        )}

        {renderedChildren}

        {descText && (
          <div
            id={descId}
            style={{
              font: `400 10px/1.3 ${font}`,
              color: V.ink2 || "#9ca3af",
              marginTop: "2px",
            }}
          >
            {descText}
          </div>
        )}

        {error && (
          <div
            id={errorId}
            style={{
              font: `400 10px/1.3 ${font}`,
              color: V.err || "#ef4444",
              marginTop: "2px",
            }}
          >
            {`> ${error}`}
          </div>
        )}
      </div>
    </FormFieldContext.Provider>
  );
};

export interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  id?: string;
  "aria-describedby"?: string;
  "aria-invalid"?: boolean | "false" | "true" | "grammar" | "spelling";
  "aria-errormessage"?: string;
  style?: React.CSSProperties;
  className?: string;
}

export const FormInput = forwardRef<HTMLInputElement, FormInputProps>(function FormInput(
  {
    id: idProp,
    "aria-describedby": ariaDescribedByProp,
    "aria-invalid": ariaInvalidProp,
    "aria-errormessage": ariaErrorMessageProp,
    style,
    className = "",
    ...props
  },
  ref
) {
  const context = useContext(FormFieldContext);

  const id = idProp || context?.id;
  const ariaDescribedBy = ariaDescribedByProp || context?.ariaDescribedBy;
  const ariaInvalid = ariaInvalidProp !== undefined ? ariaInvalidProp : context?.ariaInvalid;
  const ariaErrorMessage = ariaErrorMessageProp || context?.ariaErrorMessage;

  return (
    <input
      ref={ref}
      id={id}
      aria-describedby={ariaDescribedBy}
      aria-invalid={ariaInvalid}
      aria-errormessage={ariaErrorMessage}
      className={`form-input ${className}`.trim()}
      style={style}
      {...props}
    />
  );
});

export interface FormSwitchProps extends Omit<React.HTMLAttributes<HTMLSpanElement>, "onChange"> {
  checked?: boolean;
  on?: boolean;
  onChange?: (checked: boolean) => void;
  disabled?: boolean;
  id?: string;
  "aria-describedby"?: string;
  "aria-invalid"?: boolean | "false" | "true" | "grammar" | "spelling";
  "aria-errormessage"?: string;
  "aria-label"?: string;
  "aria-labelledby"?: string;
  style?: React.CSSProperties;
  className?: string;
}

export const FormSwitch = forwardRef<HTMLSpanElement, FormSwitchProps>(function FormSwitch(
  {
    checked,
    on,
    onChange,
    onClick,
    disabled = false,
    id: idProp,
    "aria-describedby": ariaDescribedByProp,
    "aria-invalid": ariaInvalidProp,
    "aria-errormessage": ariaErrorMessageProp,
    "aria-label": ariaLabel,
    "aria-labelledby": ariaLabelledBy,
    style,
    className = "",
    ...props
  },
  ref
) {
  const context = useContext(FormFieldContext);

  let V: Record<string, string> = {};
  try {
    const theme = useTheme();
    if (theme) {
      V = (theme.v as unknown as Record<string, string>) || {};
    }
  } catch (e) {
    // Fallback if rendered outside ThemeProvider
  }

  const isOn = checked !== undefined ? checked : Boolean(on);

  const handleChange = (e?: React.SyntheticEvent) => {
    if (disabled) return;
    if (onChange) {
      onChange(!isOn);
    }
    if (onClick) {
      onClick(e as React.MouseEvent<HTMLSpanElement>);
    }
  };

  const id = idProp || context?.id;
  const ariaDescribedBy = ariaDescribedByProp || context?.ariaDescribedBy;
  const ariaInvalid = ariaInvalidProp !== undefined ? ariaInvalidProp : context?.ariaInvalid;
  const ariaErrorMessage = ariaErrorMessageProp || context?.ariaErrorMessage;

  const defaultTrackBg = isOn === false ? (V.surf2 || "#2a2d3a") : (V.priC || "#4f46e5");
  const defaultKnobBg = isOn === false ? (V.ink2 || "#9ca3af") : (V.pri || "#818cf8");

  return (
    <span
      ref={ref}
      id={id}
      role="switch"
      aria-checked={Boolean(isOn)}
      tabIndex={disabled ? -1 : 0}
      onClick={(e) => handleChange(e)}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          handleChange(e);
        }
        if (props.onKeyDown) {
          props.onKeyDown(e);
        }
      }}
      aria-describedby={ariaDescribedBy}
      aria-invalid={ariaInvalid}
      aria-errormessage={ariaErrorMessage}
      aria-label={ariaLabel}
      aria-labelledby={ariaLabelledBy || context?.labelId}
      className={`form-switch ${className}`.trim()}
      style={{
        width: "42px",
        height: "24px",
        borderRadius: "12px",
        position: "relative",
        display: "inline-block",
        flex: "none",
        cursor: disabled ? "not-allowed" : "pointer",
        opacity: disabled ? 0.5 : 1,
        background: defaultTrackBg,
        transition: "background-color .18s ease",
        ...style,
      }}
      {...props}
    >
      <span
        style={{
          position: "absolute",
          top: "3px",
          width: "18px",
          height: "18px",
          borderRadius: "9px",
          left: isOn === false ? "3px" : "21px",
          background: defaultKnobBg,
          transition: "left .18s ease",
        }}
      />
    </span>
  );
});

export default FormField;
