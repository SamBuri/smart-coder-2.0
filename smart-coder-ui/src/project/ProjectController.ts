// src/controllers/ProjectController.ts
import { CrudController } from '@/base/RootController.ts'
import { createProjectModel, type Project } from './ProjectModel.ts'
import {defineLookupStore} from "@/lookup/lookupStore.ts";
import {defineProjectStore} from "@/project/projectStore.ts";
import {watch} from "vue";

export class ProjectController extends CrudController<Project> {


  public lookupStore:any;
  public projectStore: any;


  constructor() {
    super(createProjectModel(), '/projects', {
      warningMsg: 'Are you sure you want to save these changes?',
      showPrintPrompt: false,
    })
    this.lookupStore = defineLookupStore();
    this.lookupStore.getProjectTypes();
    this.lookupStore.getLanguages();
    this.lookupStore.getJavaProjectTypes();
    this.lookupStore.getJavaProjectTypes();
    this.projectStore = defineProjectStore();
    this.projectStore.getMini();
  }




  // Optional: extra validation
  protected async beforeSave(): Promise<boolean> {
    if (!this.model.value.baseFolder) {
      this.rootStore.error('Project folder is required')
      return false
    }
    if (!this.model.value.basePackage) {
      this.rootStore.error('Base package is required')
      return false
    }
    return true
  }
}

// Singleton factory (Vue loves this pattern)
let _instance: ProjectController
export const useProjectController = () => {
  if (!_instance) _instance = new ProjectController()

  return _instance
}
