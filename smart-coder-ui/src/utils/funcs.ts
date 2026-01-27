/**
 * Converts a string to a human-readable caption.
 * Examples:
 *   "user_profile"        → "User Profile"
 *   "my-awesome-file"     → "My Awesome File"
 *   "helloWorld"          → "Hello World"
 *   "API_KEY"             → "Api Key"
 *   "fileName.txt"        → "File Name" (if you pass without extension)
 *
 * @param str - The input string
 * @returns Capitalized, spaced string
 */
export function toCaption(str: string): string {
  if (!str) return '';

  return str
    .trim()
    // Insert space before capital letters (camelCase → camel Case)
    .replace(/([A-Z])/g, ' $1')
    // Replace underscores, hyphens, and multiple spaces with single space
    .replace(/[_-]+/g, ' ')
    .replace(/\s+/g, ' ')
    // Capitalize first letter of each word
    .replace(/\b\w/g, (char) => char.toUpperCase());
}

/**
 * Converts filename (with or without extension) to a clean object name.
 * Removes extension and sanitizes for use as identifier.
 *
 * @param filename - e.g. "my_report.csv" or "UserData.JSON"
 * @returns Clean name without extension: "my_report"
 */
export function filenameToObjectName(filename: string): string {
  if (!filename) return '';

  // Remove extension (everything after last dot)
  return filename.replace(/\.[^/.]+$/, '');
}

/**
 * Full helper: filename → objectName → human-readable caption
 *
 * @param filename - The original filename
 * @returns Human-readable caption
 */
export function filenameToCaption(filename: string): string {
  if (!filename) return '';

  const nameWithoutExt = filenameToObjectName(filename);
  return toCaption(nameWithoutExt);
}

/**
 * Slugify: converts a string to URL-friendly slug
 * "My Awesome File!" → "my-awesome-file"
 */
export function toSlug(str: string): string {
  if (!str) return '';

  return str
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9 -]/g, '') // Remove invalid chars
    .replace(/\s+/g, '-')        // Replace spaces with -
    .replace(/-+/g, '-');        // Replace multiple - with single -
}

/**
 * Truncate string with ellipsis
 */
export function truncate(str: string, length: number = 50): string {
  if (!str || str.length <= length) return str;
  return str.slice(0, length - 3) + '...';
}
