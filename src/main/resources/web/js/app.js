"use strict";

angular.module('catalog-manager',['ngLoading', 'ngButtonsRadio'])
    .config( ['$routeProvider', function($routeProvider) {
        $routeProvider
			.when('/home',      {templateUrl: 'partials/home.html' })
			
			.when('/pains',              {templateUrl: 'partials/pains.html', controller: PainsController })
			.when('/pains/:id',          {templateUrl: 'partials/pain.html', controller: PainDetailsController })
			.when('/pains/:id/edit',     {templateUrl: 'partials/pain-edit.html', controller: EditPainController })
			
			.when('/customers',          {templateUrl: 'partials/customers.html', controller: CustomersController })
			.when('/customers/:id',      {templateUrl: 'partials/customer.html', controller: CustomerDetailsController })
			.when('/customers/:id/edit', {templateUrl: 'partials/customer-edit.html', controller: EditCustomerController })
			
			.when('/results',            {templateUrl: 'partials/results.html', controller: ResultsController })
			.when('/unlinked',           {templateUrl: 'partials/unlinked-issues.html', controller: UnlinkedIssuesController })
			
            .otherwise({redirectTo: '/home'});
    }] )
	
	// wrapper em torno do $http que emite eventos "auth-loginRequired" quando um request retorna 401.
	.config(function($httpProvider) {
		
		$httpProvider.defaults.headers.common[ 'X-Session-ID' ] = localStorage[ 'Jaya-Session-ID' ];
		$httpProvider.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8';
		$httpProvider.defaults.headers.put['Content-Type'] = 'application/json;charset=utf-8';

		var interceptor = ['$rootScope', '$q', function($rootScope, $q) {

			// sucesso: retorna resposta
			function success(response) {
				return response;
			}

			// erro
			function error(response) {

				// 401, emite evento
				if (response.status === 401) {
					$rootScope.$broadcast('event:auth-loginRequired');
					$("#popNotLoggedIn").modal({backdrop:true}).modal("show");
				}


				// depois (e em qualquer outro erro), apenas retorna o erro
				return $q.reject(response);
			}

			return function(promise) {
				return promise.then(success, error);
			}
		}];
		$httpProvider.responseInterceptors.push(interceptor);
	})
	
    .run( function($rootScope, $location, $http) {
	
		// Desliga o overlay quando clicamos em cima.
		// $(function() { $("#overlay").click(function() { $(this).addClass("off") }) });
		
		// Se o botao para exibir o menu (tres linhas horizontais) estiver visivel (phones), entao simule um clique
		// para fechar o menu quando um item do menu for escolhido.
	    $(".nav a").on('click', function() {
			if ( $(".navbar-toggle").is(":visible") )
				$(".navbar-toggle").click();
		});
		
		$http.get("api/access/me").success(function(data) {
			$rootScope.currentUser = data;
		});
    	
    })	
    ;

function lazyGet( obj, key, defVal ) {
	var r = obj[key];
	if ( r == null ) {
		obj[key] = defVal;
		return defVal;
	} else {
		return r;
	}
}

function ResultsController( $scope, $http ) {
}

var findFirst = function( l, f ) {
	for ( var i = 0; i < l.length; i++ )
		if ( f(l[i]) )
			return l[i];
	return null;
}
