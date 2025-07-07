interface RootState {
  valid: string;
  idValid: boolean;
  idRules: Array<(v: string) => string | boolean>;
  form: any | null;  // Replace 'any' with a more specific type if possible
  idForm: any | null; // Replace 'any' with a more specific type if possible
  confirmEdit: boolean;
}

const rootState: RootState = {
  valid: "",
  idValid: false,
  idRules: [(v: string) => !!v || "Please enter the Id"],
  form: null,
  idForm: null,
  confirmEdit: false,
};

export default rootState;