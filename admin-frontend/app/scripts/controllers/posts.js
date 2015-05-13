'use strict';

angular.module('Virblog')
  .controller('PostsCtrl', function ($scope, $http, $translate, type, $sce) {

    $scope.$translate = $translate;
    $scope.status = 'Published';
    $scope.locales = [];
    $scope.currentPage = 1;
    $scope.title = 'nav.' + type.toUpperCase() + 'S';
    $scope.type = type;

    function reload() {
      $http.get('/api/v1/posts?page=' + $scope.currentPage +
        '&type=' + type + '&status=' + $scope.status).success(function (data) {
        $scope.posts = data.data;
        $scope.count = data.count;
      });
    }

    $scope.$watch('status', reload);

    $http.get('/api/v1/options').success(function (data) {
      for (var key in data.locales) {
        $scope.locales.push({
          code: key,
          name: data.locales[key]
        });
      }
    });

    $scope.unpublish = function (post) {
      post.status = 'Draft';
      $http.put('/api/v1/posts', post).success(function () {
        reload();
      });
    };

    $scope.publish = function (post) {
      post.status = 'Published';
      $http.put('/api/v1/posts', post).success(function () {
        reload();
      });
    };

    $scope.remove = function (post) {
      $http.delete('/api/v1/posts/' + post.slug).success(function () {
        reload();
      });
    };

    $scope.pageChanged = reload;

    var languageOverrides = {
      js: 'javascript',
      html: 'xml'
    };

    var md = markdownit({
      highlight: function (code, lang) {
        if (languageOverrides[lang]) {
          lang = languageOverrides[lang];
        }
        if (lang && hljs.getLanguage(lang)) {
          try {
            return hljs.highlight(lang, code).value;
          } catch (e) {
          }
        }
        return '';
      }
    });

    $scope.render = function (content) {
      return $sce.trustAsHtml(md.render(content || ''));
    };

    $http.get('/api/v1/options').success(function (data) {
      $scope.settings = data;
    });

  });
