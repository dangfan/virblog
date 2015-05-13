'use strict';

angular.module('Virblog')
  .controller('LoginCtrl', function ($scope, $location, $http, $translate) {

    $http.get('/api/v1/user-info')
      .success(function () {
        $location.path('/dashboard');
      });

    $scope.setLang = function (locale) {
      $translate.use(locale);
    };

    $scope.submit = function () {
      $http.post('/api/v1/login', $scope.login).success(function (response) {
        if (response.status === 'ok') {
          $location.path('/dashboard');
        }
      }).error(function () {
        $scope.authMsg = 'login.WRONG';
      });
      return false;
    }

  });
