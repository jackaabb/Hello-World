(function () {
  var app = angular.module("fwpApp", ['compost', 'eWalletCommonModule', 'ui.grid', 'ui.grid.pagination', 'ui.grid.autoResize', 'ui.grid.resizeColumns',
    'ui.grid.selection']);

  app.factory('fwpService', function (cResource) {
    var resource = cResource('/', {});
    return {
      execute: function (url, param, omitAlerts) {
        var options = {
          'omitAlerts': omitAlerts || false
        };
        return resource.execute(url, param, options);
      },
    };
  });

  app.controller('fwpController', function ($scope, fwpService, cStateManager, cAlerter, $filter) {
    var state = cStateManager([{
      name: 'init',
      from: ['NONE'],
      to: 'INIT'
    }
    ]);
    $scope.init = function (omitAlerts) {
      state.init();
      $scope.search = {};
      $scope.model = {};
      cAlerter.clear();

      fwpService.execute('isProd', $scope.model).then(function (response) {
        if (response != null) {
          $scope.isProd = response.result;
        }
      });
    };

    //FWP350x對帳資料預轉批次
    $scope.batch350x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      $scope.model.tmTxnTime350 = $filter('date')(new Date(moment($scope.search.tmTxnTime350).subtract(1, 'day')), 'yyyy-MM-dd');

      fwpService.execute('batch350Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP352x產對帳檔
    $scope.batch352x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      //對帳檔找當日的， 故先註解
//			$scope.model.tmTxnTime352 = $filter('date')(new Date(moment($scope.search.tmTxnTime352).subtract(1, 'day')),'yyyy-MM-dd');

      fwpService.execute('batch352Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP354x產撥款收據
    $scope.batch354x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('batch354Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP358x對帳資料預轉批次
    $scope.batch358x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      $scope.model.tmTxnTime358 = $filter('date')(new Date(moment($scope.search.tmTxnTime358).subtract(1, 'day')), 'yyyy-MM-dd');

      fwpService.execute('batch358Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }


    //produceRec
    $scope.produceRec = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      $scope.model.produceRec = $filter('date')(new Date(moment($scope.search.produceRec)), 'yyyy-MM-dd');

      fwpService.execute('produceRec', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP367X POS 撥款批次
    $scope.batch367x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('batch367Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP360x續年年費通知批次
    $scope.batch360x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      $scope.model.tmTxnTime360 = $filter('date')(new Date(moment($scope.search.tmTxnTime360)), 'yyyy-MM-dd');

      fwpService.execute('batch360Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP361x年費繳款資訊檔
    $scope.batch361x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('batch361Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    //FWP366xPOS對帳通知批次
    $scope.batch366x = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      $scope.model.tmTxnTime366 = $filter('date')(new Date(moment($scope.search.tmTxnTime366).subtract(1, 'day')), 'yyyy-MM-dd');

      fwpService.execute('batch366Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }
    //Fwp360x首年年費寄信通知
    $scope.sendFirst360Service = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('sendFirst360Service', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
    }

    $scope.gen371xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen371xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen372xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen372xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen373xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen373xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen374xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen374xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen375xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen375xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen376xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen376xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen378xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen378xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen379xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen379xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen380xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen380xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen381xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen381xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen382xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen382xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen383xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen383xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen384xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen384xReport', $scope.model).then(function (response) {
      });
    };

    $scope.gen385xReport = function () {
      $scope.model = angular.copy($scope.search);
      fwpService.execute('gen385xReport', {}).then(function (response) {
      });
    };

    $scope.gen386xReport = function () {
      $scope.model = angular.copy($scope.search);
      $scope.checkTwPayReportDate();
      fwpService.execute('gen386xReport', $scope.model).then(function (response) {
      });
    };

    $scope.checkTwPayReportDate = function () {
      if ($scope.model.twPayReportTime371 instanceof Date) {
        $scope.model.twPayReportTime371 = $filter('date')($scope.model.twPayReportTime371, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime372 instanceof Date) {
        $scope.model.twPayReportTime372 = $filter('date')($scope.model.twPayReportTime372, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime373 instanceof Date) {
        $scope.model.twPayReportTime373 = $filter('date')($scope.model.twPayReportTime373, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime374 instanceof Date) {
        $scope.model.twPayReportTime374 = $filter('date')($scope.model.twPayReportTime374, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime375 instanceof Date) {
        $scope.model.twPayReportTime375 = $filter('date')($scope.model.twPayReportTime375, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime376 instanceof Date) {
        $scope.model.twPayReportTime376 = $filter('date')($scope.model.twPayReportTime376, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime378 instanceof Date) {
        $scope.model.twPayReportTime378 = $filter('date')($scope.model.twPayReportTime378, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime379 instanceof Date) {
        $scope.model.twPayReportTime379 = $filter('date')($scope.model.twPayReportTime379, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime380 instanceof Date) {
        $scope.model.twPayReportTime380 = $filter('date')($scope.model.twPayReportTime380, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime381 instanceof Date) {
        $scope.model.twPayReportTime381 = $filter('date')($scope.model.twPayReportTime381, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime382 instanceof Date) {
        $scope.model.twPayReportTime382 = $filter('date')($scope.model.twPayReportTime382, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime383 instanceof Date) {
        $scope.model.twPayReportTime383 = $filter('date')($scope.model.twPayReportTime383, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime384 instanceof Date) {
        $scope.model.twPayReportTime384 = $filter('date')($scope.model.twPayReportTime384, 'yyyy-MM-ddT00:00:00.000');
      }
      if ($scope.model.twPayReportTime386 instanceof Date) {
        $scope.model.twPayReportTime386 = $filter('date')($scope.model.twPayReportTime386, 'yyyy-MM-ddT00:00:00.000');
      }
    };

    $scope.batch368x = function () {
      cAlerter.clear();
      $scope.model = {};
      fwpService.execute('batch368x', $scope.model).then(function (response) {
      });
    };

    $scope.getDisbursementDate = function () {
      cAlerter.clear();
      $scope.model = {accountDate: $scope.search.accountDate, allocatedDays: $scope.search.allocatedDays};
      fwpService.execute('getDisbursementDate', $scope.model).then(function (response) {
        $scope.model.disbursementDate = response;
      });
    };

    $scope.batch490x = function () {
      cAlerter.clear();
      if ($scope.search.tmTxnTime490 == null) {
        cAlerter.warn("請輸入交易日期");
      } else {
        $scope.model = {tmTxnTime490: $filter('date')($scope.search.tmTxnTime490, 'yyyy-MM-ddT00:00:00.000')};
        fwpService.execute('batch490x', $scope.model).then(function (response) {
        });
      }

    };

    $scope.batch370x = function () {
      cAlerter.clear();
      if ($scope.search.walletListTime == null) {
        cAlerter.warn("錢包日期");
      } else {
        $scope.model = {walletListTime: $filter('date')($scope.search.walletListTime, 'yyyy-MM-ddT00:00:00.000')};
        fwpService.execute('batch370x', $scope.model).then(function (response) {
        });
      }
    };

    $scope.exportIdmbFileBatch = function () {
        cAlerter.clear();
        $scope.model = {execBatchTime: $filter('date')($scope.search.execBatchTime, 'yyyy-MM-ddT00:00:00.000')};
        fwpService.execute('exportIdmbFileBatch', $scope.model).then(function (response) {
        if (response != null) {
          cAlerter.warn(response.result);
        }
      });
       
      
    };


    $scope.clear = function () {
      $scope.init();
    };
    $scope.init();
  });



})();
