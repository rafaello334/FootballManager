$(document).ready(function() {

	var parameter = findGetParameter("idTeam");
	$('#cost').load('/getCost?idTeam=' + parameter);
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