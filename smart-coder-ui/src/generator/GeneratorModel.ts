// src/models/ProjectModel.ts
import type { BaseModel } from '@/base/RootController.ts'
import type {Field} from "@/field/Field.ts";


export interface Generator extends BaseModel {
  id?: string
  projectType: string
  projectId?: any
  objectName?: string
  objectCaption: string
  entityType: string
  serviceType?: string
  moduleName: string
  files: Array<String>
  fields: Array<Field>
  outputDirectory: string
  saveToProject: boolean,
  openFile: boolean,
}

export const createGeneratorModel = (): Generator => ({
  projectType: '',
  projectId: '',
  objectName: '',
  objectCaption: '',
  entityType: '',
  serviceType: '',
  moduleName:'',
  files: [],
  fields: [],
  outputDirectory: '',
  saveToProject: false,
  openFile: false,
  clear(){
    Object.assign(this, createGeneratorModel())
    this.id = undefined
  },

  copy(data: Partial<Generator>) {
    Object.assign(this, data)
  },
})
