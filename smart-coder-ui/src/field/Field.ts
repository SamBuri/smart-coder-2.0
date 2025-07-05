
// src/models/Field.ts

export interface Field {
  fieldName: string;
  caption: string;
  dataType: string;
  references: string;
  mapping: string;
  key: string;
  saburiKey: string;
  size: number;
  nullable: boolean;
  enumerated: boolean;
  subFields: string;
  projectName: string;
  expose: boolean;
  moduleName: string;
  select: boolean;
}

// ✅ Helper function to create a default field
export function createDefaultField(): Field {
  return {
    fieldName: '',
    caption: '',
    dataType: '',
    references: '',
    mapping: '',
    key: '',
    saburiKey: '',
    size: 100,
    nullable: false,
    enumerated: false,
    subFields: '',
    projectName: '',
    expose: false,
    moduleName: '',
    select: false,
  };
}
