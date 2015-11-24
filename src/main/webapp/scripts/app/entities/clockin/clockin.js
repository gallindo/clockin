'use strict';

angular.module('clockinApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('clockin', {
                parent: 'entity',
                url: '/clockins',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'clockinApp.clockin.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/clockin/clockins.html',
                        controller: 'ClockinController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('clockin');
                        $translatePartialLoader.addPart('registryType');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('clockin.detail', {
                parent: 'entity',
                url: '/clockin/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'clockinApp.clockin.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/clockin/clockin-detail.html',
                        controller: 'ClockinDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('clockin');
                        $translatePartialLoader.addPart('registryType');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Clockin', function($stateParams, Clockin) {
                        return Clockin.get({id : $stateParams.id});
                    }]
                }
            })
            .state('clockin.new', {
                parent: 'clockin',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/clockin/clockin-dialog.html',
                        controller: 'ClockinDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    sequentialRegisterNumber: null,
                                    dateTime: null,
                                    registryType: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('clockin', null, { reload: true });
                    }, function() {
                        $state.go('clockin');
                    })
                }]
            })
            .state('clockin.edit', {
                parent: 'clockin',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/clockin/clockin-dialog.html',
                        controller: 'ClockinDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Clockin', function(Clockin) {
                                return Clockin.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('clockin', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
