// use tauri_plugin_shell::process::CommandEvent;
// use tauri_plugin_shell::ShellExt;
//
// #[cfg_attr(mobile, tauri::mobile_entry_point)]
// #[cfg_attr(mobile, tauri::mobile_entry_point)]
// pub fn run() {
//     tauri::Builder::default()
//         .plugin(tauri_plugin_fs::init())
//         .plugin(tauri_plugin_dialog::init())
//         .plugin(tauri_plugin_shell::init())
//         // 1. Move the Log plugin here, BEFORE setup
//         .plugin(
//             tauri_plugin_log::Builder::default()
//                 .level(log::LevelFilter::Info)
//                 .build(),
//         )
//         .setup(|app| {
//              #[cfg(debug_assertions)]
//              {
//                  // In Dev Mode, we do nothing.
//                  // We assume you have started Spring Boot manually in your IDE.
//                  println!("Running in Dev mode: Sidecar will NOT be started by Tauri.");
//              }
//
//              #[cfg(not(debug_assertions))]
//              {
//                  // In Release Mode (Production), spawn the sidecar automatically.
//                  let sidecar_command = app.shell().sidecar("backend").unwrap();
//                  let (mut rx, _child) = sidecar_command
//                      .spawn()
//                      .expect("Failed to spawn native backend sidecar");
//
//                  tauri::async_runtime::spawn(async move {
//                      while let Some(event) = rx.recv().await {
//                          if let tauri_plugin_shell::process::CommandEvent::Stdout(line_bytes) = event {
//                              println!("Backend Output: {}", String::from_utf8_lossy(&line_bytes));
//                          }
//                      }
//                  });
//              }
//
//              Ok(())
//          })
//
//         .run(tauri::generate_context!())
//         .expect("error while running tauri application");
// }
//

// 1. Move imports behind the same guard as the logic
// Only import these when NOT in debug mode (Release/Production)
#[cfg(not(debug_assertions))]
use std::sync::Mutex;
#[cfg(not(debug_assertions))]
use tauri::Manager;
#[cfg(not(debug_assertions))]
use tauri_plugin_shell::{process::{CommandChild, CommandEvent}, ShellExt};

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_fs::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_shell::init())
        .plugin(
            tauri_plugin_log::Builder::default()
                .level(log::LevelFilter::Info)
                .build(),
        )
        // Using _app tells Rust we acknowledge it might be unused in Dev
        .setup(|_app| {
            #[cfg(not(debug_assertions))]
            {
                let sidecar_command = _app.shell().sidecar("backend").unwrap();
                let (mut rx, child) = sidecar_command
                    .spawn()
                    .expect("Failed to spawn native backend sidecar");

                // Save the child in managed state to kill it on exit
                _app.manage(Mutex::new(Some(child)));

                tauri::async_runtime::spawn(async move {
                    while let Some(event) = rx.recv().await {
                        if let CommandEvent::Stdout(line_bytes) = event {
                            println!("Backend: {}", String::from_utf8_lossy(&line_bytes));
                        }
                    }
                });
            }
            Ok(())
        })
        .build(tauri::generate_context!())
        .expect("error while building tauri application")
        // Using _app_handle silences the unused variable warning in Dev
        .run(|_app_handle, event| match event {
            tauri::RunEvent::ExitRequested { .. } => {
                #[cfg(not(debug_assertions))]
                {
                    if let Some(child_mutex) = _app_handle.try_state::<Mutex<Option<CommandChild>>>() {
                        if let Ok(mut guard) = child_mutex.lock() {
                            if let Some(child) = guard.take() {
                                let _ = child.kill();
                            }
                        }
                    }
                }
            }
            _ => {}
        });
}
