'use strict';

angular.module('Virblog')
  .controller('TagsCtrl', function ($scope, $http, ngTableParams, notify,
                                    $ngBootbox, $translate) {

    $scope.locales = [];

    $http.get('/api/v1/options').success(function (data) {
      $scope.locales = data.locales;
    });

    $scope.tableParams = new ngTableParams({
      count: 2
    }, {
      counts: [],
      groupBy: 'slug',
      total: 0,
      getData: function($defer) {
        var tags = [];
        $http.get('/api/v1/tags').success(function (data) {
          data.forEach(function (tag) {
            for (var key in $scope.locales) {
              tags.push({
                slug: tag.slug,
                code: key,
                name: tag.name[key] || ''
              });
            }
          });
          $defer.resolve(tags);
        });
      }
    });

    $scope.add = function () {
      $ngBootbox.prompt($translate.instant('tags.ADD_PROMPT'))
        .then(function(result) {
          var data = [];
          for (var key in $scope.locales) {
            data.push({
              slug: result,
              code: key,
              name: ''
            });
          }
          $scope.tableParams.data.push({
            value: result,
            data: data
          });
        });
    };

    $scope.save = function () {
      var tags = $scope.tableParams.data;
      var newTags = [];
      tags.forEach(function (tag) {
        var name = {};
        tag.data.forEach(function (n) {
          name[n.code] = n.name;
        });
        newTags.push({
          slug: tag.value,
          name: name
        });
      });
      $http.put('/api/v1/tags', newTags).success(function () {
        notify({
          message: $translate.instant('SUCCESS'),
          position: 'right',
          classes: ['alert-success'],
          duration: 3000
        });
      }).error(function () {
        notify({
          message: $translate.instant('FAILURE'),
          position: 'right',
          classes: ['alert-danger'],
          duration: 3000
        });
      });
    };

  });
