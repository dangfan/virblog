// Karma configuration
module.exports = function(config) {
  'use strict';

  config.set({
    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // base path, that will be used to resolve files and exclude
    //basePath: '../',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      // bower:js
      '../bower_components/jquery/dist/jquery.js',
      '../bower_components/angular/angular.js',
      '../bower_components/bootstrap/dist/js/bootstrap.js',
      '../bower_components/ui-router/release/angular-ui-router.js',
      '../bower_components/angular-animate/angular-animate.js',
      '../bower_components/angular-translate/angular-translate.js',
      '../bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      '../bower_components/bootbox/bootbox.js',
      '../bower_components/ngBootbox/dist/ngBootbox.js',
      '../bower_components/chosen/chosen.jquery.min.js',
      '../bower_components/angular-chosen-localytics/chosen.js',
      '../bower_components/angular-notify/dist/angular-notify.js',
      '../bower_components/markdown-it/dist/markdown-it.js',
      '../bower_components/codemirror/lib/codemirror.js',
      '../bower_components/angular-ui-codemirror/ui-codemirror.js',
      '../bower_components/angular-ui-bootstrap-datetimepicker/datetimepicker.js',
      '../bower_components/ng-table/dist/ng-table.min.js',
      '../bower_components/ng-tags-input/ng-tags-input.min.js',
      '../bower_components/md5/build/md5.min.js',
      // endbower
      '../app/scripts/**/*.js',
      //'../test/mock/**/*.js',
      '../test/spec/**/*.js'
    ],

    // list of files / patterns to exclude
    exclude: [
    ],

    // web server port
    port: 8080,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: [
      'PhantomJS'
    ],

    // Which plugins to enable
    plugins: [
      'karma-phantomjs-launcher',
      'karma-jasmine'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,

    colors: true,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // Uncomment the following lines if you are using grunt's server to run the tests
    // proxies: {
    //   '/': 'http://localhost:9000/'
    // },
    // URL root prevent conflicts with the site root
    // urlRoot: '_karma_'
  });
};
