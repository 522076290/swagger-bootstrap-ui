module.exports = {
  publicPath: "/",
  assetsDir: "webjars",
  outputDir: "dist",
  lintOnSave: false,
  productionSourceMap: false,
  indexPath: "index.html",
  css: {
    loaderOptions: {
      less: {
        javascriptEnabled: true
      }
    }
  },
  devServer: {
    proxy: {
      "/": {
        target: "http://localhost:17808/", 
        ws: true,
        changeOrigin: true
      }
    }
  }
};
