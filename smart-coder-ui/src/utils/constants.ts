interface ButtonTexts {
  save: string;
  update: string;
  done: string;
  print: string;
}
interface ProjectType {
  springBoot: string;
  vue: string,
  javaFx: string;
}

type PasswordRule = (v: string) => string | boolean;

const constants = {
  buttonTexts: {
    save: "Save",
    update: "Update",
    done: "Done",
    print: "Print"
  } as ButtonTexts,

  PASSWORD_RULES: [
    (v: string) => !!v || 'Password is required',
    // Minimum length: 8
    (v: string) => (v && v.length >= 8) || 'Password must be at least 8 characters',
    // At least 1 uppercase letter
    (v: string) => /[A-Z]/.test(v) || 'Must contain at least 1 uppercase letter',
    // At least 1 lowercase letter
    (v: string) => /[a-z]/.test(v) || 'Must contain at least 1 lowercase letter',
    // At least 1 digit
    (v: string) => /\d/.test(v) || 'Must contain at least 1 number',
    // At least 1 special character
    (v: string) => /[!@#$%^&*(),.?":{}|<>]/.test(v) || 'Must contain at least 1 special character',
  ] as PasswordRule[],

  projectTypes: {
    springBoot: "Springboot",
    vue: "Vue",
    javaFx: "JavaFX"

  } as ProjectType
};

export default constants;
