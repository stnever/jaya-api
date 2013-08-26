function ResultsController( $scope, $http, Loading, $q ) {
	
	$scope.loadingPage = Loading.task().start();
	$q.all([
		$http.get( "api/pains" ),
		$http.get( "api/customers" ),
		$http.get( "api/users" ),
		$http.get( "api/leaderboard" )
	]).then(function(responses) {
		$scope.pains = responses[0].data;
		$scope.customers = responses[1].data;
		$scope.users = responses[2].data;
		$scope.board = responses[3].data;
		$scope.loadingPage.success();
	});

	/*
	$scope.board = {
		pains: [
			{ title: "Segurança", average: 3.5, participants: [ { value: 1 }, { value: 2 }, { value: 3 } ] }, 
			{ title: "Aceleração do Onboarding", average: 3.0, participants: [ { value: 1 }, { value: 2 }, { value: 3 } ] }, 
			{ title: "Facilitar geração de Documentação", average: 2.5, participants: [ { value: 1 }, { value: 2 }, { value: 3 } ] }, 
			{ title: "Identidade visual", average: 0.5, participants: [ { value: 1 }, { value: 2 }, { value: 3 } ] }
		],
		
		participants: [
			{ name: "Conta Azul" },
			{ name: "MIT" },
			{ name: "Serasa" }
		]
	}
	*/
}

