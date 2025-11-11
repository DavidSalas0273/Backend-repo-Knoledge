#!/usr/bin/env node

const { spawn } = require("child_process");
const path = require("path");

const args = process.argv.slice(2);
const isWindows = process.platform === "win32";
const mvnwExecutable = isWindows ? "mvnw.cmd" : "./mvnw";

const child = spawn(mvnwExecutable, args, {
  cwd: path.resolve(__dirname, ".."),
  stdio: "inherit",
  shell: isWindows,
});

child.on("error", (error) => {
  console.error("Error al ejecutar Maven Wrapper:", error);
  process.exit(1);
});

child.on("exit", (code) => {
  process.exit(code ?? 0);
});
