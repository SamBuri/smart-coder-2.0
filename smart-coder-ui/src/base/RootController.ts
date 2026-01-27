// src/controllers/base/CrudController.ts
import { ref, computed, type Ref } from 'vue'
import { defineRootStore } from '@/base/RootStore'
import type { RootStore } from '@/base/RootStore'
import type { Results, Request } from '@/base/RootStore'
import constants from '@/utils/constants'
import {open} from "@tauri-apps/plugin-dialog";

export interface BaseModel {
  id?: string | number
  clear: () => void
  copy: (data: any) => void
  modify?: () => void
  modifyToUpdate?: () => void
  getFormData?: () => any
  printOptions?: () => any
}

export interface CrudControllerOptions {
  showPrintPrompt?: boolean
  warningMsg?: string
}

export class CrudController<T extends BaseModel> {
  protected rootStore: RootStore
  public model: Ref<T>
  protected path: string
  protected options: CrudControllerOptions

  rootState = ref({
    id: '',
    valid: false,
    idValid: false,
    idRules: [(v: any) => !!v || 'Please enter the Id'],
    buttonText: constants.buttonTexts.save,
    confirmEdit: false,
    showSearch: false,
    confirmDelete: false,
    printData: false,
  })

  constructor(model: T, path: string, options: CrudControllerOptions = {}) {
    this.rootStore = defineRootStore()
    this.model = ref(model) as Ref<T>
    this.path = path
    this.options = options

    // CRITICAL: Bind all methods that use `this` in async contexts
    this.browseFolder = this.browseFolder.bind(this)
    // this.isFormValid= this.isFormValid.bind(this)
    this.save = this.save.bind(this)
    this.update = this.update.bind(this)
    this.editClicked = this.editClicked.bind(this)
    this.editConfirmOk = this.editConfirmOk.bind(this)

  }

  get isSave() { return computed(() => this.rootState.value.buttonText === constants.buttonTexts.save) }
  get isUpdate() { return computed(() => this.rootState.value.buttonText === constants.buttonTexts.update) }
  get isPreview() { return computed(() => this.rootState.value.buttonText === constants.buttonTexts.print) }

  clear() { this.model.value.clear() }
  setData(data: any) { this.model.value.copy(data) }

  saveRequest(){
    return {
      path: this.path,
      body: this.model.value.getFormData?.() ?? this.model.value,
    };
  }

  isFormValid(){
   return this.rootState.value.valid;
  }

  async save() {
    if (!this.rootState.value.valid) return
    this.model?.value?.modify?.();

    const res = await this.rootStore.post(this.saveRequest())
    if (res.success) {
      this.rootState.value.printData && this.print()
      this.clear()
    }
    return res
  }

  async update() {
    if (!this.rootState.value.valid || !this.model.value.id) return
    this.model.value.modify?.()
    this.model.value.modifyToUpdate?.()

    const req: Request = {
      path: `${this.path}/${this.model.value.id}`,
      body: this.model.value.getFormData?.() ?? this.model.value,
    }

    return await this.rootStore.put(req)
  }

  async getData(){
    return this.rootStore.getData(this.path)
  }

  editClicked() {
    if (!this.rootState.value.valid) return
    if (this.rootState.value.buttonText === constants.buttonTexts.print) {
      this.print()
    } else {
      this.rootState.value.confirmEdit = true
    }
  }

  editConfirmOk() {
    this.isUpdate.value ? this.update() : this.save()
    this.rootState.value.confirmEdit = false
  }

  cancelEdit() {
    this.rootState.value.confirmEdit = false
  }

  print() {
    console.log('Print:', this.model.value.printOptions?.())
  }

  setButtonText(buttonLabel: string){
    this.rootState.value.buttonText = buttonLabel;
  }

  // Native method — now safe
  async browseFolder() {
    try {
      // Official Tauri v2 dialog — no custom Rust needed
      const selected = await open({
        directory: true,    // Folder only (not file)
        multiple: false,    // Single selection
        title: 'Select Project Root Folder',  // Custom title
      })

      if (selected) {
        // selected is a string path (or null if cancelled)
        // this.model.propertyValue.baseFolder = selected as string
        console.log('Selected folder:', selected)
      }
    } catch (err: any) {
      console.error('Folder selection failed:', err)
      this.rootStore.error('Failed to select folder: ' + (err.message || 'Unknown error'))
    }
  }

  protected async beforeSave(): Promise<boolean> { return true }
}
