import { CrudController } from '@/base/RootController.ts'
import { createGeneratorModel, type Generator } from './GeneratorModel.ts'
import { saveGeneratedFiles } from '@/utils/tauriUtils.ts'

export class GeneratorController extends CrudController<Generator> {


  constructor() {
    super(createGeneratorModel(), '/generate', {
      warningMsg: 'Are you sure you want to generate code?',
      showPrintPrompt: false,
    })
  }


  clear() {
   console.log("Do not clear on save");
  }

  // async save(): Promise<any> {
  //   return super.save();
  // }


  async save(): Promise<any> {
    // 1️⃣ Call existing backend logic
    const res = await super.save()

    if (!res?.success) {
      return res
    }

    // 2️⃣ Extract generated files
    const files = res.data?.fileResponses

    if (!files || !Array.isArray(files) || files.length === 0) {
      console.warn('No files returned by generator')
      return res
    }

    // 3️⃣ Save files locally via Tauri
    const saveResult = await saveGeneratedFiles(files)

    // 4️⃣ Optional: user feedback / logging
    console.log('Files saved:', saveResult.saved)
    console.log('Files skipped:', saveResult.skipped)
    console.log('Files failed:', saveResult.failed)

    if (saveResult.failed.length > 0) {
      // this.rootStore.warning(
      //   `${saveResult.failed.length} file(s) failed to save. Check logs.`
      // )
    }

    return res
  }
}

// Singleton factory (Vue loves this pattern)
let _instance: GeneratorController
export const useGeneratorController = () => {
  if (!_instance) _instance = new GeneratorController()

  return _instance
}
