package fm.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fm.data.Match;
import fm.data.Team;

@Component("MatchGenerator")
public class MatchGenerator {

	public Map<Integer, List<Match>> generateQueues(List<Team> teamList) {
		Map<Integer, List<Match>> queueMap = new HashMap<>();
		List<Match> tempQueue = new ArrayList<>();

		int firstTeamOdd;
		int secondTeamOdd;
		int firstTeamEven;
		int secondTeamEven;

		for (int day = 1; day < teamList.size(); day++) {
			firstTeamOdd = ((day - 1) / 2);
			secondTeamOdd = teamList.size() - 1;
			firstTeamEven = teamList.size() - 1;
			secondTeamEven = teamList.size() / 2 + ((day - 1) / 2);

			if (day % 2 == 1) {
				for (int odd = 0; odd < teamList.size() / 2; odd++) {
					if (odd == 1) {
						secondTeamOdd = firstTeamOdd - 2;
					}
					if (odd > 0 && secondTeamOdd == -1) {
						secondTeamOdd = teamList.size() - 2;
					}
					tempQueue.add(new Match(teamList.get(firstTeamOdd).getIdTeam(),
							teamList.get(secondTeamOdd).getIdTeam(), (long) day));

					firstTeamOdd++;
					secondTeamOdd--;
				}
			} else {
				for (int even = 0; even < teamList.size() / 2; even++) {
					if (even == 1) {
						firstTeamEven = secondTeamEven + 2;
					}
					if (even > 0 && firstTeamEven == teamList.size() - 1) {
						firstTeamEven = 0;
					}
					tempQueue.add(new Match(teamList.get(firstTeamEven).getIdTeam(),
							teamList.get(secondTeamEven).getIdTeam(), (long) day));

					firstTeamEven++;
					secondTeamEven--;

				}
			}
			queueMap.put(day, tempQueue);
			tempQueue = new ArrayList<>();

		}
		return queueMap;
	}
}
