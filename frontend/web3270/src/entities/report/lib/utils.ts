/**
 * Converts a string to its hexadecimal representation.
 * Each character is converted to its char code in hex, padded to 2 digits.
 * The resulting hex pairs are separated by spaces.
 */
export function toHex(str: string): string {
  return Array.from(str)
    .map(c => c.charCodeAt(0).toString(16).toUpperCase().padStart(2, "0"))
    .join(" ");
}

/**
 * Truncates a string if it exceeds the specified maximum length.
 * Appends "…" if truncated.
 */
export function truncate(str: string, max: number = 22): string {
  return str.length > max ? str.slice(0, max) + "…" : str;
}
