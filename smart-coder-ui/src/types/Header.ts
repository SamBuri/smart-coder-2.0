
export interface Header {
  key: String;
  title: string;
  type: 'text' | 'select' | 'checkbox';
  namedValues?: string[];
}