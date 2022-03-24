// This file sets a custom webpack configuration to use your Next.js app
// with Sentry.
// https://nextjs.org/docs/api-reference/next.config.js/introduction
// https://docs.sentry.io/platforms/javascript/guides/nextjs/

const { withSentryConfig } = require('@sentry/nextjs');
const withPWA = require("next-pwa");
const runtimeCaching = require("next-pwa/cache");

module.exports = withPWA({
  pwa: {
    dest: "public",
    runtimeCaching,
    buildExcludes: [/middleware-manifest\.json$/],
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

const sentryWebpackPluginOptions = {
  // Additional config options for the Sentry Webpack plugin. Keep in mind that
  // the following options are set automatically, and overriding them is not
  // recommended:
  //   release, url, org, project, authToken, configFile, stripPrefix,
  //   urlPrefix, include, ignore

  silent: true, // Suppresses all logs
  // For all available options, see:
  // https://github.com/getsentry/sentry-webpack-plugin#options.
};

// Make sure adding Sentry options is the last code to run before exporting, to
// ensure that your source maps include changes from all other Webpack plugins
// module.exports = withSentryConfig(moduleExports);
