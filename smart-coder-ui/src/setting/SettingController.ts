import { CrudController } from '@/base/RootController.ts'
import {createSettingsModel, type Settings} from "@/setting/SettingsModel.ts";
import constants from "@/utils/constants.ts";
export class SettingsController extends CrudController<Settings> {


  constructor() {
    super(createSettingsModel(), '/settings', {
      warningMsg: 'Are you sure you want to continue?',
      showPrintPrompt: false,
    })
  }


  // clear() {
  //  console.log("Do not clear on save");
  // }

  // async save(): Promise<any> {
  //   return super.save();
  // }


  isFormValid() {
    return true;
  }

  async saveDefaultLocationSettings(settingModel : Settings, value:string): Promise<any> {
   settingModel.property=constants.settingProperties.outputFolder
    settingModel.propertyValue=value;
   return await super.save()


  }
}

// Singleton factory (Vue loves this pattern)
let _instance: SettingsController
export const useSettingsController = () => {
  if (!_instance) _instance = new SettingsController()

  return _instance
}
