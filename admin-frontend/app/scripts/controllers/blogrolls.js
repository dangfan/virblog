'use strict';

angular.module('Virblog')
  .controller('BlogrollsCtrl', function ($scope, $http, notify, $translate) {

    $http.get('/api/v1/blogrolls').success(function (data) {
      $scope.blogrolls = data;
    });

    $scope.add = function () {
      $scope.blogrolls.push({name: '', link: ''});
    };

    $scope.remove = function (index) {
      $scope.blogrolls.splice(index, 1);
    };

    $scope.save = function () {
      $http.post('/api/v1/blogrolls', $scope.blogrolls).success(function () {
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
    }

  });
