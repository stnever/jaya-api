package com.sensedia.jaya.api.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AggregateResult {

	public static Comparator<AggregateResult> AverageDescendingComparator = new Comparator<AggregateResult>() {
		@Override
		public int compare(AggregateResult o1, AggregateResult o2) {
			if (o1.average != o2.average)
				return o2.average.compareTo(o1.average);

			if (!o1.getPainId().equals(o2.getPainId()))
				return o1.painId.compareTo(o2.painId);

			return o1.customerId.compareTo(o2.customerId);
		}
	};

	private Integer total = 0;
	private Double average = 0.0;
	private Long customerId;
	private String customerName;
	private String painId;
	private String painName;

	private List<Opinion> opinions = new ArrayList<Opinion>();

	public Double getAverage() {
		return average;
	}

	public Integer getTotal() {
		return this.total;
	}

	public Integer getCount() {
		return this.opinions.size();
	}

	public AggregateResult calculateTotal() {
		this.total = 0;
		for (Opinion o : opinions)
			this.total += o.getValue();

		this.average = (double) this.total / opinions.size();
		return this;
	}

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public AggregateResult addOpinion(Opinion opinions) {
		this.opinions.add(opinions);
		return this;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public AggregateResult setCustomerId(Long customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getCustomerName() {
		return customerName;
	}

	public AggregateResult setCustomerName(String customerName) {
		this.customerName = customerName;
		return this;
	}

	public String getPainId() {
		return painId;
	}

	public AggregateResult setPainId(String painId) {
		this.painId = painId;
		return this;
	}

	public String getPainName() {
		return painName;
	}

	public AggregateResult setPainName(String painName) {
		this.painName = painName;
		return this;
	}

}
