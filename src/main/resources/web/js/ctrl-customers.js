function CustomersController( $scope, $http, Loading ) {
	$scope.loadingPage = Loading.task().start();
	$http.get( "api/customers" ).success( function(data) { $scope.customers = data; $scope.loadingPage.success() } ).error( $scope.loadingPage.error );
}

function CustomerDetailsController( $scope, $http, $routeParams, $q, Loading ) {
	var calculateAverages = function( customer ) {
		customer.pains = [];
		angular.forEach( customer.opinions, function(opinion) {
			var pain = findFirst( customer.pains, function(p) { return p.id == opinion.painId } );
			if ( pain == null ) {
				var painTitle = findFirst( $scope.pains, function(p) { return p.id == opinion.painId } ).title;
				pain = { id: opinion.painId, totalWeight: 0, opinionsCount: 0, opinions: [], title: painTitle };
				customer.pains.push( pain );
			}
			
			pain.opinions.push( opinion );
			pain.totalWeight += opinion.userWeight;
			pain.opinionsCount++;
		});
	}

	$scope.loadingPage = Loading.task().start();
	$scope.refreshingResults = Loading.task();
	
	$q.all([
		$http.get( "api/pains" ),
		$http.get( "api/customers/" + $routeParams.id ),
		$http.get( "api/customers/" + $routeParams.id + "/results" )
	]).then(function(responses) {
		$scope.pains = responses[0].data;
		$scope.customer = responses[1].data;
		$scope.aggregateResults = responses[2].data;
		calculateAverages($scope.customer);
		$scope.loadingPage.success();
		$scope.refreshingResults.success();
	});

	$scope.collapseComments = true;
	$scope.collapseResults = true;
	$scope.collapseOpinion = true;
	
	$scope.possibleOpinions = [ 1, 2, 3, 4 ];
	
	$scope.refreshResults = function() {
		$scope.refreshingResults = Loading.task().start();
		$http.get("api/customers/" + $scope.customer.id + "/results").then(
			function(response) { $scope.aggregateResults = response.data; $scope.refreshingResults.success(); },
			function(response) { $scope.refreshingResults.error() }
		);
	}
}

function CustomerPainOpinionController( $scope, $http, Loading ) {
	// Quando este controller eh instanciado, no escopo sera colocado um objeto "Pain" que representa a pain corrente.
	// Iremos buscar no escopo pai qual a opiniao atual do usuario sobre esta pain, e colocar a opiniao neste escopo.
	$scope.currentUserOpinion = findFirst( $scope.customer.opinions, function(o) { return o.painId == $scope.pain.id && o.userId == $scope.currentUser.userId } );
	if ( $scope.currentUserOpinion == null ) {
		console.log( "opinion did not exist, creating one" );
		$scope.currentUserOpinion = { userId: $scope.currentUser.userId };
		$scope.customer.opinions.push( $scope.currentUserOpinion );
	}
	
	// Quando a opinion atual muda, avisa o server.
	var sendOpinionToServer = function(newVal, oldVal) {
		if ( oldVal === newVal ) {
			console.log( "listener initialized, values haven't really changed" );
			return;
		}
		$http.put( "api/customers/" + $scope.customer.id + "/opinions/" + $scope.pain.id,
			{ value: $scope.currentUserOpinion.value, comment: $scope.currentUserOpinion.comment } ).error( Loading.error );
		console.log( "changing " + $scope.pain.id + " value to " + $scope.currentUserOpinion.value );
	}
	$scope.$watch( 'currentUserOpinion', sendOpinionToServer, true );
}

function EditCustomerController( $scope, $http, $routeParams, Loading, $location ) {
	var id = $routeParams.id;
	if ( id != "new" ) {
		Loading.start();
		$http.get( "api/customers/" + $routeParams.id ).then(function(response) {
			Loading.hide();
			$scope.customer = response.data;
		});
	} else {
		$scope.customer = {};
	}
	
	$scope.save = function() {
		Loading.start();
		var q = null;
		if ( $scope.customer.id ) {
			q = $http.put("api/customers/" + $scope.customer.id, $scope.customer);
		} else {
			q = $http.post("api/customers", $scope.customer);
		}
		
		q.then(
			function(response) { Loading.success( "Customer saved successfully", function() { Loading.hide(); $location.path("customers") }, 1500 ) },
			Loading.error
		);
	}
	
}

