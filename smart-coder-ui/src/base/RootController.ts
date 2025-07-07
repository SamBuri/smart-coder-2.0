// src/root/RootController.ts
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
// import printPDF from '@/utils/PrintPDF';
import constants from '@/utils/constants';
import rootOptions from './RootOptions';
import type HttpStrategy from './HttpStrategy';

import { defineRootStore } from './RootStore';

// export interface HttpStrategy {
//   resultHandler?: (response: any) => any;
//   errorHandler?: (error: any) => any;
// }

export interface RootModel<TModel> {
  path: string;
  rules?: any;
  model: TModel;
  httpStrategy?: () => Promise<HttpStrategy | null>;
}

export interface RootOptions {
  [key: string]: any;
}

export interface RootHooks<TModel = any> {
  afterSave?: (res: any) => void;
  save?: (model: TModel) => void;
}

export interface BaseModel {
  clear?: () => void;
  modify?: () => void;
  getFormData?: () => any;
  printOptions?: () => any;
}

export class RootController<TModel extends BaseModel> {
  protected path: string;
  protected rules?: any;

  public model = ref<TModel>();
  public options = ref<RootOptions>(rootOptions);
  public props = ref<any>(null);

  private rootStore = defineRootStore();

  public rootState = ref({
    id: '',
    valid: false,
    idValid: false,
    idRules: [(v: string) => !!v || 'Please enter the Id'],
    buttonText: constants.buttonTexts.save,
    confirmEdit: false,
    showSearch: false,
    confirmDelete: false,
    printData: false,
  });

  constructor(
    protected rawModel: RootModel<TModel>,
    rawOptions: RootOptions = rootOptions,
    protected hooks: RootHooks<TModel> = {}
  ) {
    this.path = rawModel.path;
    this.rules = rawModel.rules;
    this.model.value = rawModel.model;
    this.options.value = rawOptions;

    const route = useRoute();
    watch(
      () => route.params,
      (params) => {
        const { mode } = params as { mode?: string }; // 👈 FIX HERE
    // const numMode = Number(mode);
        // const mode = Number(params.mode);
        this.rootState.value.showSearch = mode === '1' || mode === '2';

        const text =
          mode === '1'
            ? constants.buttonTexts.update
            : mode === '2'
            ? constants.buttonTexts.print
            : mode === '3'
            ? constants.buttonTexts.done
            : constants.buttonTexts.save;

        this.setButtonText(text);
      },
      { immediate: true }
    );
  }

  public setProps(props: any) {
    this.props.value = props;
  }

  public clear(): void {
    this.model.value?.clear?.();
  }

  public setButtonText(text: string): void {
    this.rootState.value.buttonText = text;
  }

  protected async getHttpStrategy(): Promise<HttpStrategy | null> {
    const strategy = this.rawModel.httpStrategy;
    return strategy ? await strategy() : null;
  }

  protected body(): any {
    return this.model.value?.getFormData?.() ?? this.model.value;
  }

  protected async afterSave(res: any): Promise<void> {
    this.hooks.afterSave?.(res);
  }

  // public async print(): Promise<void> {
  //   const options = this.model.value?.printOptions?.();
  //   if (options) await printPDF(options);
  // }

  public async save(): Promise<void> {
    if (!this.rootState.value.valid) return;

    if (this.hooks.save) {
      this.hooks.save(this.model.value!);
      return;
    }

    this.model.value?.modify?.();

    // const strategy = await this.getHttpStrategy();
    const res = await this.rootStore.post({
      path: this.path,
      body: this.body(),
    });

    await this.afterSave(res);

    if (res.success) {
      // if (this.rootState.value.printData) await this.print();
      this.clear();
    }
  }
}
