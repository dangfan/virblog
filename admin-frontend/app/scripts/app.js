'use strict';

angular.module('Virblog', ['ui.router', 'ngAnimate', 'pascalprecht.translate',
  'ui.bootstrap', 'ngBootbox', 'localytics.directives', 'cgNotify',
  'ui.bootstrap.datetimepicker', 'ui.codemirror', 'ngTable', 'ngTagsInput'])
  .config(function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.when('/dashboard', '/dashboard/posts');
    $urlRouterProvider.otherwise('/login');

    $stateProvider
      .state('base', {
        abstract: true,
        url: '',
        templateUrl: 'views/base.html'
      })
      .state('login', {
        url: '/login',
        parent: 'base',
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl',
        title: 'login.LOGIN'
      })
      .state('dashboard', {
        url: '/dashboard',
        parent: 'base',
        templateUrl: 'views/dashboard.html',
        controller: 'DashboardCtrl'
      })
      .state('dashboard.posts', {
        url: '/posts',
        templateUrl: 'views/dashboard/posts.html',
        controller: 'PostsCtrl',
        resolve: {
          type: function () {
            return 'Post';
          }
        },
        title: 'nav.POSTS'
      })
      .state('dashboard.pages', {
        url: '/pages',
        templateUrl: 'views/dashboard/posts.html',
        controller: 'PostsCtrl',
        resolve: {
          type: function () {
            return 'Page';
          }
        },
        title: 'nav.PAGES'
      })
      .state('dashboard.editor', {
        url: '/edit/:slug?type',
        templateUrl: 'views/dashboard/editor.html',
        controller: 'EditorCtrl',
        title: 'nav.EDIT'
      })
      .state('dashboard.tags', {
        url: '/tags',
        templateUrl: 'views/dashboard/tags.html',
        controller: 'TagsCtrl',
        title: 'nav.TAGS'
      })
      .state('dashboard.settings', {
        url: '/settings',
        templateUrl: 'views/dashboard/settings.html',
        controller: 'SettingsCtrl',
        title: 'nav.SETTINGS'
      })
      .state('dashboard.users', {
        url: '/users',
        templateUrl: 'views/dashboard/users.html',
        controller: 'UsersCtrl',
        title: 'nav.USERS'
      })
      .state('dashboard.blogrolls', {
        url: '/blogrolls',
        templateUrl: 'views/dashboard/blogrolls.html',
        controller: 'BlogrollsCtrl',
        title: 'nav.BLOGROLLS'
      });

  }).config(['$translateProvider', function ($translateProvider) {

    $translateProvider
      .translations('en', EN)
      .translations('zh-Hans', ZH_HANS)
      .registerAvailableLanguageKeys(['zh-Hans', 'en'], {
        'zh-CN': 'zh-Hans',
        'en-US': 'en',
        'en-UK': 'en'
      })
      .fallbackLanguage('en')
      .useSanitizeValueStrategy('escaped')
      .uniformLanguageTag('bcp47')
      .determinePreferredLanguage();

  }]).config(function ($httpProvider) {

    $httpProvider.interceptors.push(function ($q) {
      return {
        'responseError': function (rejection) {
          if (rejection.status == 401 &&
            window.location.href.indexOf('/login') == -1) {
            window.location.href = '/admin/';
          }
          return $q.reject(rejection);
        }
      };
    });

  }).directive('updateTitle', ['$rootScope', '$timeout', '$translate',
    function ($rootScope, $timeout, $translate) {
    return {
      link: function (scope, element) {
        var listener = function (event, toState) {
          var title = 'Default Title';
          if (toState.title) {
            title = $translate.instant(toState.title);
          }
          $timeout(function () {
            element.text(title);
          }, 0, false);
        };
        $rootScope.$on('$stateChangeSuccess', listener);
      }
    };
  }]).directive('newScope', function () {
    return {
      scope: true
    }
  });
