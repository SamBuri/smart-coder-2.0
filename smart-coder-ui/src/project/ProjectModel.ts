// src/models/ProjectModel.ts
import type { BaseModel } from '@/base/RootController.ts'


export interface Project extends BaseModel {
  id?: string
  displayName: string
  parent?: any
  projectName?: string
  basePackage: string
  baseFolder: string
  mainSourceFolder?: string
  testFolder?: string
  resourceFolder?: string
  frontendFolder?: string | null
  projectType: string
  language: string
  conventionVersion?: string
  gitRemoteUrl?: string | null
  notes?: string | null
  dryRunByDefault?: boolean
}

export const createProjectModel = (): Project => ({
  displayName: '',
  basePackage: '',
  baseFolder: '',
  language: '',
  projectType: '',
  parent: null,
  frontendFolder: null,
  gitRemoteUrl: null,
  notes: null,
  dryRunByDefault: true,


  clear(){
    Object.assign(this, createProjectModel())
    this.id = undefined
  },

  copy(data: Partial<Project>) {
    Object.assign(this, data)
  },
})
