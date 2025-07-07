// src/utils/FieldParser.ts
import type { Field } from './Field';

export class FieldParser {
  /**
   * Converts fieldName like "firstName" into "First Name"
   */
  private static humanize(fieldName: string): string {
    return fieldName
      .replace(/([A-Z])/g, ' $1') // split camelCase
      .replace(/_/g, ' ') // convert underscores
      .replace(/\s+/g, ' ') // normalize spaces
      .trim()
      .replace(/^./, (c) => c.toUpperCase()); // capitalize first letter
  }

  private static toNumber(value: string | undefined, fallback = 100): number {
  const trimmed = value?.trim() ?? '';
  const num = Number(trimmed);
  return isNaN(num) ? fallback : num;
}

static parse(raw: string): Field[] {
  const lines = raw.trim().split('\n');

  return lines
    .map((line) => line.trim())
    .filter((line) => line.length > 0)
    .map((line) => {
      const parts = line.split(',').map(p => p.trim()); // ✅ TRIM ALL PARTS

      const fieldName = parts[0] || '';
      const caption = parts[1] || this.humanize(fieldName);
      const dataType = parts[2] || 'String';

      return {
        fieldName,
        caption,
        dataType,
        references: parts[3] || '',
        mapping: parts[4] || '',
        key: parts[5] || '',
        saburiKey: parts[6] || '',
        size: this.toNumber(parts[7], 100),
        nullable: parts[8] === 'true',
        enumerated: parts[9] === 'true',
        subFields: parts[10] || '',
        projectName: parts[11] || '',
        expose: parts[12] === 'true',
        moduleName: parts[13] || '',
        select: parts[14] === 'true',
      } as Field;
    });
}

}
