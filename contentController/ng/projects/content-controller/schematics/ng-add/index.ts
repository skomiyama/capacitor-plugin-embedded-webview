/* eslint-disable @typescript-eslint/consistent-type-imports */
import { virtualFs, workspaces } from '@angular-devkit/core';
import {
  Rule, SchematicContext, SchematicsException, Tree,
} from '@angular-devkit/schematics';
import { NodePackageInstallTask } from '@angular-devkit/schematics/tasks';
import * as fs from 'fs';

function createHost(tree: Tree): workspaces.WorkspaceHost {
  return {
    async readFile(path: string): Promise<string> {
      const data = tree.read(path);
      if (!data) {
        throw new SchematicsException('File not found.');
      }
      return virtualFs.fileBufferToString(data);
    },
    async writeFile(path: string, data: string): Promise<void> {
      return tree.overwrite(path, data);
    },
    async isDirectory(path: string): Promise<boolean> {
      return !tree.exists(path) && tree.getDir(path).subfiles.length > 0;
    },
    async isFile(path: string): Promise<boolean> {
      return tree.exists(path);
    },
  };
}


interface Options {
  project?: string;
  globalStylePath?: string;
}

// Just return the tree
export function ngAdd(options: Options): Rule {
  return (tree: Tree, context: SchematicContext) => {
    context.addTask(new NodePackageInstallTask());

    const host = createHost(tree);
    host.readFile('/angular.json').then(async json => {
      const angularJson = JSON.parse(json)
      const project: string | undefined = options.project || angularJson.defaultProject;
      if (!project) {
        throw new SchematicsException('project is undefined. please pass --project option or set defaultProject into angular.json')
      }
      const projectDir: string = angularJson.projects[project].root;
      const projectGlobalStylePath = projectDir + (options.globalStylePath || 'src/global.scss');

      /* overwrite style */
      if (!(await host.isFile(projectGlobalStylePath))) {
        throw new SchematicsException('global style file is not undefined. please make sure global style path is correct.')
      }
      const globalStyleFile = await host.readFile(projectGlobalStylePath);
      if (!globalStyleFile.includes('@skomiyama/embedded-webview-controller')) {
        const style = '\n@import "~@skomiyama/embedded-webview-controller/styles/ionic-app.scss";\n';
        const newGlobalStyleFile = globalStyleFile + style;
        fs.writeFileSync(projectGlobalStylePath, newGlobalStyleFile);
        console.log(`Updated ${projectGlobalStylePath}`)
      }
    });

    return tree;
  };
}
