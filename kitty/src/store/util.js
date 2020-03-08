const autoLoadModule = () => {
  // https://webpack.js.org/guides/dependency-management/#requirecontext
  const modulesFiles = require.context('./modules', true, /\.js$/)

  // you do not need `import app from './modules/app'`
  // it will auto require all vuex module from modules file
  return modulesFiles.keys().reduce((modules, modulePath) => {
    // set './app.js' => 'app' or './mySelf/index.js' => 'mySelf'
    // only scan one layer, './mySelf/modules/xxx.js' can not auto require
    // const moduleName = modulePath.replace(/^\.\/(.*)(\.|\\)\w+$/, '$1')
    let moduleName = modulePath
    const addModules = (reg) => {
      moduleName = modulePath.match(reg)[1]
      const value = modulesFiles(modulePath)
      modules[moduleName] = value.default
    }
    const folderReg = /^\.\/(\w+)\/index\.\w+$/
    const fileReg = /^\.\/(\w+)\.\w+$/
    /* eslint-disable */
    folderReg.test(modulePath) ? addModules(folderReg) : fileReg.test(modulePath) ? addModules(fileReg) : modules
    return modules
  }, {})
}
export { autoLoadModule }
