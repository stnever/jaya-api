package com.sensedia.jaya.api.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.sensedia.jaya.api.dao.CustomerDAO;
import com.sensedia.jaya.api.dao.OpinionDAO;
import com.sensedia.jaya.api.dao.PainDAO;
import com.sensedia.jaya.api.model.Customer;
import com.sensedia.jaya.api.model.Opinion;
import com.sensedia.jaya.api.model.Pain;
import com.sensedia.jaya.api.utils.AggregateUtils;

@Path("/leaderboard")
public class LeaderboardResource {

	CustomerDAO customerDAO;
	PainDAO painDAO;
	OpinionDAO opinionDAO;

	public LeaderboardResource(CustomerDAO customerDAO, PainDAO painDAO, OpinionDAO opinionDAO) {
		super();
		this.customerDAO = customerDAO;
		this.painDAO = painDAO;
		this.opinionDAO = opinionDAO;
	}

	@GET
	public Leaderboard getLeaderboard(LeaderboardFilter filter) {
		Leaderboard result = new Leaderboard();

		// Filtra por customer ou recupera todos
		if (filter.customerIds == null || filter.customerIds.isEmpty())
			result.participants = customerDAO.findAll();
		else
			for (Long id : filter.customerIds)
				result.participants.add(customerDAO.findById(id));

		// Filtra por pain ou recupera todas
		List<Pain> pains = new ArrayList<Pain>();
		if (filter.painIds == null || filter.painIds.isEmpty())
			pains = painDAO.findAll();
		else
			for (String id : filter.painIds)
				pains.add(painDAO.findById(id));

		// Para cada pain escolhida...
		for (Pain p : pains) {
			BoardPain bp = new BoardPain(p.getTitle(), 0.0);

			// ...e para cada customer escolhido...
			for (Customer c : result.participants) {

				// ...recupera as opiniões.
				List<Opinion> thisPainAndCustomerOpinions = opinionDAO.findByPainAndCustomer(p.getId(), c.getId());
				filterOutUsers(thisPainAndCustomerOpinions, filter);
				Double average = AggregateUtils.avg(thisPainAndCustomerOpinions, "value");
				bp.participants.add(new BoardParticipant(average));
			}

			// média das médias
			bp.average = AggregateUtils.avg(bp.participants, "value");

			result.pains.add(bp);
		}

		// ordena por média decrescente
		Collections.sort(result.pains, new Comparator<BoardPain>() {
			@Override
			public int compare(BoardPain o1, BoardPain o2) {
				return o2.average.compareTo(o1.average);
			}
		});

		return result;
	}

	private void filterOutUsers(List<Opinion> thisPainAndCustomerOpinions, LeaderboardFilter filter) {
		if (filter.userIds == null || filter.userIds.isEmpty())
			return;

		for (Iterator<Opinion> it = thisPainAndCustomerOpinions.iterator(); it.hasNext();) {
			Opinion op = it.next();
			if (!filter.userIds.contains(op.getUserId()))
				it.remove();
		}
	}

	public static class LeaderboardFilter {
		public List<String> painIds;
		public List<Long> customerIds;
		public List<String> userIds;
	}

	public static class Leaderboard {
		public List<BoardPain> pains = new ArrayList<BoardPain>();
		public List<Customer> participants = new ArrayList<Customer>();
	}

	public static class BoardPain {
		public String title;
		public Double average;
		public List<BoardParticipant> participants = new ArrayList<BoardParticipant>();

		public BoardPain(String title, Double average) {
			this.title = title;
			this.average = average;
		}
	}

	public static class BoardParticipant {
		public Double value;

		public Double getValue() {
			return value;
		}

		public BoardParticipant(Double value) {
			this.value = value;
		}
	}
}
