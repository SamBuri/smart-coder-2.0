// src/models/ProjectModel.ts
import type { BaseModel } from '@/base/RootController.ts'
import type {Field} from "@/field/Field.ts";


export interface Settings extends BaseModel {
  id?: string
 property: string
  propertyValue?: any
}

export const createSettingsModel = (): Settings => ({
  property: '',
  propertyValue: '',

  clear(){
    Object.assign(this, createSettingsModel())
    this.id = undefined
  },

  copy(data: Partial<Generator>) {
    Object.assign(this, data)
  },
})
