const withPWA = require("next-pwa");
module.exports = withPWA({
  pwa: {
    dest: "public",
  },
  env: {
    // Commenting to avoid inlining of this env variable. Will be fetched using getServerSideProps from pages where its required
    // APP_BASE_URL: process.env.APP_BASE_URL,
    API_BASE: process.env.API_BASE,
    INTEGRATED_DOC: process.env.INTEGRATED_DOC,
    DEBUG: process.env.DEBUG,
    DOC_APP_IMAGE_BASE_URL: process.env.DOC_APP_IMAGE_BASE_URL,
  },
  publicRuntimeConfig: {
    // Will be available on both server and client
    isOss: process.env.IS_OSS,
  },
  async rewrites() {
    const backendBaseUrl = process.env.API_BASE_URL;
    const apiBase = process.env.API_BASE;
    return [
      {
        source: "/swagger/:path*",
        destination: `${backendBaseUrl}${apiBase}/swagger/:path*`,
      },
      {
        source: "/swagger-static/:path*",
        destination: `${backendBaseUrl}${apiBase}/swagger-static/:path*`,
      },
      {
        source: "/swagger.json/:path*",
        destination: `${backendBaseUrl}${apiBase}/swagger.json/:path*`,
      },
      {
        source: `${apiBase}/:path*`,
        destination: `${backendBaseUrl}${apiBase}/:path*`,
      },
    ];
  },
});
