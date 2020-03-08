'use strict'
const path = require('path')
const WebpackBar = require('webpackbar')
const settings = require('./settings')

function resolve (dir) {
  return path.join(__dirname, dir)
}

const name = settings.projectName

module.exports = {
  publicPath: process.env.VUE_APP_PUBLIC_PATH,
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: process.env.NODE_ENV === 'development' ? 'error' : false,
  productionSourceMap: false,
  devServer: {
    host: settings.host,
    port: settings.port,
    open: true,
    progress: false,
    overlay: {
      warnings: false,
      errors: true
    },
    proxy: {
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:9000/octopus`,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: '/api'
        }
      }
    }
  },
  configureWebpack: {
    name: name,
    resolve: {
      alias: {
        '@': resolve('src'),
        '@v': resolve('src/views')
      }
    },
    externals: {
    },
    plugins: [
      new WebpackBar()
    ]
  },
  transpileDependencies: [
    'vuetify'
  ],
  chainWebpack (config) {
    config.plugins.delete('preload') // TODO: need test
    config.plugins.delete('prefetch') // TODO: need test
    // const types = ['vue-modules', 'vue', 'normal-modules', 'normal']
    // types.forEach(type => addStyleResource(config.module.rule('less').oneOf(type)))

    // // 使用sass-loader引用全局变量文件
    // function addStyleResource(rule) {
    //   rule.use('sass-resource')
    //     .loader('sass-resources-loader')
    //     .options({
    //       resources: resolve('src/styles/variable.scss')
    //     })
    // }

    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()

    // set preserveWhitespace
    config.module
      .rule('vue')
      .use('vue-loader')
      .loader('vue-loader')
      .tap(options => {
        options.compilerOptions.preserveWhitespace = true
        return options
      })
      .end()

    // config.module
    //   .rule('')
    //   .test(/view-design.src.*?js$/)
    //   .use('babel-loader')
    //   .loader('babel-loader')
    //   .end()

    config
      .when(process.env.NODE_ENV === 'development',
        // cheap-source-map没有loader之间的sourcemap文件，在debug的时候，定义到压缩前的js中的时候，不能跟踪到vue中
        // config => config.devtool('cheap-source-map')
        config => config.devtool('source-map')
      )

    config
      .when(process.env.NODE_ENV !== 'development',
        config => {
          config
            .plugin('ScriptExtHtmlWebpackPlugin')
            .after('html')
                .use('script-ext-html-webpack-plugin', [{
                  // `runtime` must same as runtimeChunk name. default is `runtime`
                  inline: /runtime\..*\.js$/
                }])
            .end()
          config
            .optimization.splitChunks({
              chunks: 'all',
              cacheGroups: {
                libs: {
                  name: 'chunk-libs',
                  test: /[\\/]node_modules[\\/]/,
                  priority: 10,
                  chunks: 'initial' // only package third parties that are initially dependent
                },
                vendor: {
                  name: 'chunk-vendors',
                  priority: 30,
                  test: /[\\/]vendors[\\/]/
                },
                moment: {
                  name: 'chunk-vendors',
                  priority: 30,
                  test: /[\\/]node_modules[\\/]_?moment(.*)/
                },
                lodash: {
                  name: 'chunk-vendors',
                  priority: 30,
                  test: /[\\/]node_modules[\\/]_?lodash(.*)/
                }
              }
            })
          config
            .optimization.runtimeChunk('single')
        }
      )
  }
}
