$(document).ready(function() {

	var parameter = findGetParameter("idTeam");
	$('#budget').load('/getBudget?idTeam=' + parameter);
});

function findGetParameter(parameterName) {
	var result = null, tmp = [];
	location.search.substr(1).split("&").forEach(function(item) {
		tmp = item.split("=");
		if (tmp[0] === parameterName)
			result = decodeURIComponent(tmp[1]);
	});
	return result;
}