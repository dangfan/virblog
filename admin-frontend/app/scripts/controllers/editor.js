'use strict';

angular.module('Virblog')
  .controller('EditorCtrl', function ($scope, $http, $translate, $stateParams,
                                      notify, $q, $filter, $state) {

    var slug = $stateParams.slug;
    $scope.langKey = $translate.use();
    $scope.locales = [];
    $scope.editorOptions = {
      lineWrapping: true,
      lineNumbers: false,
      matchBrackets: true,
      mode: 'gfm',
      theme: 'default',
      extraKeys: {Enter: 'newlineAndIndentContinueMarkdownList'}
    };
    $scope.tags = [];
    $scope.new = $stateParams.slug === '';

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

    $http.get('/api/v1/tags').success(function (data) {
      data.forEach(function (tag) {
        $scope.tags.push({
          text: tag.slug
        });
      });
    });

    $scope.loadTags = function (query) {
      var defer = $q.defer();
      defer.resolve($filter('filter')($scope.tags, {text: query}));
      return defer.promise;
    };

    $http.get('/api/v1/options').success(function (data) {
      $scope.settings = data;
      for (var key in data.locales) {
        $scope.locales.push({
          code: key,
          name: data.locales[key]
        });
      }
    });

    $scope.post = {
      time: new Date(),
      subtitle: {},
      excerpt: {},
      content: {},
      postType: $stateParams.type,
      headerImage: ''
    };

    if (!$scope.new) {
      $http.get('/api/v1/posts/' + slug).success(function (data) {
        $scope.post = data;
        $scope.post._tags = [];
        data.tags.forEach(function (tag) {
          $scope.post._tags.push({text: tag});
        });
      });
    }

    $scope.$watch('post.content[langKey]', function () {
      $('#content-preview').html(
        md.render($scope.post.content[$scope.langKey] || ''));
    });

    $scope.$watch('post.excerpt[langKey]', function () {
      $('#excerpt-preview').html(
        md.render($scope.post.excerpt[$scope.langKey] || ''));
    });

    $scope.save = function () {
      $scope.post.tags = [];
      if ($scope.post.postType == 'Post') {
        $scope.post._tags.forEach(function (tag) {
          $scope.post.tags.push(tag.text);
        });
      }
      var method = $scope.new ? $http.post : $http.put;
      method('/api/v1/posts', $scope.post).success(function () {
        notify({
          message: $translate.instant('SUCCESS'),
          position: 'right',
          classes: ['alert-success'],
          duration: 3000
        });
        $state.go('dashboard.' + $scope.post.postType.toLowerCase() + 's');
      }).error(function () {
        notify({
          message: $translate.instant('FAILURE'),
          position: 'right',
          classes: ['alert-danger'],
          duration: 3000
        });
      });
    };

    $scope.zhs2zht = function (field) {
      var data = {
        content: $scope.post[field]['zh-Hans']
      };
      $http.post('/api/v1/i18n/zhs2zht', data).success(function (data) {
        $scope.post[field]['zh-Hant'] = data;
      });
    }

  });
