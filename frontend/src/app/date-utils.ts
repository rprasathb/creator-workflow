/** Convert the value from a local datetime-local input into an ISO UTC instant. */
export function toIso(localDateTime: string): string | null {
  if (!localDateTime) return null;
  const date = new Date(localDateTime);
  return Number.isNaN(date.getTime()) ? null : date.toISOString();
}

/** Convert a UTC instant to a local datetime-local input value. */
export function toLocalInput(instant: string | null): string {
  if (!instant) return '';
  const date = new Date(instant);
  if (Number.isNaN(date.getTime())) return '';
  const offsetMs = date.getTimezoneOffset() * 60_000;
  return new Date(date.getTime() - offsetMs).toISOString().slice(0, 16);
}
