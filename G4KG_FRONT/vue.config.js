const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack');

module.exports = defineConfig({
  publicPath: '/G4KG/',
  devServer: {
    proxy: {
      '/api': {
        target: 'http://10.201.12.44:8081', // 指向后端地址
        changeOrigin: true,
        pathRewrite: { '^/api': '/api' } // 保留 /api 前缀
      }
    }
  },
  transpileDependencies: true,
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        '__VUE_OPTIONS_API__': JSON.stringify(true), // 启用 Composition API 和 Options API
        '__VUE_PROD_DEVTOOLS__': JSON.stringify(false), // 禁用生产环境的 Vue Devtools
        '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': JSON.stringify(false), // 添加缺失的 feature flag
      }),
    ],
  },
});
