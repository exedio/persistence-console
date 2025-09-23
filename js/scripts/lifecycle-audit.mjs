/* eslint-disable no-console,no-undef */
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

// @ts-expect-error no types provided
import AdmZip from "adm-zip";

/**
 * @typedef DependencyMeta
 * @type {{
 *    built?: boolean
 *  }}
 */
/**
 * @typedef DependenciesMeta
 * @type {{ [pkgAndVersion: string]: DependencyMeta }}
 */
/**
 * @typedef Package
 * @type {{
 *  name: string;
 *  version: string;
 *  scripts?: {
 *    [s: string]: string;
 *  };
 *  dependenciesMeta?: DependenciesMeta
 *  }}
 */

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const ROOT = path.join(__dirname, "../..");
const CACHE_DIR = path.join(ROOT, ".yarn/cache");
const PROJECT_PACKAGE_JSON = path.join(ROOT, "package.json");
const SCRIPT_NAMES = new Set([
  "preinstall",
  "postinstall",
  "install",
  // there are more and not all are use by yarn
  // https://docs.npmjs.com/cli/v8/using-npm/scripts
  // https://yarnpkg.com/advanced/lifecycle-scripts
  // For backwards compatibility, the preinstall and install scripts, if presents, are called right before running
  // the postinstall script from the same package. In general, prefer using postinstall over those two.
]);

checkLifecycleScript();

function checkLifecycleScript() {
  if (!fs.existsSync(CACHE_DIR)) {
    console.error("No .yarn/cache directory found at: " + CACHE_DIR);
    process.exit(1);
  }

  /**
   * @type {Package}
   */
  const projectPackageJson = JSON.parse(
    fs.readFileSync(PROJECT_PACKAGE_JSON, "utf8"),
  );
  /**
   * @type {DependenciesMeta}
   */
  const dependenciesMeta = projectPackageJson.dependenciesMeta || {};

  let found = 0;

  const zipFiles = fs.readdirSync(CACHE_DIR).filter((f) => f.endsWith(".zip"));
  for (const zipFile of zipFiles) {
    const zipPath = path.join(CACHE_DIR, zipFile);

    /**
     * @type {Record<string,Package|string>}
     */
    let pkgs;
    try {
      pkgs = readPackageJsonsFromZip(zipPath);
    } catch (err) {
      // @ts-expect-error good enough
      console.error(`Error reading ${zipFile}: ${err.message}`);
      process.exit(2);
    }

    // usually there is one package json
    // some packages have up to 4 for different version (common vs module etc.)
    // resolve has 14 including the tests
    // vue-simple-range-slider has 720 - the whole vue2 dependencies
    for (const [entry, pkgOrParseError] of Object.entries(pkgs)) {
      if (typeof pkgOrParseError === "string") {
        console.warn(entry, pkgOrParseError);
        continue;
      }
      /**
       * @type {Package}
       */
      const pkg = pkgOrParseError;
      if (!pkg.scripts) continue;

      const pkgKey = `${pkg.name}@${pkg.version}`;
      /**
       * @type {DependencyMeta|undefined}
       */
      const meta = dependenciesMeta[pkgKey] ?? dependenciesMeta[pkg.name];
      const buildEnabled = meta && meta.built === true;
      const warningSuppressed = meta && meta.built === false;
      const scripts = Object.entries(pkg.scripts)
        .filter(([name]) => SCRIPT_NAMES.has(name))
        .filter(([name, script]) => {
          if (
            (name === "preinstall" ||
              name === "install" ||
              name === "postinstall") &&
            buildEnabled
          ) {
            return false; //whitelisted in package.json
          }
          if (name === "postinstall" && script === "lerna bootstrap")
            return false; //valid mono repo management script
          //TODO add more here
          return true;
        });
      if (scripts.length <= 0 || warningSuppressed) continue;
      found += scripts.length;

      console.log(`${pkg.name}@${pkg.version} (${zipFile} - ${entry})`);
      for (const [key, script] of scripts) {
        console.log(`  ${key}: ${script}`);
      }
    }
  }

  if (found > 0) {
    console.log(
      `Found ${found} packages with install-related scripts. Verify the scripts and update dependenciesMeta in package.json if suitable.`,
    );
    process.exit(2);
  } else {
    console.log("Everything fine. No unexpected scripts.");
    process.exit(0);
  }
}

/**
 * @param {string} zipPath
 * @returns {Record<string,Package|string>}
 */
function readPackageJsonsFromZip(zipPath) {
  /**
   * @type {Record<string,Package|string>}
   */
  const result = {};
  for (const entry of new AdmZip(zipPath).getEntries()) {
    if (entry.name === "package.json") {
      try {
        result[entry.entryName] = JSON.parse(entry.getData().toString("utf8"));
      } catch (e) {
        // @ts-expect-error goof enough
        result[entry.entryName] = e.message;
      }
    }
  }
  return result;
}
