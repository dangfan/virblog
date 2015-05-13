'use strict';

angular.module('Virblog')
  .controller('UsersCtrl', function ($scope, $http, $translate, $filter,
                                     notify, $rootScope, $modal) {

    $scope.save = function () {
      $http.put('/api/v1/users/update', {
        email: $rootScope.user.email,
        nickname: $rootScope.user.nickname
      }).success(function () {
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

    $scope.open = function () {

      var modalInstance = $modal.open({
        animation: false,
        templateUrl: 'ChangePassword',
        controller: 'ChangePasswordCtrl'
      });

    };

  }).controller('ChangePasswordCtrl', function ($scope, $rootScope, notify,
                                                $modalInstance, $http,
                                                $translate) {

    $scope.passwords = {};

    $scope.ok = function () {
      $http.put('/api/v1/users/update-password', {
        old: $scope.passwords.old,
        new: $scope.passwords.new
      }).success(function () {
        $modalInstance.close();
        notify({
          message: $translate.instant('SUCCESS'),
          position: 'right',
          classes: ['alert-success'],
          duration: 3000
        });
      }).error(function () {
        $scope.mismatch = true;
      });
    };

    $scope.cancel = function () {
      $modalInstance.dismiss();
    };

  }).directive('pwCheck', [function () {
    return {
      require: 'ngModel',
      link: function (scope, elem, attrs, ctrl) {
        var firstPassword = '#' + attrs.pwCheck;
        elem.add(firstPassword).on('keyup', function () {
          scope.$apply(function () {
            var v = elem.val() === $(firstPassword).val();
            ctrl.$setValidity('pwmatch', v);
          });
        });
      }
    }
  }]);
