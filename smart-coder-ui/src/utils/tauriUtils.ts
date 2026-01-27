export interface FileResponse {
  fullFileName: string;
  shortName: string;
  content: string;
}

export interface SaveFilesResult {
  saved: string[];
  skipped: string[];
  failed: { file: string; reason: string }[];
}

// src/utils/fileWriter.ts
import { writeTextFile, exists, mkdir } from "@tauri-apps/plugin-fs";
import { confirm } from "@tauri-apps/plugin-dialog";
import { dirname } from "@tauri-apps/api/path";

export async function saveGeneratedFiles(files: FileResponse[]): Promise<SaveFilesResult> {
  const result: SaveFilesResult = { saved: [], skipped: [], failed: [] };

  for (const file of files) {
    try {
      const filePath = file.fullFileName;
      const fileExists = await exists(filePath);

      if (fileExists) {
        const overwrite = await confirm(
          `The file "${file.shortName}" already exists.\n\nDo you want to overwrite it?`,
          { title: "File already exists", kind: "warning" }
        );

        if (!overwrite) {
          result.skipped.push(file.shortName);
          continue;
        }
      }

      // 🔧 Fix: Explicitly create the parent directory if it doesn't exist
      const parentDir = await dirname(filePath);
      await mkdir(parentDir, { recursive: true });

      await writeTextFile(filePath, file.content);
      result.saved.push(file.shortName);

    } catch (error) {
      result.failed.push({
        file: file.shortName,
        reason: error instanceof Error ? error.message : String(error)
      });
    }
  }
  return result;
}


