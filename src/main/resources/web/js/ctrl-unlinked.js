function UnlinkedIssuesController( $http, $scope ) {
	var config = {
		params: {
			"jql": "project = AG and status not in ( Cancelado, Fechado, Rejeitado, 'Entrega Parcial' ) and issuetype in ( Épico, Estória )",
			"fields": "issuelinks,summary",
			"maxResults": 200,
		},
		headers: {
			"Authorization": "Basic dXN1YXJpby5jb3JpbmdhOnVzdWFyaW8uY29yaW5nYQ=="
		}
	}
	
	$http.get( "http://192.168.5.114:8080/jira/search", config )
		.success( function(data, status) {
			$scope.issues = data.issues;
			$scope.totalCount = data.issues.length;
			$scope.unlinkedCount = data.issues.length;
			
			angular.forEach( data.issues, function(issue) {
				issue.linkedToPain = false;
				if ( issue.fields.issuelinks ) {
					angular.forEach( issue.fields.issuelinks, function(link) {
						if ( link.inwardIssue ) link.otherIssue = link.inwardIssue;
						if ( link.outwardIssue ) link.otherIssue = link.outwardIssue;
					
						if ( link.otherIssue && link.otherIssue.key.indexOf( "PG" ) != -1 ) {
							issue.linkedToPain = true;
						}
					});
				}
				
				if ( issue.linkedToPain ) $scope.unlinkedCount--;
			});
		})
		.error( function(data, status) { console.log("error"); console.log(status) })
}
