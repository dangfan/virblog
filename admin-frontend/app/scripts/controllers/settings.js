'use strict';

angular.module('Virblog')
  .controller('SettingsCtrl', function ($scope, $http, $translate, $filter,
                                        notify) {

    var availableLocales = $scope.availableLocales = [
      {code: 'en', name: 'English'},
      {code: 'zh-Hans', name: '简体中文'},
      {code: 'zh-Hant', name: '台灣繁體'}];

    $scope.$translate = $translate;
    $scope.locales = [];

    $http.get('/api/v1/options').success(function (data) {
      $scope.settings = data;
      for (var key in data.locales) {
        $scope.locales.push({
          code: key,
          name: data.locales[key]
        });
      }
    });

    $scope.addLocale = function () {
      $scope.locales.push($filter('filter')(availableLocales,
        {code: $scope.newLocale})[0]);
      $scope.newLocale = '';
    };

    $scope.setDefaultLocale = function (code) {
      $scope.settings.defaultLocale = code
    };

    $scope.removeLocale = function (index) {
      $scope.locales.splice(index, 1);
    };

    $scope.saveLang = function () {
      $scope.settings.locales = {};
      $scope.locales.forEach(function (item) {
        $scope.settings.locales[item.code] = item.name;

        $scope.settings.blogName[item.code] =
          $scope.settings.blogName[item.code] || '';

        $scope.settings.blogDescription[item.code] =
          $scope.settings.blogDescription[item.code] || '';

        $scope.settings.datetimeFormat[item.code] =
          $scope.settings.datetimeFormat[item.code] || '';
      });
      $scope.save();
    };

    $scope.save = function () {
      $http.put('/api/v1/options', $scope.settings).success(function () {
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
