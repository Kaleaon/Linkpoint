import AccessibleAvatar, { initialsOf as initialsOfHelper } from "./AccessibleAvatar.jsx";

export const initialsOf = initialsOfHelper;

// The photo the user chose for a contact, or plain initials when there is none.
// Delegated to AccessibleAvatar primitive.
export default function ContactAvatar({
  name,
  photo = undefined,
  size = 40,
  label = undefined,
  decorative = true,
  ...rest
}) {
  return (
    <AccessibleAvatar
      name={name}
      photo={photo}
      size={size}
      label={label}
      decorative={decorative}
      {...rest}
    />
  );
}
