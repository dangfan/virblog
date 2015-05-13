'use strict';

angular.module('Virblog')
  .controller('DashboardCtrl', function ($scope, $rootScope, $state, $http) {
    $scope.$state = $state;

    $http.get('/api/v1/user-info')
      .success(function (data) {
        data.email = data.email || '';
        data.nickname = data.nickname || '';
        $rootScope.user = data;
        if (data.email) {
          var hash = md5(data.email.trim().toLowerCase());
          $scope.avatar = '//www.gravatar.com/avatar/' + hash + '?s=160';
        }
      });

  });
