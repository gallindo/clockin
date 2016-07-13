(function() {
    'use strict';

    angular
        .module('clockinApp')
        .controller('ClockinCalendarController', ClockinCalendarController);

    ClockinCalendarController.$inject = ['ClockinCalendar','entity','$scope','$state','$locale',
    '$rootScope','$stateParams','$mdMedia','$mdDialog','$filter','DateUtils'];

    function ClockinCalendarController (ClockinCalendar, entity, $scope, $state, $locale, $rootScope, $stateParams,
    $mdMedia, $mdDialog, $filter, DateUtils) {
        var vm = this;

        var month = eval($stateParams.month);
        var year = eval($stateParams.year);

        vm.previousYear = year;
        vm.previousMonth = (month - 1);
        vm.nextYear = year;
        vm.nextMonth = (month + 1);
        if (month == 1){
        	vm.previousYear = (year - 1);
        	vm.previousMonth = 12;
        } else if (month == 12){
        	 vm.nextYear = (year + 1);
             vm.nextMonth = 1;
        }
        vm.date = new Date(vm.previousYear, vm.previousMonth);

        vm.workdays = entity;

        vm.shortDays = $locale.DATETIME_FORMATS.SHORTDAY.map(function(shortDay){
            return {
                header: $filter('capitalize')(shortDay),
                fstChar: shortDay.charAt(0).toUpperCase()
            };
        });

        $scope.customFullscreen = $mdMedia('xs') || $mdMedia('sm');
        $scope.showTabDialog = function(event, workday) {

            if (workday.clockinValues.length > 0) {
                $mdDialog.show({
                    controller: DialogController,
                    templateUrl: 'app/entities/clockin/clockin-calendar.tab-dialog.html',
                    parent: angular.element(document.body),
                    targetEvent: event,
                    clickOutsideToClose: true,
                    disableParentScroll: false,
                    locals: {
                        workday: workday
                    }
                });
           }
        };

        function DialogController($scope, $mdDialog, workday) {
            $scope.workday = workday;
            $scope.hide = function() {
              $mdDialog.hide();
            };
        }
    }
})();
