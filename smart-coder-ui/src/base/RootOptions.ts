interface RootOptions {
  maxWidth: number;
  warn: boolean;
  warningMsg: string;
  showPrintPrompt: boolean;
  mode: number;
  showDelete: boolean;
}

const rootOptions: RootOptions = {
  maxWidth: 900,
  warn: true,
  warningMsg: 'Confirm Action',
  showPrintPrompt: false,
  mode: 0,
  showDelete: false
};

export default rootOptions;